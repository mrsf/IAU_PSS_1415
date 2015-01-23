package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.RankingsTable;

public class DropboxActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {

    private static final String TAG = "DROPBOX_ACTIVITY";

    private ESTGParkingApplication app;

    private DbxAccountManager.AccountListener accountListener = new DbxAccountManager.AccountListener() {

        @Override
        public void onLinkedAccountChange(DbxAccountManager mgr, DbxAccount acct) {
            // Set up the datastore manager
            if (mgr.hasLinkedAccount()) {
                try {
                    // Use Dropbox datastores
                    app.setDatastoreManager(DbxDatastoreManager.forAccount(mgr.getLinkedAccount()));
                    CreateUserRanking();
                    // show menu activity
                } catch (DbxException.Unauthorized e) {
                    Log.e(TAG, "Account was unlinked remotely: ", e);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                // Account isn't linked yet, use local datastores
                app.setDatastoreManager(DbxDatastoreManager.localManager(mgr));
                // Show link button
                getFragmentManager().beginTransaction().replace(R.id.container, new DropboxFragment(mgr, app.getDatastoreManager())).commitAllowingStateLoss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);

        this.app = ESTGParkingApplication.getInstance();
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
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        this.app.getAccountManager().addListener(accountListener);
        this.accountListener.onLinkedAccountChange(app.getAccountManager(), app.getAccountManager().getLinkedAccount());

        try {
            if (this.app.getDatastoreManager().isShutDown()) {
                try {
                    this.app.setDatastoreManager(DbxDatastoreManager.forAccount(this.app.getAccountManager().getLinkedAccount()));
                } catch (DbxException.Unauthorized unauthorized) {
                    unauthorized.printStackTrace();
                }
            }
            this.app.initDatastore();
            this.app.getDatastore().addSyncStatusListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
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
        //System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dropbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateUserRanking() {
        try {
            // criar a base de dados
            this.app.initDatastore();

            /*for(DbxRecord record : this.app.getDatastore().getTable("ranking").query()) {
                record.deleteRecord();
                this.app.getDatastore().sync();
            }*/

            // criar tabela e inserir registo
            RankingsTable rankings = new RankingsTable(this.app.getDatastore());
            rankings.createRanking(this.app.getUserInfoProvider().getName(), this.app.getUserInfoProvider().getEmail(), 5, this.app.getUserInfoProvider().getPhotoURL());
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        if (datastore.getSyncStatus().isConnected) {
            if (!(datastore.getSyncStatus().hasOutgoing || datastore.getSyncStatus().hasIncoming)) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                try {
                    datastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
