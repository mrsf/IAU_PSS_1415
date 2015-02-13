package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SpacerItemDecoration;

public abstract class BaseRecyclerViewActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {

    private static final String TAG = "RECYCLERVIEW_ACTIVITY";

    private ESTGParkingApplication app;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter viewAdapter;
    private FrameLayout progressFrameLayout;
    private int layoutResID;
    private int progressFrameLayoutID;
    private int recyclerViewID;
    private int menuResID;
    private boolean isConnected;
    private boolean isWiFi;
    private boolean isUpdated;
    private boolean isDataLoaded;

    private long time, startTime;

    public BaseRecyclerViewActivity(int layoutResID, int progressFrameLayoutID, int recyclerViewID, int menuResID) {

        this.layoutResID = layoutResID;
        this.progressFrameLayoutID = progressFrameLayoutID;
        this.recyclerViewID = recyclerViewID;
        this.menuResID = menuResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.layoutResID);

        if (savedInstanceState == null) {

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);

            this.app = ESTGParkingApplication.getInstance();

            this.time = 0;
            this.startTime = 0;

            progressFrameLayout = (FrameLayout) findViewById(this.progressFrameLayoutID);
            recyclerView = (RecyclerView) findViewById(this.recyclerViewID);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            this.isUpdated = false;
            this.isDataLoaded = false;

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            this.isConnected = activeNetwork != null && activeNetwork.isConnected();

            this.isWiFi = this.isConnected && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        this.app.initDatastore();
        try {
            this.app.getDatastore().addSyncStatusListener(this);
            this.startTime = System.nanoTime();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), "Problema na conexão com o dropbox.", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            this.app.getDatastore().removeSyncStatusListener(this);
            this.app.getDatastore().close();
            this.app.setDatastore(null);
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (!this.app.getDatastoreManager().isShutDown())
                this.app.getDatastoreManager().shutDown();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(this.menuResID, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateDatastore() {
        try {
            this.app.getDatastore().sync();
            this.isUpdated = false;
            this.isDataLoaded = false;
            this.progressFrameLayout.setVisibility(FrameLayout.VISIBLE);
        } catch (DbxException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void updateIfWifi(DbxDatastore datastore) {
        if (this.isWiFi && datastore.getSyncStatus().hasIncoming)
            this.updateDatastore();
        else if (!datastore.getSyncStatus().hasIncoming) {
            this.isUpdated = true;
        }
    }

    public void specifyAdapter(RecyclerView.Adapter viewAdapter) {
        this.viewAdapter = viewAdapter;
        this.recyclerView.setAdapter(this.viewAdapter);

        RecyclerView.ItemDecoration itemDecoration = new SpacerItemDecoration(this);
        this.recyclerView.addItemDecoration(itemDecoration);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.progressFrameLayout.setVisibility(FrameLayout.GONE);

        this.isUpdated = true;
        this.isDataLoaded = true;

        if (startTime != 0) {
            time = System.nanoTime() - startTime;
            Log.d(TAG, "Datastore Sync: " + (time / 1000000) + " milliseconds");
            startTime = 0;
        }
    }

    public ESTGParkingApplication getApp() {
        return app;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.Adapter getViewAdapter() {
        return viewAdapter;
    }

    public void setViewAdapter(RecyclerView.Adapter viewAdapter) {
        this.viewAdapter = viewAdapter;
    }

    public FrameLayout getProgressFrameLayout() {
        return progressFrameLayout;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
