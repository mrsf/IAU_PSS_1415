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
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.RankingsTable.RankingRecord;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingRepository implements IRankingRepository {

    private static final String TAG = "RANKING_REPOSITORY";

    private DbxDatastore datastore;
    private RankingsData rankingsData;
    private boolean isUpdate;

    public RankingRepository(Context context, DbxDatastore datastore, boolean isUpdate) {

        this.datastore = datastore;
        this.isUpdate = isUpdate;
        this.rankingsData = new RankingsData(context, isUpdate);
    }

    @Override
    public List<Ranking> fetchRankings() {

        if (isUpdate || this.dataBaseRankings().isEmpty()) {
            Log.d(TAG, "DATASTORE");
            return this.dataStoreRankings();
        } else {
            Log.d(TAG, "DATABASE");
            return this.dataBaseRankings();
        }
    }

    private List<Ranking> dataStoreRankings() {

        List<RankingRecord> rankingRecords = new ArrayList<>();

        try {
            this.datastore.sync();
            RankingsTable rankingsTable = new RankingsTable(datastore);
            rankingRecords.addAll(rankingsTable.getRankingsSorted());
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        List<Ranking> rankings = new LinkedList<>();
        int position = 0;
        int topScore = 0;

        for(RankingRecord rankingRecord: rankingRecords) {

            position += 1;
            Ranking ranking = new Ranking();

            if (position == 1)
                topScore = rankingRecord.getScore();

            ranking.setPosition(position);
            ranking.setId(rankingRecord.getId());
            ranking.setName(rankingRecord.getName());
            ranking.setEmail(rankingRecord.getEmail());
            ranking.setScore(rankingRecord.getScore());
            ranking.setImagePath(rankingRecord.getImagePath());
            ranking.setTopScore(topScore);

            rankings.add(ranking);

            Log.d(TAG, "Add: " + ranking.getId() + " | " + ranking.getName() + " | " + ranking.getEmail()
                    + " | " + ranking.getScore() + " | " + ranking.getImagePath());
        }

        this.rankingsData.open();
        this.rankingsData.insertRankings(rankings);
        this.rankingsData.close();

        return rankings;
    }

    private List<Ranking> dataBaseRankings() {

        try {
            this.datastore.sync();
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        this.rankingsData.open();
        List<Ranking> rankings = this.rankingsData.getRankings();
        this.rankingsData.close();

        return rankings;
    }
}
