package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
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
import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SectionsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "SECTIONS_ACTIVITY";

    private String lotId;

    public SectionsActivity() {
        super(R.layout.activity_sections, R.id.my_sections_progress_bar, R.id.my_sections_recycler_view, R.menu.menu_parking_sections);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.lotId == null) {
            Intent i = getIntent();
            this.lotId = i.getStringExtra("LotId");
        }
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
            super.onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        List<Section> sections;

        if (datastore.getSyncStatus().hasIncoming) {
            try {
                datastore.sync();
            } catch (DbxException e) {
                e.printStackTrace();
            }

        } /*else {
            sections = super.getApp().getSectionRepository(this.lotId).fetchSections();
        }*/

        sections = super.getApp().getSectionRepository(this.lotId).fetchSections();

        if (sections == null || sections.isEmpty()) {
            Toast.makeText(getBaseContext(), "Não existem Secções", Toast.LENGTH_LONG).show();
            finish();
        }

        // specify adapter
        super.setViewAdapter(new SectionsAdapter(sections));
        super.getRecyclerView().setAdapter(super.getViewAdapter());

        RecyclerView.ItemDecoration itemDecoration = new SpacerItemDecoration(this);
        super.getRecyclerView().addItemDecoration(itemDecoration);
        super.getRecyclerView().setItemAnimator(new DefaultItemAnimator());

        super.getProgressBar().setVisibility(ProgressBar.GONE);
    }
}
