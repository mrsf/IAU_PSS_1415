package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SectionsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "SECTIONS_ACTIVITY";

    private String lotId;

    public SectionsActivity() {
        super(R.layout.activity_sections, R.id.sections_progress_frame_layout, R.id.my_sections_recycler_view, R.menu.menu_sections);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            if (this.lotId == null) {
                Intent i = getIntent();
                this.lotId = i.getStringExtra("LotId");
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh_sections:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                    if (super.getApp().getDatastore().getSyncStatus().hasIncoming) {
                        super.updateDatastore();
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

        if (!datastore.getSyncStatus().needsReset && datastore.getSyncStatus().isConnected && super.isConnected()) {

            super.updateIfWifi(datastore);

            if (!datastore.getSyncStatus().isDownloading && !super.isUpdated()) {
                List<Section> sections = super.getApp().getSectionRepository(this.lotId).fetchSections();

                if (sections == null || sections.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Não existem Secções", Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                }

                // specify adapter
                super.specifyAdapter(new SectionsAdapter(sections));
            }

        } else {
            Toast.makeText(getApplicationContext(), "Problema na conexão com o dropbox.", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}
