package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxAuthActivity;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxPrincipal;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingLotsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;


public class ParkingLotsActivity extends ActionBarActivity {

    /*private static final String TAG = "PARKING_LOTS_ACTIVITY";

    private static final String APP_KEY = "2t0sxcmlop91dal";
    private static final String APP_SECRET = "biel5w67o22tinj";
    private static final String APP_TOKEN = "wew0_ddOKZ8AAAAAAAAAIWrCMolyHAs-3Xuuhdmgmbr_cPpqO_6pzdUZxvgPhn0l";

    private static final int REQUEST_LINK_TO_DBX = 0;

    // Initializing managers vars
    DbxAccountManager accountManager;*/
    private ESTGParkingApplication app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lots);

        this.app = ESTGParkingApplication.getInstance();

        List<ParkingLot> parkingLots = this.app.getParkingLotRepository().fetchParkingLots();


        //try {
            /* criar a base de dados */
            /*DbxDatastore datastore = this.app.getDatastoreManager().openOrCreateDatastore(".XKY75But_i5IprjZEcRGSc0V1tPq5iuO3-XBq4KJa7c");
            datastore.setRole(DbxPrincipal.PUBLIC, DbxDatastore.Role.EDITOR);
            datastore.sync();*/

            //DbxDatastore datastore = this.app.getDatastoreManager().openDatastore(".XKY75But_i5IprjZEcRGSc0V1tPq5iuO3-XBq4KJa7c");

            /* criar tabela e inserir registo */
            /*DbxTable parkingLots = datastore.getTable("parking_lots");
            DbxRecord parkingLot = parkingLots.insert().set("nome", "Lote 2").set("descricao", "A frente do edificio D")
                    .set("latitude", 38.14043).set("longitude", -8.23675);
            datastore.sync();*/
        /*} catch (DbxException e) {
            e.printStackTrace();
        }*/

        /*LinkedList<ParkingLot> parkingLotList = new LinkedList<ParkingLot>();
        for(Iterator<DbxRecord> result = records.iterator(); result.hasNext();) {
            ParkingLot pLot = new ParkingLot(result.next());
            parkingLotList.add(pLot);
            Log.i("TESTE", pLot.getName() + " | " + pLot.getDescription() + " | " + pLot.getLatitude() + " | " + pLot.getLongitude());
        }

        /*this.app.onCreate();
        this.app = ESTGParkingApplication.getInstance();

        setContentView(R.layout.activity_parking_lots);

        accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        accountManager.startLink(ParkingLotsActivity.this, REQUEST_LINK_TO_DBX);

        // Set up the datastore manager
        if (accountManager.hasLinkedAccount()) {
            try {
                // Use Dropbox datastores
                this.app.datastoreManager = DbxDatastoreManager.forAccount(accountManager.getLinkedAccount());
            } catch (DbxException.Unauthorized e) {
                System.out.println("Account was unlinked remotely");
            }
        }
        if (this.app.datastoreManager == null) {
            // Account isn't linked yet, use local datastores
            this.app.datastoreManager = DbxDatastoreManager.localManager(accountManager);
        }*/

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ParkingLotAdapter(parkingLots);//myDataset);
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = accountManager.getLinkedAccount();
                try {
                    // Migrate any local datastores to the linked account
                    this.app.datastoreManager.migrateToAccount(account);
                    // Now use Dropbox datastores
                    this.app.datastoreManager = DbxDatastoreManager.forAccount(account);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                // Link failed or was cancelled by the user
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

}
