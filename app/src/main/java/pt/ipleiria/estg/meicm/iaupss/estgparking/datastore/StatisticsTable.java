package pt.ipleiria.estg.meicm.iaupss.estgparking.datastore;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsTable {

    private DbxDatastore datastore;
    private DbxTable table;

    public class StatisticRecord {

        private DbxRecord record;

        public StatisticRecord(DbxRecord record) {
            this.record = record;
        }

        public String getId() {
            return record.getId();
        }

        public String getWeekDay() {
            return record.getString("weekday");
        }

        public Date getHour() {
            return record.getDate("hour");
        }

        public int getOccupation() {
            return (int) record.getLong("occupation");
        }

        public String getSectionId() {
            return record.getString("section_id");
        }

        public void delete() throws DbxException {
            record.deleteRecord();
            datastore.sync();
        }

    }

    public StatisticsTable(DbxDatastore datastore) {
        this.datastore = datastore;
        table = datastore.getTable("statistic");
    }

    public void createStatistic(String weekDay, Date hour, int occupation, String sectionId) throws DbxException {
        DbxFields statisticFields = new DbxFields()
                .set("weekday", weekDay)
                .set("hour", hour)
                .set("occupation", occupation)
                .set("section_id", sectionId);
        table.insert(statisticFields);
        datastore.sync();
    }

    public double getOccupationStatistic(String weekDay, Date hour, String sectionId) throws DbxException {
        double occupation = 0.0;
        List<StatisticRecord> statisticsList = new ArrayList<>();
        DbxFields queryParams = new DbxFields().set("weekday", weekDay).set("hour", hour).set("section_id", sectionId);
        for (DbxRecord record : table.query(queryParams)) {
            statisticsList.add(new StatisticRecord(record));
            occupation += record.getLong("occupation");
        }
        return occupation / statisticsList.size();
    }
}
