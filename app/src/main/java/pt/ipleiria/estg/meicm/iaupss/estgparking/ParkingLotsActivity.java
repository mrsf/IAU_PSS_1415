package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;


public class ParkingLotsActivity extends ActionBarActivity {

    private static final String APP_KEY = "avpur29bk1xsrye";
    private static final String APP_SECRET = "1d94tetjd15mt1s";
    static final int REQUEST_LINK_TO_DBX = 0;  // This value is up to you

    /* Initializing managers vars */
    private Button mLinkButton;
    private DbxAccountManager mAccountManager;
    private DbxDatastoreManager mDatastoreManager;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lots);

        // Set up the account manager
        mAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        // Button to link to Dropbox
        /*mLinkButton = (Button) findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.startLink((ActionBarActivity)ParkingLotsActivity.this, REQUEST_LINK_TO_DBX);
            }
        });*/

        // Set up the datastore manager
        if (mAccountManager.hasLinkedAccount()) {
            try {
                // Use Dropbox datastores
                mDatastoreManager = DbxDatastoreManager.forAccount(mAccountManager.getLinkedAccount());
                // Hide link button
                mLinkButton.setVisibility(View.GONE);
            } catch (DbxException.Unauthorized e) {
                System.out.println("Account was unlinked remotely");
            }
        }
        if (mDatastoreManager == null) {
            // Account isn't linked yet, use local datastores
            mDatastoreManager = DbxDatastoreManager.localManager(mAccountManager);
            // Show link button
            mLinkButton.setVisibility(View.VISIBLE);
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

                /* Manage DataStore Records*/
        try {
            mAccountManager.startLink((ActionBarActivity)ParkingLotsActivity.this, REQUEST_LINK_TO_DBX);
            DbxDatastore datastore = mDatastoreManager.openDefaultDatastore();
            DbxTable parkingLotsTable = datastore.getTable("ParkingLots");
            DbxRecord parkingLotRecord = parkingLotsTable.insert().set("nome", "Lote 1").
                    set("descricao", "Em frente ao edificio D").
                    set("latitude", 32.00).set("longitude", -8.00);
            datastore.sync();
        } catch (DbxException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = mAccountManager.getLinkedAccount();
                try {
                    // Migrate any local datastores to the linked account
                    mDatastoreManager.migrateToAccount(account);
                    // Now use Dropbox datastores
                    mDatastoreManager = DbxDatastoreManager.forAccount(account);
                    // Hide link button
                    mLinkButton.setVisibility(View.GONE);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                // Link failed or was cancelled by the user
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
