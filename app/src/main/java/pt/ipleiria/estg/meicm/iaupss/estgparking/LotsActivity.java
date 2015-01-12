package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SpacerItemDecoration;
import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.LotsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public class LotsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "LOTS_ACTIVITY";

    private ImageCache imageCache;

    public LotsActivity() {
        super(R.layout.activity_lots, R.id.my_progress_bar, R.id.my_recycler_view, R.menu.menu_parking_lots);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.imageCache = new ImageCache();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            //super.getApp().getDatastoreManager().shutDown();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        super.getApp().getDatastoreManager().shutDown();
        super.onBackPressed();
    }*/

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        List<Lot> lots;

        if (datastore.getSyncStatus().hasIncoming) {
            try {
                datastore.sync();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        } /*else {
            lots = super.getApp().getLotRepository(false).fetchLots();
        }*/

        lots = super.getApp().getLotRepository(true).fetchLots();

        if (lots == null || lots.isEmpty()) {
            Toast.makeText(getBaseContext(), "Não existem lotes de estacionamento", Toast.LENGTH_LONG).show();
            finish();
        }

        // specify adapter
        super.setViewAdapter(new LotsAdapter(lots, this.imageCache));
        super.getRecyclerView().setAdapter(super.getViewAdapter());

        RecyclerView.ItemDecoration itemDecoration = new SpacerItemDecoration(this);
        super.getRecyclerView().addItemDecoration(itemDecoration);
        super.getRecyclerView().setItemAnimator(new DefaultItemAnimator());

        super.getProgressBar().setVisibility(ProgressBar.GONE);
    }
}
