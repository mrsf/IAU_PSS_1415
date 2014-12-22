package pt.ipleiria.estg.meicm.iaupss.estgparking.datastore;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParkingLotsTable {

    private DbxDatastore mDatastore;
    private DbxTable mTable;

    public class ParkingLotRecord {

        private DbxRecord mRecord;

        public ParkingLotRecord(DbxRecord record) {
            mRecord = record;
        }

        public String getId() {
            return mRecord.getId();
        }

        public String getName() {
            return mRecord.getString("nome");
        }

        public String getDescription() {
            return mRecord.getString("descricao");
        }

        public Double getLatitude() {
            return mRecord.getDouble("latitude");
        }

        public Double getLongitude() {
            return mRecord.getDouble("longitude");
        }

        public String getImagePath() {
            return mRecord.getString("image_path");
        }

        public void delete() throws DbxException {
            mRecord.deleteRecord();
            mDatastore.sync();
        }

    }

    public ParkingLotsTable(DbxDatastore datastore) {
        mDatastore = datastore;
        mTable = datastore.getTable("parking_lot");
    }

    public void createParkingLot(String name, String description, double latitude, double longitude, String imagePath) throws DbxException {
        DbxFields parkingLotFields = new DbxFields()
                .set("nome", name)
                .set("descricao", description)
                .set("latitude", latitude)
                .set("longitude", longitude)
                .set("image_path", imagePath);
        mTable.insert(parkingLotFields);
        mDatastore.sync();
    }

    public List<ParkingLotRecord> getParkingLotsSorted() throws DbxException {
        List<ParkingLotRecord> resultList = new ArrayList<>();
        for (DbxRecord result : mTable.query()) {
            resultList.add(new ParkingLotRecord(result));
        }
        Collections.sort(resultList, new Comparator<ParkingLotRecord>() {
            @Override
            public int compare(ParkingLotRecord l1, ParkingLotRecord l2) {
                return l1.getName().compareTo(l2.getName());
            }
        });
        return resultList;
    }
}
