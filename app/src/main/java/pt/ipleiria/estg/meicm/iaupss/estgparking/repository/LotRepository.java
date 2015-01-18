package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.LotsData;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.LotsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.LotsTable.LotRecord;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public class LotRepository implements ILotRepository {

    private static final String TAG = "LOT_REPOSITORY";

    private DbxDatastore datastore;
    private LotsData lotsData;
    private boolean update;

    public LotRepository(Context context, DbxDatastore datastore, boolean update) {

        this.datastore = datastore;
        this.update = update;
        //this.lotsData = new LotsData(context, true);
    }

    @Override
    public List<Lot> fetchLots() {

        /*if (update) {
            Log.wtf("VER", "DATASTORE");
            return this.dataStoreLots();
        } else {
            Log.wtf("VER", "DATABASE");
            List<Lot> lots = this.dataBaseLots();
            if (lots.size() > 0)
                return this.dataBaseLots();*/
            //else
                return this.dataStoreLots();
        //}
    }

    @Override
    public String findLot(double lat, double lng) {

        String lotId = null;

        try {
            this.datastore.sync();
            LotsTable lotsTable = new LotsTable(datastore);
            lotId = lotsTable.getLotIdForLocation(lat, lng);
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return lotId;
    }

    private List<Lot> dataStoreLots() {

        List<LotRecord> lotRecords = new ArrayList<>();

        try {
            this.datastore.sync();
            LotsTable lotsTable = new LotsTable(datastore);
            lotRecords.addAll(lotsTable.getLotsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<Lot> lots = new LinkedList<>();

        for(LotRecord lotRecord: lotRecords) {

            Lot lot = new Lot();

            lot.setId(lotRecord.getId());
            lot.setName(lotRecord.getName());
            lot.setDescription(lotRecord.getDescription());
            lot.setLatitudeA(lotRecord.getLatitudeA());
            lot.setLongitudeA(lotRecord.getLongitudeA());
            lot.setLatitudeB(lotRecord.getLatitudeB());
            lot.setLongitudeB(lotRecord.getLongitudeB());
            lot.setLatitudeC(lotRecord.getLatitudeC());
            lot.setLongitudeC(lotRecord.getLongitudeC());
            lot.setLatitudeD(lotRecord.getLatitudeD());
            lot.setLongitudeD(lotRecord.getLongitudeD());
            lot.setImagePath(lotRecord.getImagePath());

            lots.add(lot);

            Log.d(TAG, "Add: " + lot.getId() + " | " + lot.getName() + " | " + lot.getDescription() +
                    " | " + lot.getImagePath());
        }

        /*this.lotsData.open();
        this.lotsData.insertLots(lots);
        this.lotsData.close();*/

        return lots;
    }

    private List<Lot> dataBaseLots() {

        this.lotsData.open();
        List<Lot> lots = this.lotsData.getLots();
        this.lotsData.close();

        return lots;
    }
}
