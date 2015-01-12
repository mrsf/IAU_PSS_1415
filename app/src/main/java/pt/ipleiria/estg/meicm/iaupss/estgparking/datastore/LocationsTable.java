package pt.ipleiria.estg.meicm.iaupss.estgparking.datastore;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.util.ArrayList;
import java.util.List;

public class LocationsTable {

    private DbxDatastore datastore;
    private DbxTable table;

    public class LocationRecord {

        private DbxRecord record;

        public LocationRecord(DbxRecord record) {
            this.record = record;
        }

        public String getId() {
            return record.getId();
        }

        public Double getLatitude() {
            return record.getDouble("latitude");
        }

        public Double getLongitude() {
            return record.getDouble("longitude");
        }

        public int getTime() {
            return (int) record.getLong("time");
        }

        public String getEmail() {
            return record.getString("email");
        }

        public String getSectionId() {
            return record.getString("section_id");
        }

        public void delete() throws DbxException {
            record.deleteRecord();
            datastore.sync();
        }

    }

    public LocationsTable(DbxDatastore datastore) {
        this.datastore = datastore;
        table = datastore.getTable("location");
    }

    public void createLocation(double latitude, double longitude, int time, String email, String sectionId) throws DbxException {
        DbxFields sectionFields = new DbxFields()
                .set("latitude", latitude)
                .set("longitude", longitude)
                .set("time", time)
                .set("email", email)
                .set("section_id", sectionId);
        table.insert(sectionFields);
        datastore.sync();
    }

    public LocationRecord getMyLocation(String email) throws DbxException {
        List<LocationRecord> locationsList = new ArrayList<>();
        DbxFields queryParams = new DbxFields().set("email", email);
        for (DbxRecord record : table.query(queryParams)) {
            locationsList.add(new LocationRecord(record));
        }
        return locationsList.get(0);
    }
}
