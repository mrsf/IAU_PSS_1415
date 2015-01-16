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

import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Dot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Rectangle;

public class SectionsTable {

    private DbxDatastore datastore;
    private DbxTable table;

    public class SectionRecord {

        private DbxRecord record;

        public SectionRecord(DbxRecord record) {
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

        public int getCapacity() {
            return (int) record.getLong("capacity");
        }

        public int getOccupation() {
            return (int) record.getLong("occupation");
        }

        public String getLotId() {
            return record.getString("lot_id");
        }

        public void delete() throws DbxException {
            record.deleteRecord();
            datastore.sync();
        }

    }

    public SectionsTable(DbxDatastore datastore) {
        this.datastore = datastore;
        table = datastore.getTable("section");
    }

    public void createSection(String name, String description, double latitudeA, double longitudeA, double latitudeB, double longitudeB, double latitudeC, double longitudeC, double latitudeD, double longitudeD, int capacity, int occupation, String lotId) throws DbxException {
        DbxFields sectionFields = new DbxFields()
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
                .set("capacity", capacity)
                .set("occupation", occupation)
                .set("lot_id", lotId);
        table.insert(sectionFields);
        datastore.sync();
    }

    public List<SectionRecord> getSectionsSorted(String lotId) throws DbxException {
        List<SectionRecord> sectionsList = new ArrayList<>();
        DbxFields queryParams = new DbxFields().set("lot_id", lotId);
        for (DbxRecord record : table.query(queryParams)) {
            sectionsList.add(new SectionRecord(record));
        }
        Collections.sort(sectionsList, new Comparator<SectionRecord>() {
            @Override
            public int compare(SectionRecord section1, SectionRecord section2) {
                return section1.getName().compareTo(section2.getName());
            }
        });
        return sectionsList;
    }

    public boolean occupationIncrement(double lat, double lng) throws DbxException {

        boolean isIncremented = false;

        for (DbxRecord record : table.query()) {

            Rectangle rectangle = new Rectangle();
            rectangle.setDotA(new Dot(record.getDouble("latitude_a"), record.getDouble("longitude_a")));
            rectangle.setDotB(new Dot(record.getDouble("latitude_b"), record.getDouble("longitude_b")));
            rectangle.setDotC(new Dot(record.getDouble("latitude_c"), record.getDouble("longitude_c")));
            rectangle.setDotD(new Dot(record.getDouble("latitude_d"), record.getDouble("longitude_d")));

            if (rectangle.dotIsInside(new Dot(lat, lng))) {
                if (record.getLong("occupation") < record.getLong("capacity")) {
                    record.set("occupation", (record.getLong("occupation") + 1));
                    isIncremented = true;
                }
                break;
            }
        }

        return isIncremented;
    }

    public boolean occupationDecrement(double lat, double lng) throws DbxException {

        boolean isDecremented = false;

        for (DbxRecord record : table.query()) {

            Rectangle rectangle = new Rectangle();
            rectangle.setDotA(new Dot(record.getDouble("latitude_a"), record.getDouble("longitude_a")));
            rectangle.setDotB(new Dot(record.getDouble("latitude_b"), record.getDouble("longitude_b")));
            rectangle.setDotC(new Dot(record.getDouble("latitude_c"), record.getDouble("longitude_c")));
            rectangle.setDotD(new Dot(record.getDouble("latitude_d"), record.getDouble("longitude_d")));

            if (rectangle.dotIsInside(new Dot(lat, lng))) {
                if (record.getLong("occupation") > 0) {
                    record.set("occupation", (record.getLong("occupation") - 1));
                    isDecremented = true;
                }
                break;
            }
        }

        return isDecremented;
    }
}
