package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingLotsTable;


public class ParkingLotsActivity extends ActionBarActivity {

    private static final String TAG = "PARKING_LOTS_ACTIVITY";

    private static final String APP_KEY = "avpur29bk1xsrye";
    private static final String APP_SECRET = "1d94tetjd15mt1s";
    private static final String APP_TOKEN = "wew0_ddOKZ8AAAAAAAAAHtrH1sQCDYwV9h4hiZIONqsfZI1oPhbgNLf04bpfZ4Ae";

    private static final int REQUEST_LINK_TO_DBX = 0;

    /* Initializing managers vars */
    DbxAccountManager accountManager;
    ESTGParkingApplication app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = ESTGParkingApplication.getInstance();

        setContentView(R.layout.activity_parking_lots);

        accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        // Set up the datastore manager
        if (accountManager.hasLinkedAccount()) {
            try {
                // Use Dropbox datastores
                app.datastoreManager = DbxDatastoreManager.forAccount(accountManager.getLinkedAccount());
            } catch (DbxException.Unauthorized e) {
                System.out.println("Account was unlinked remotely");
            }
        }
        else {//if (app.datastoreManager == null) {
            // Account isn't linked yet, use local datastores
            app.datastoreManager = DbxDatastoreManager.localManager(accountManager);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ParkingLotAdapter(null);//myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parking_lots, menu);
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
