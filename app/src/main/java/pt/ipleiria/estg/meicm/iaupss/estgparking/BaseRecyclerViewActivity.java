package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dropbox.sync.android.DbxDatastore;

public abstract class BaseRecyclerViewActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {

    private ESTGParkingApplication app;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter viewAdapter;

    private FrameLayout progressFrameLayout;

    private int layoutResID;
    private int progressFrameLayoutID;
    private int recyclerViewID;
    private int menuResID;

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

            progressFrameLayout = (FrameLayout) findViewById(this.progressFrameLayoutID);
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
        /*if (this.app.getDatastoreManager().isShutDown()) {
            try {
                this.app.setDatastoreManager(DbxDatastoreManager.forAccount(this.app.getAccountManager().getLinkedAccount()));
            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }
        }*/
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
}
