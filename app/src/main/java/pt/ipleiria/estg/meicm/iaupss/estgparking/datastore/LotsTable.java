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

public class LotsTable {

    private DbxDatastore datastore;
    private DbxTable table;

    public class LotRecord {

        private DbxRecord record;

        public LotRecord(DbxRecord record) {
            this.record = record;
        }

        public String getId() {
            return record.getId();
        }

        public String getName() {
            return record.getString("name");
        }

        public String getDescription() {
            return record.getString("description");
        }

        public Double getLatitude() {
            return record.getDouble("latitude");
        }

        public Double getLongitude() {
            return record.getDouble("longitude");
        }

        public String getImagePath() {
            return record.getString("image_path");
        }

        public void delete() throws DbxException {
            record.deleteRecord();
            datastore.sync();
        }

    }

    public LotsTable(DbxDatastore datastore) {
        this.datastore = datastore;
        this.table = datastore.getTable("lot");
    }

    public void createLot(String name, String description, double latitude, double longitude, String imagePath) throws DbxException {
        DbxFields lotFields = new DbxFields()
                .set("name", name)
                .set("description", description)
                .set("latitude", latitude)
                .set("longitude", longitude)
                .set("image_path", imagePath);
        table.insert(lotFields);
        datastore.sync();
    }

    public List<LotRecord> getLotsSorted() throws DbxException {
        List<LotRecord> lotsList = new ArrayList<>();
        for (DbxRecord record : table.query()) {
            lotsList.add(new LotRecord(record));
        }
        Collections.sort(lotsList, new Comparator<LotRecord>() {
            @Override
            public int compare(LotRecord lot1, LotRecord lot2) {
                return lot1.getName().compareTo(lot2.getName());
            }
        });
        return lotsList;
    }
}
