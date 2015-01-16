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
import com.dropbox.sync.android.DbxRecord;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SpacerItemDecoration;
import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.LotsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.LotsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.SectionsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Dot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Rectangle;

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

        /*try {
            super.getApp().initDatastore();
            super.getApp().getDatastore().sync();
            SectionsTable sectionsTable = new SectionsTable(super.getApp().getDatastore());*/
            /*sectionsTable.createSection("Secção 1", "Situada no lote 1-1", 39.734404, -8.821291, 39.733818, -8.820693, 39.733789, -8.820734, 39.734367, -8.821350, 32, 12, "TmQUKcdHQwEp7XtOZbIFjw");
            sectionsTable.createSection("Secção 2", "Situada no lote 1-2", 39.734336, -8.821364, 39.733748, -8.820768, 39.733711, -8.820806, 39.734301, -8.821428, 32, 20, "TmQUKcdHQwEp7XtOZbIFjw");
            sectionsTable.createSection("Secção 3", "Situada no lote 1-3", 39.734301, -8.821428, 39.733711, -8.820806, 39.733697, -8.820870, 39.734268, -8.821460, 32, 31, "TmQUKcdHQwEp7XtOZbIFjw");
            sectionsTable.createSection("Secção 4", "Situada no lote 1-4", 39.734251, -8.821506, 39.733655, -8.820900, 39.733631, -8.820943, 39.734216, -8.821552, 32, 8, "TmQUKcdHQwEp7XtOZbIFjw");*/
            /*sectionsTable.createSection("Secção 1", "Situada no lote 2-1", 39.734208, -8.821565, 39.733618, -8.820964, 39.733589, -8.820999, 39.734179, -8.821613, 32, 17, "Q7y_Ky-lq79aChvLceKEZg");
            sectionsTable.createSection("Secção 2", "Situada no lote 2-2", 39.734163, -8.821648, 39.733573, -8.821053, 39.733540, -8.821098, 39.734123, -8.821696, 32, 24, "Q7y_Ky-lq79aChvLceKEZg");
            sectionsTable.createSection("Secção 3", "Situada no lote 2-3", 39.734123, -8.821696, 39.733540, -8.821098, 39.733509, -8.821136, 39.734092, -8.821747, 32, 5, "Q7y_Ky-lq79aChvLceKEZg");
            sectionsTable.createSection("Secção 4", "Situada no lote 2-4", 39.734092, -8.821747, 39.733498, -8.821197, 39.733467, -8.821243, 39.734070, -8.821863, 32, 18, "Q7y_Ky-lq79aChvLceKEZg");*/
            /*LotsTable lotsTable = new LotsTable(super.getApp().getDatastore());
            lotsTable.createLot("Lote 1", "Em frente ao edificio D.", 39.734472, -8.821348, 39.733754, -8.820624, 39.733587, -8.820905, 39.734287, -8.821629, "https://www.dropbox.com/s/22p8mlxaugrt2ry/lote1.jpg?dl=1");
            lotsTable.createLot("Lote 2", "Em frente à cantina 2.", 39.734287, -8.821629, 39.733587, -8.820905, 39.733422, -8.821195, 39.734117, -8.821922, "https://www.dropbox.com/s/9pytcrks0em4547/lote2.jpg?dl=1");
            lotsTable.createLot("Lote 3", "Em frente ao edificio A.", 39.734978, -8.820983, 39.735402, -8.820264, 39.735235, -8.820058, 39.734806, -8.820771, "https://www.dropbox.com/s/xltmp9buebn1ugq/lote3.jpg?dl=1");*/
            /*Log.i("TESTE", lotsTable.getLotIdForLocation(39.734273, -8.820901));*/
            /*for(DbxRecord record : super.getApp().getDatastore().getTable("section").query()) {
                record.deleteRecord();
                super.getApp().getDatastore().sync();
            }*/
        /*} catch (DbxException e) {
            e.printStackTrace();
        }*/

//        Rectangle rectangle = new Rectangle();
//        rectangle.setDotA(new Dot(39.734484, -8.821324));
//        rectangle.setDotB(new Dot(39.733798, -8.820560));
//        rectangle.setDotC(new Dot(39.733560, -8.820874));
//        rectangle.setDotD(new Dot(39.734286, -8.821641));
//        Log.i("TESTE", String.valueOf(rectangle.dotIsInside(new Dot(39.734347, -8.821465))));
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
