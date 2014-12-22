package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.dropbox.sync.android.DbxDatastore;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.ParkingLotAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

//public class ParkingLotsActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {
public class ParkingLotsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "PARKING_LOTS_ACTIVITY";

    /*private ESTGParkingApplication app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;*/

    private ImageCache imageCache;

    /*private List<ParkingLot> parkingLots;
    private ProgressBar progressBar;*/

    public ParkingLotsActivity() {
        super(R.layout.activity_parking_lots, R.id.my_progress_bar, R.id.my_recycler_view, R.menu.menu_parking_lots);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.imageCache = new ImageCache();

        /*setContentView(R.layout.activity_parking_lots);

        if (savedInstanceState == null) {

            this.app = ESTGParkingApplication.getInstance();
            this.imageDownloader = new ImageDownloader(imageCache);
            this.imageCache = new ImageCache();

            progressBar = (ProgressBar) findViewById(R.id.my_progress_bar);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }*/
    }

    /*@Override
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
        this.app.setDatastore(null);
        this.app.getDatastoreManager().shutDown();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
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
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            super.getApp().getDatastoreManager().shutDown();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.getApp().getDatastoreManager().shutDown();
        super.onBackPressed();
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        super.onDatastoreStatusChange(datastore);
        /*if (datastore.getSyncStatus().hasIncoming) {
            try {
                //Map<String, Set<DbxRecord>> changes = this.app.getDatastore().sync();
                this.app.getDatastore().sync();
                // Handle the updated data
            } catch (DbxException e) {
                // Handle exception
                e.printStackTrace();
            }
        }*/

        List<ParkingLot> parkingLots = super.getApp().getParkingLotRepository().fetchParkingLots();

        // specify adapter
        super.setViewAdapter(new ParkingLotAdapter(parkingLots, this.imageCache));
        super.getRecyclerView().setAdapter(super.getViewAdapter());

        super.getProgressBar().setVisibility(ProgressBar.GONE);
        /*this.parkingLots = this.app.getParkingLotRepository().fetchParkingLots();

        // specify an adapter (see also next example)
        mAdapter = new ParkingLotAdapter(parkingLots, imageCache);//myDataset);
        mRecyclerView.setAdapter(mAdapter);

        this.progressBar.setVisibility(ProgressBar.GONE);*/
    }

}
