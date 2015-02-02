package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.LotsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public class LotsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "LOTS_ACTIVITY";

    public LotsActivity() {
        super(R.layout.activity_lots, R.id.lots_progress_frame_layout, R.id.my_recycler_view, R.menu.menu_lots);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh_lots:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                        if (super.getApp().getDatastore().getSyncStatus().hasIncoming) {
                            super.updateDatastore();
                        } else
                            Toast.makeText(getApplicationContext(), "Os lotes estão atualizados", Toast.LENGTH_SHORT).show();
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
                List<Lot> lots = super.getApp().getLotRepository(true).fetchLots();

                if (lots == null || lots.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Não existem lotes de estacionamento", Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                }

                // specify adapter
                super.specifyAdapter(new LotsAdapter(lots, super.getApp().getImageCache()));
            }

        } else {
            Toast.makeText(getApplicationContext(), "Problema na conexão com o dropbox.", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}
