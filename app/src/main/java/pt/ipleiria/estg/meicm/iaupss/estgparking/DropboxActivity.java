package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.RankingsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.profile.IUserInfoProvider;

public class DropboxActivity extends FragmentActivity implements DbxDatastore.SyncStatusListener {

    private static final String TAG = "DROPBOX_ACTIVITY";

    private ESTGParkingApplication app;

    private FrameLayout containerFrameLayout;
    private FrameLayout progressFrameLayout;

    private DbxAccountManager.AccountListener accountListener = new DbxAccountManager.AccountListener() {

        @Override
        public void onLinkedAccountChange(DbxAccountManager mgr, DbxAccount acct) {
            // Set up the datastore manager
            if (mgr.hasLinkedAccount()) {
                try {
                    // Use Dropbox datastores
                    app.setDatastoreManager(DbxDatastoreManager.forAccount(mgr.getLinkedAccount()));
                } catch (DbxException.Unauthorized e) {
                    Log.e(TAG, "Account was unlinked remotely: ", e);
                }
            } else {
                // Account isn't linked yet, use local datastores
                app.setDatastoreManager(DbxDatastoreManager.localManager(mgr));
                // Show link button
                getFragmentManager().beginTransaction().replace(containerFrameLayout.getId(), new DropboxFragment(mgr, app.getDatastoreManager())).commitAllowingStateLoss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);

        this.app = ESTGParkingApplication.getInstance();

        this.containerFrameLayout = (FrameLayout) findViewById(R.id.dropbox_container_frame_layout);
        this.progressFrameLayout = (FrameLayout) findViewById(R.id.dropbox_progress_frame_layout);
    }

    @Override
    public void onPause() {

        super.onPause();

        this.app.getAccountManager().removeListener(accountListener);

        try {
            this.app.getDatastore().removeSyncStatusListener(this);
            this.app.getDatastore().close();
            this.app.setDatastore(null);
        } catch (NullPointerException e) {
            Log.e(TAG, "Datastore is not initialized: ", e);
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        this.app.getAccountManager().addListener(accountListener);
        this.accountListener.onLinkedAccountChange(app.getAccountManager(), app.getAccountManager().getLinkedAccount());

        try {
            if (this.app.getDatastoreManager().isShutDown()) {
                this.app.setDatastoreManager(DbxDatastoreManager.forAccount(this.app.getAccountManager().getLinkedAccount()));
            }
            this.app.initDatastore();
            this.app.getDatastore().addSyncStatusListener(this);
        } catch (NullPointerException e) {
            Log.e(TAG, "Datastore is not initialized: ", e);
        } catch (DbxException.Unauthorized unauthorized) {
            Log.e(TAG, "Account was unlinked remotely: ", unauthorized);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!this.app.getDatastoreManager().isShutDown())
            this.app.getDatastoreManager().shutDown();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void CreateUserRanking() {

        IUserInfoProvider userInfoProvider = app.getUserInfoProvider();

        try {
            // inicializa a base de dados
            this.app.initDatastore();

            // criar tabela e inserir registo
            RankingsTable rankings = new RankingsTable(this.app.getDatastore());
            rankings.createRanking(userInfoProvider.getName(), userInfoProvider.getEmail(), 5, userInfoProvider.getPhotoURL());
        } catch (DbxException e) {
            Log.e(TAG, "Communication with datastore failed: ", e);
        }
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        if (datastore.getSyncStatus().isConnected) {
            try {
                datastore.sync();
                this.containerFrameLayout.setVisibility(FrameLayout.GONE);
                this.progressFrameLayout.setVisibility(FrameLayout.VISIBLE);
                if (datastore.getSyncStatus().hasOutgoing || datastore.getSyncStatus().hasIncoming) {
                    datastore.sync();
                } else {
                    CreateUserRanking();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            } catch (DbxException e) {
                this.progressFrameLayout.setVisibility(FrameLayout.GONE);
                this.containerFrameLayout.setVisibility(FrameLayout.VISIBLE);
                Log.e(TAG, "Communication with datastore failed: ", e);
            }
        }
    }
}
