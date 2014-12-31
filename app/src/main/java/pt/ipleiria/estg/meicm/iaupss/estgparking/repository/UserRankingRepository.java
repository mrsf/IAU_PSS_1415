package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.RankingsData;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.RankingsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class UserRankingRepository implements IUserRankingRepository {

    private static final String TAG = "FETCH_USER_RANKING";

    private DbxDatastore datastore;
    private RankingsData rankingsRepository;

    public UserRankingRepository(Context context, DbxDatastore datastore) {

        this.datastore = datastore;
        this.rankingsRepository = new RankingsData(context, false);
    }

    @Override
    public List<Ranking> fetchUserRankings() {

        List<RankingsTable.RankingRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            RankingsTable rankingsTable = new RankingsTable(datastore);
            records.addAll(rankingsTable.getRankingsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<Ranking> rankings = new LinkedList<>();

        for(RankingsTable.RankingRecord record: records) {
            Ranking uRanking = new Ranking();
            uRanking.setId(record.getId());
            uRanking.setName(record.getName());
            uRanking.setScore(record.getScore());
            uRanking.setImagePath(record.getImagePath());

            rankings.add(uRanking);
            Log.d(TAG, "Add: " + uRanking.getId() + " | " + uRanking.getName() + " | " + uRanking.getScore() + " | " + uRanking.getImagePath());
        }

        return rankings;
    }

    private List<Ranking> dataStoreUserRankings() {

        List<RankingsTable.RankingRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            RankingsTable rankingsTable = new RankingsTable(datastore);
            records.addAll(rankingsTable.getRankingsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<Ranking> rankings = new LinkedList<>();

        for(RankingsTable.RankingRecord record: records) {
            Ranking uRanking = new Ranking();
            uRanking.setId(record.getId());
            uRanking.setName(record.getName());
            uRanking.setScore(record.getScore());
            uRanking.setImagePath(record.getImagePath());

            rankings.add(uRanking);
            Log.d(TAG, "Add: " + uRanking.getId() + " | " + uRanking.getName() + " | " + uRanking.getScore() + " | " + uRanking.getImagePath());
        }

        this.rankingsRepository.open();
        this.rankingsRepository.insertRankings(rankings);
        this.rankingsRepository.close();

        return rankings;
    }

    private List<Ranking> dataBaseUserRankings() {

        this.rankingsRepository.open();
        List<Ranking> rankings = this.rankingsRepository.getRankings();
        this.rankingsRepository.close();

        return rankings;
    }
}
