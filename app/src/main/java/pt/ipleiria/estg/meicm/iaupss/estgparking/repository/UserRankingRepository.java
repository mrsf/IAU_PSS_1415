package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.RankingsRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.UserRankingsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.UserRanking;

public class UserRankingRepository implements IUserRankingRepository {

    private static final String TAG = "FETCH_USER_RANKING";

    private DbxDatastore datastore;
    private RankingsRepository rankingsRepository;

    public UserRankingRepository(Context context, DbxDatastore datastore) {

        this.datastore = datastore;
        this.rankingsRepository = new RankingsRepository(context, false);
    }

    @Override
    public List<UserRanking> fetchUserRankings() {

        List<UserRankingsTable.UserRankingRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            UserRankingsTable rankingsTable = new UserRankingsTable(datastore);
            records.addAll(rankingsTable.getUserRankingsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<UserRanking> userRankings = new LinkedList<>();

        for(UserRankingsTable.UserRankingRecord record: records) {
            UserRanking uRanking = new UserRanking();
            uRanking.setId(record.getId());
            uRanking.setName(record.getName());
            uRanking.setPoints(record.getPoints());
            uRanking.setImagePath(record.getImagePath());

            userRankings.add(uRanking);
            Log.d(TAG, "Add: " + uRanking.getId() + " | " + uRanking.getName() + " | " + uRanking.getPoints() + " | " + uRanking.getImagePath());
        }

        return userRankings;
    }

    private List<UserRanking> dataStoreUserRankings() {

        List<UserRankingsTable.UserRankingRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            UserRankingsTable rankingsTable = new UserRankingsTable(datastore);
            records.addAll(rankingsTable.getUserRankingsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<UserRanking> userRankings = new LinkedList<>();

        for(UserRankingsTable.UserRankingRecord record: records) {
            UserRanking uRanking = new UserRanking();
            uRanking.setId(record.getId());
            uRanking.setName(record.getName());
            uRanking.setPoints(record.getPoints());
            uRanking.setImagePath(record.getImagePath());

            userRankings.add(uRanking);
            Log.d(TAG, "Add: " + uRanking.getId() + " | " + uRanking.getName() + " | " + uRanking.getPoints() + " | " + uRanking.getImagePath());
        }

        this.rankingsRepository.open();
        this.rankingsRepository.insertRankings(userRankings);
        this.rankingsRepository.close();

        return userRankings;
    }

    private List<UserRanking> dataBaseUserRankings() {

        this.rankingsRepository.open();
        List<UserRanking> userRankings = this.rankingsRepository.getRankings();
        this.rankingsRepository.close();

        return userRankings;
    }
}
