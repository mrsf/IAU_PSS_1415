package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.LotsRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingLotsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

public class ParkingLotRepository implements IParkingLotRepository {

    private static final String TAG = "FETCH_PARKING_LOT";

    private DbxDatastore datastore;
    private LotsRepository lotsRepository;

    public ParkingLotRepository(Context context, DbxDatastore datastore) {

        this.datastore = datastore;
        this.lotsRepository = new LotsRepository(context, false);
    }

    @Override
    public List<ParkingLot> fetchParkingLots() {

        List<ParkingLotsTable.ParkingLotRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            ParkingLotsTable lotsTable = new ParkingLotsTable(datastore);
            records.addAll(lotsTable.getParkingLotsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<ParkingLot> parkingLots = new LinkedList<>();

        for(ParkingLotsTable.ParkingLotRecord record: records) {
            ParkingLot pLot = new ParkingLot();
            pLot.setId(record.getId());
            pLot.setName(record.getName());
            pLot.setDescription(record.getDescription());
            pLot.setLatitude(record.getLatitude());
            pLot.setLongitude(record.getLongitude());
            pLot.setImagePath(record.getImagePath());

            parkingLots.add(pLot);
            Log.d(TAG, "Add: " + pLot.getId() + " | " + pLot.getName() + " | " + pLot.getDescription()
                       + " | " + pLot.getLatitude() + " | " + pLot.getLongitude() + " | " + pLot.getImagePath());
        }

        this.lotsRepository.open();
        this.lotsRepository.insertLots(parkingLots);
        this.lotsRepository.close();

        return parkingLots;
    }

    private List<ParkingLot> dataStoreParkingLots() {

        List<ParkingLotsTable.ParkingLotRecord> records = new ArrayList<>();

        try {
            datastore.sync();
            ParkingLotsTable lotsTable = new ParkingLotsTable(datastore);
            records.addAll(lotsTable.getParkingLotsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<ParkingLot> parkingLots = new LinkedList<>();

        for(ParkingLotsTable.ParkingLotRecord record: records) {

            ParkingLot pLot = new ParkingLot();

            pLot.setId(record.getId());
            pLot.setName(record.getName());
            pLot.setDescription(record.getDescription());
            pLot.setLatitude(record.getLatitude());
            pLot.setLongitude(record.getLongitude());
            pLot.setImagePath(record.getImagePath());

            parkingLots.add(pLot);

            Log.d(TAG, "Add: " + pLot.getId() + " | " + pLot.getName() + " | " + pLot.getDescription()
                    + " | " + pLot.getLatitude() + " | " + pLot.getLongitude() + " | " + pLot.getImagePath());
        }

        this.lotsRepository.open();
        this.lotsRepository.insertLots(parkingLots);
        this.lotsRepository.close();

        return parkingLots;
    }

    private List<ParkingLot> dataBaseParkingLots() {

        this.lotsRepository.open();
        List<ParkingLot> parkingLots = this.lotsRepository.getLots();
        this.lotsRepository.close();

        return parkingLots;
    }
}
