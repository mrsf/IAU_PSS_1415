package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.DividerItemDecoration;

public class BaseRecyclerViewActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {

    private ESTGParkingApplication app;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter viewAdapter;

    private ProgressBar progressBar;

    private int layoutResID;
    private int progressBarID;
    private int recyclerViewID;
    private int menuResID;

    public BaseRecyclerViewActivity(int layoutResID, int progressBarID, int recyclerViewID, int menuResID) {

        this.layoutResID = layoutResID;
        this.progressBarID = progressBarID;
        this.recyclerViewID = recyclerViewID;
        this.menuResID = menuResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.layoutResID);

        if (savedInstanceState == null) {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            this.app = ESTGParkingApplication.getInstance();

            progressBar = (ProgressBar) findViewById(this.progressBarID);
            recyclerView = (RecyclerView) findViewById(this.recyclerViewID);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }
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
        this.app.setDatastore(null);
    }

    @Override
    public void onBackPressed() {
        finish();
        //System.exit(0);
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

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
