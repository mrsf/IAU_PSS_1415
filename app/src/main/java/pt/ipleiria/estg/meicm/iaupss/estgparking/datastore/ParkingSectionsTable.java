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

/**
 * Created by francisco on 19-12-2014.
 */
public class ParkingSectionsTable {

    private DbxDatastore mDatastore;
    private DbxTable mTable;

    public class ParkingSectionRecord {

        private DbxRecord mRecord;

        public ParkingSectionRecord(DbxRecord record) {
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

        public int getCapacity() {
            return (int) mRecord.getLong("lotacao");
        }

        public double getOccupation() {
            return mRecord.getDouble("ocupacao");
        }

        public String getLotId() {
            return  mRecord.getString("id_lote");
        }

        public void delete() throws DbxException {
            mRecord.deleteRecord();
            mDatastore.sync();
        }

    }

    public ParkingSectionsTable(DbxDatastore datastore) {
        mDatastore = datastore;
        mTable = datastore.getTable("parking_section");
    }

    public void createParkingSection(String name, String description, double latitude, double longitude, int capacity, double occupation, String lotId) throws DbxException {
        DbxFields parkingSectionFields = new DbxFields()
                .set("nome", name)
                .set("descricao", description)
                .set("latitude", latitude)
                .set("longitude", longitude)
                .set("lotacao", capacity)
                .set("ocupacao", occupation)
                .set("id_lote", lotId);
        mTable.insert(parkingSectionFields);
        mDatastore.sync();
    }

    public List<ParkingSectionRecord> getParkingSectionsSorted(String lotId) throws DbxException {
        List<ParkingSectionRecord> resultList = new ArrayList<ParkingSectionRecord>();
        DbxFields queryParams = new DbxFields().set("id_lote", lotId);
        for (DbxRecord result : mTable.query(queryParams)) {
            resultList.add(new ParkingSectionRecord(result));
        }
        Collections.sort(resultList, new Comparator<ParkingSectionRecord>() {
            @Override
            public int compare(ParkingSectionRecord o1, ParkingSectionRecord o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return resultList;
    }
}
