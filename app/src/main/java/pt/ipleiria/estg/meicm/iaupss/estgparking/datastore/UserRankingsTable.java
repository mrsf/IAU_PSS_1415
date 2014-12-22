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

public class UserRankingsTable {

    private DbxDatastore mDatastore;
    private DbxTable mTable;

    public class UserRankingRecord {

        private DbxRecord mRecord;

        public UserRankingRecord(DbxRecord record) {
            mRecord = record;
        }

        public String getId() {
            return mRecord.getId();
        }

        public String getName() {
            return mRecord.getString("name");
        }

        public int getPoints() {
            return (int) mRecord.getLong("points");
        }

        public String getImagePath() {
            return mRecord.getString("image_path");
        }

        public void delete() throws DbxException {
            mRecord.deleteRecord();
            mDatastore.sync();
        }

    }

    public UserRankingsTable(DbxDatastore datastore) {
        mDatastore = datastore;
        mTable = datastore.getTable("user_ranking");
    }

    public void createUserRanking(String name, int points, String imagePath) throws DbxException {
        DbxFields userRankingFields = new DbxFields()
                .set("name", name)
                .set("points", points)
                .set("image_path", imagePath);
        mTable.insert(userRankingFields);
        mDatastore.sync();
    }

    public List<UserRankingRecord> getUserRankingsSorted() throws DbxException {
        List<UserRankingRecord> resultList = new ArrayList<>();
        for (DbxRecord result : mTable.query()) {
            resultList.add(new UserRankingRecord(result));
        }
        Collections.sort(resultList, new Comparator<UserRankingRecord>() {
            @Override
            public int compare(UserRankingRecord r1, UserRankingRecord r2) {
                return (r1.getPoints() < r2.getPoints() ? 0 : 1);
            }
        });
        return resultList;
    }
}
