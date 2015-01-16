package pt.ipleiria.estg.meicm.iaupss.estgparking.datastore;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.google.android.gms.maps.model.Polygon;

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

        public Double getLatitudeA() {
            return record.getDouble("latitude_a");
        }

        public Double getLongitudeA() {
            return record.getDouble("longitude_a");
        }

        public Double getLatitudeB() {
            return record.getDouble("latitude_b");
        }

        public Double getLongitudeB() {
            return record.getDouble("longitude_b");
        }

        public Double getLatitudeC() {
            return record.getDouble("latitude_c");
        }

        public Double getLongitudeC() {
            return record.getDouble("longitude_c");
        }

        public Double getLatitudeD() {
            return record.getDouble("latitude_d");
        }

        public Double getLongitudeD() {
            return record.getDouble("longitude_d");
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

    public void createLot(String name, String description, double latitudeA, double longitudeA, double latitudeB, double longitudeB, double latitudeC, double longitudeC, double latitudeD, double longitudeD, String imagePath) throws DbxException {
        DbxFields lotFields = new DbxFields()
                .set("name", name)
                .set("description", description)
                .set("latitude_a", latitudeA)
                .set("longitude_a", longitudeA)
                .set("latitude_b", latitudeB)
                .set("longitude_b", longitudeB)
                .set("latitude_c", latitudeC)
                .set("longitude_c", longitudeC)
                .set("latitude_d", latitudeD)
                .set("longitude_d", longitudeD)
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

    public String getLotIdForLocation(double lat, double lng) throws DbxException {
        String lotId = null;

        for (DbxRecord record : table.query()) {
            if (lat <= record.getDouble("latitude_x") && lng >= record.getDouble("longitude_x") && lat >= record.getDouble("latitude_y") && lng <= record.getDouble("longitude_y")) {
                lotId = record.getId();
                break;
            }
        }
        return lotId;
    }
}
