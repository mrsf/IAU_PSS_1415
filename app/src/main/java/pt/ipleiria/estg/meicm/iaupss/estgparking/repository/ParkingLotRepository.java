package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingLotsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

/**
 * Created by francisco on 05-12-2014.
 */
public class ParkingLotRepository implements IParkingLotRepository {

    private DbxDatastore datastore;

    public ParkingLotRepository(DbxDatastore datastore) {

        this.datastore = datastore;
    }


    @Override
    public List<ParkingLot> fetchParkingLots() {

        List<ParkingLotsTable.ParkingLotRecord> records = new ArrayList<ParkingLotsTable.ParkingLotRecord>();
        try {
            datastore.sync();
            ParkingLotsTable lotsTable = new ParkingLotsTable(datastore);
            records.addAll(lotsTable.getParkingLotsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        LinkedList<ParkingLot> parkingLotList = new LinkedList<ParkingLot>();

        for(ParkingLotsTable.ParkingLotRecord record: records) {
            ParkingLot pLot = new ParkingLot();
            pLot.setName(record.getName());
            pLot.setDescription(record.getDescription());
            pLot.setLatitude(record.getLatitude());
            pLot.setLongitude(record.getLongitude());
            pLot.setImagePath(record.getImagePath());

            parkingLotList.add(pLot);
            Log.i("TESTE", pLot.getName() + " | " + pLot.getDescription() + " | " + pLot.getLatitude() + " | " + pLot.getLongitude());
        }

        return parkingLotList;
    }
}
