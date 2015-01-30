package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
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
    private boolean isConnected;
    private boolean isWiFi;
    private boolean isUpdated;

    public SectionsActivity() {
        super(R.layout.activity_sections, R.id.sections_progress_frame_layout, R.id.my_sections_recycler_view, R.menu.menu_sections);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            if (this.lotId == null) {
                Intent i = getIntent();
                this.lotId = i.getStringExtra("LotId");
            }

            this.isUpdated = false;

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnected();

            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                finish();
                return true;
            case R.id.action_refresh_sections:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                    if (super.getApp().getDatastore().getSyncStatus().hasIncoming) {
                        try {
                            super.getApp().getDatastore().sync();
                            isUpdated = false;
                            super.getProgressFrameLayout().setVisibility(FrameLayout.VISIBLE);
                        } catch (DbxException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "As secções estão atualizadas", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        Log.d(TAG, datastore.getSyncStatus().toString());

        if (!isWiFi && datastore.getSyncStatus().hasIncoming) {
            try {
                datastore.sync();
                isUpdated = false;
            } catch (DbxException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        if (!datastore.getSyncStatus().isDownloading && !isUpdated) {
            List<Section> sections;
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

            super.getProgressFrameLayout().setVisibility(FrameLayout.GONE);

            this.isUpdated = true;
        }
    }
}
