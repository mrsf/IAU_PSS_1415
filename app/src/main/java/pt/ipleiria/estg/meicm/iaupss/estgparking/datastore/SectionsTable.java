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

        public Double getLatitudeX() {
            return record.getDouble("latitude_x");
        }

        public Double getLongitudeX() {
            return record.getDouble("longitude_x");
        }

        public Double getLatitudeY() {
            return record.getDouble("latitude_y");
        }

        public Double getLongitudeY() {
            return record.getDouble("longitude_y");
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

    public void createSection(String name, String description, double latitudeX, double longitudeX, double latitudeY, double longitudeY, int capacity, int occupation, String lotId) throws DbxException {
        DbxFields sectionFields = new DbxFields()
                .set("name", name)
                .set("description", description)
                .set("latitude_x", latitudeX)
                .set("longitude_x", longitudeX)
                .set("latitude_y", latitudeY)
                .set("longitude_y", longitudeY)
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
}
