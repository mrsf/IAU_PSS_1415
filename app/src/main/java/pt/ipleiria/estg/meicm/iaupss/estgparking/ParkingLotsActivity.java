package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.ParkingLotAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.task.ImageDownloader;


public class ParkingLotsActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {

    private static final String TAG = "PARKING_LOTS_ACTIVITY";

    private ESTGParkingApplication app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageDownloader imageDownloader;
    private ImageCache imageCache;

    private List<ParkingLot> parkingLots;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lots);

        this.app = ESTGParkingApplication.getInstance();
        this.imageDownloader = new ImageDownloader(imageCache);
        this.imageCache = new ImageCache();

        /*try {
            // criar a base de dados
            this.app.initDatastore();
            DbxDatastore datastore = this.app.getDatastore();
            datastore.setRole(DbxPrincipal.PUBLIC, DbxDatastore.Role.EDITOR);
            datastore.sync();
            // criar tabela e inserir registo
            DbxTable parkingLots = datastore.getTable("parking_lot");
            parkingLots.insert().set("nome", "Lote 1").set("descricao", "À frente do edifício A.")
                    .set("latitude", 38.14043).set("longitude", -8.23675).set("image_path", "https://www.dropbox.com/s/22p8mlxaugrt2ry/lote1.jpg?dl=1");
            datastore.sync();
            parkingLots.insert().set("nome", "Lote 2").set("descricao", "À frente do edifício B.")
                    .set("latitude", 38.14061).set("longitude", -8.23876).set("image_path", "https://www.dropbox.com/s/9pytcrks0em4547/lote2.jpg?dl=1");
            datastore.sync();
            parkingLots.insert().set("nome", "Lote 3").set("descricao", "À frente do edifício C.")
                    .set("latitude", 38.14321).set("longitude", -8.24071).set("image_path", "https://www.dropbox.com/s/9pytcrks0em4547/lote2.jpg?dl=1");
            datastore.sync();
        } catch (DbxException e) {
            e.printStackTrace();
        }*/

        progressBar = (ProgressBar) findViewById(R.id.my_progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


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
    }

    @Override
    public void onResume() {
        super.onResume();
        this.app.initDatastore();
        this.app.getDatastore().addSyncStatusListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.app.getDatastore().removeSyncStatusListener(this);
        this.app.getDatastore().close();
        this.app.getDatastoreManager().shutDown();
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

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {
        if (datastore.getSyncStatus().hasIncoming) {
            try {
                //Map<String, Set<DbxRecord>> changes = this.app.getDatastore().sync();
                this.app.getDatastore().sync();
                // Handle the updated data
            } catch (DbxException e) {
                // Handle exception
                e.printStackTrace();
            }
        }

        this.parkingLots = this.app.getParkingLotRepository().fetchParkingLots();

        // specify an adapter (see also next example)
        mAdapter = new ParkingLotAdapter(parkingLots, imageCache);//myDataset);
        mRecyclerView.setAdapter(mAdapter);

        this.progressBar.setVisibility(ProgressBar.GONE);
    }

}
