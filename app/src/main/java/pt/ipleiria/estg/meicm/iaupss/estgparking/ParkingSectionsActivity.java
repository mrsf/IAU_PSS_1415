package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.dropbox.sync.android.DbxDatastore;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.ParkingSectionAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;


// public class ParkingSectionsActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {
public class ParkingSectionsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "PARKING_SECTIONS_ACTIVITY";

    /*private ESTGParkingApplication app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<ParkingSection> parkingSections;
    private ProgressBar progressBar;*/

    private String lotId;

    public ParkingSectionsActivity() {
        super(R.layout.activity_parking_sections, R.id.my_sections_progress_bar, R.id.my_sections_recycler_view, R.menu.menu_parking_sections);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.lotId == null) {
            Intent i = getIntent();
            this.lotId = i.getStringExtra("LotId");
        }
        /*setContentView(R.layout.activity_parking_sections);

        if (savedInstanceState == null) {

            this.app = ESTGParkingApplication.getInstance();

            Intent i = getIntent();
            this.lotId = i.getStringExtra("lotId");

            progressBar = (ProgressBar) findViewById(R.id.my_sections_progress_bar);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_sections_recycler_view);

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
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parking_sections, menu);
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

        List<ParkingSection> parkingSections = super.getApp().getParkingSectionRepository(this.lotId).fetchParkingSections();

        // specify adapter
        super.setViewAdapter(new ParkingSectionAdapter(parkingSections));
        super.getRecyclerView().setAdapter(super.getViewAdapter());

        super.getProgressBar().setVisibility(ProgressBar.GONE);
        /*this.parkingSections = this.app.getParkingSectionRepository(lotId).fetchParkingSections();

        // specify an adapter (see also next example)
        mAdapter = new ParkingSectionAdapter(parkingSections);//myDataset);
        mRecyclerView.setAdapter(mAdapter);

        this.progressBar.setVisibility(ProgressBar.GONE);*/
    }
}
