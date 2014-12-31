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

public class RankingsTable {

    private DbxDatastore datastore;
    private DbxTable table;

    public class RankingRecord {

        private DbxRecord record;

        public RankingRecord(DbxRecord record) {
            this.record = record;
        }

        public String getId() {
            return record.getId();
        }

        public String getName() {
            return record.getString("name");
        }

        public String getEmail() {
            return record.getString("email");
        }

        public int getScore() {
            return (int) record.getLong("score");
        }

        public String getImagePath() {
            return record.getString("image_path");
        }

        public void delete() throws DbxException {
            record.deleteRecord();
            datastore.sync();
        }

    }

    public RankingsTable(DbxDatastore datastore) {
        this.datastore = datastore;
        table = datastore.getTable("ranking");
    }

    public void createRanking(String name, String email, int score, String imagePath) throws DbxException {
        DbxFields rankingFields = new DbxFields()
                .set("name", name)
                .set("email", email)
                .set("score", score)
                .set("image_path", imagePath);
        table.insert(rankingFields);
        datastore.sync();
    }

    public List<RankingRecord> getRankingsSorted() throws DbxException {
        List<RankingRecord> rankingsList = new ArrayList<>();
        for (DbxRecord record : table.query()) {
            rankingsList.add(new RankingRecord(record));
        }
        Collections.sort(rankingsList, new Comparator<RankingRecord>() {
            @Override
            public int compare(RankingRecord ranking1, RankingRecord ranking2) {
                return (ranking1.getScore() < ranking2.getScore() ? 0 : 1);
            }
        });
        return rankingsList;
    }

    public RankingRecord getMyRanking(String email) throws DbxException {
        List<RankingRecord> rankingsList = new ArrayList<>();
        DbxFields queryParams = new DbxFields().set("email", email);
        for (DbxRecord record : table.query(queryParams)) {
            rankingsList.add(new RankingRecord(record));
        }
        return rankingsList.get(0);
    }
}
