package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

public class DropboxActivity extends ActionBarActivity {

    private static final String TAG = "DROPBOX_ACTIVITY";
    private static int REQUEST_LINK_TO_DBX = 0;

    private ESTGParkingApplication app;

    private DbxAccountManager.AccountListener accountListener = new DbxAccountManager.AccountListener() {

        @Override
        public void onLinkedAccountChange(DbxAccountManager mgr, DbxAccount acct) {

            if (mgr.hasLinkedAccount()) {
                // When the user is logged in, show the game.
                try {
                    // Use Dropbox datastores
                    app.setDatastoreManager(DbxDatastoreManager.forAccount(mgr.getLinkedAccount()));
                    Intent i = new Intent(getBaseContext(), ParkingLotsActivity.class);
                    startActivity(i);
                    finish();
                } catch (DbxException.Unauthorized e) {
                    Log.e(TAG, "Account was unlinked remotely");
                }
            } else {
                // Account isn't linked yet, use local datastores
                app.setDatastoreManager(DbxDatastoreManager.localManager(mgr));
                // When the user is not logged in, show the login screen.
                getFragmentManager().beginTransaction().replace(R.id.container, new DropboxFragment(mgr)).commitAllowingStateLoss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);

        this.app = ESTGParkingApplication.getInstance();

        /*if (savedInstanceState == null) {
            this.accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);
        }*/
    }

    @Override
    public void onPause() {

        super.onPause();

        this.app.getAccountManager().addListener(accountListener);
    }

    @Override
    public void onResume() {

        super.onResume();

        this.app.getAccountManager().addListener(accountListener);
        this.accountListener.onLinkedAccountChange(app.getAccountManager(), app.getAccountManager().getLinkedAccount());
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
}
