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

    public RankingRepository(Context context, DbxDatastore datastore) {

        this.datastore = datastore;
        //this.rankingsData = new RankingsData(context, false);
    }

    @Override
    public List<Ranking> fetchRankings() {

        return this.dataStoreRankings();
    }

    @Override
    public List<Ranking> fetchMyRanking(String email) {

        List<RankingRecord> rankingRecords = new ArrayList<>();

        try {
            this.datastore.sync();
            RankingsTable rankingsTable = new RankingsTable(datastore);
            rankingRecords.add(rankingsTable.getMyRanking(email));
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<Ranking> rankings = new LinkedList<>();

        for(RankingRecord rankingRecord: rankingRecords) {

            Ranking ranking = new Ranking();

            ranking.setId(rankingRecord.getId());
            ranking.setName(rankingRecord.getName());
            ranking.setEmail(rankingRecord.getEmail());
            ranking.setScore(rankingRecord.getScore());
            ranking.setImagePath(rankingRecord.getImagePath());

            rankings.add(ranking);

            Log.d(TAG, "Add: " + ranking.getId() + " | " + ranking.getName() + " | " + ranking.getEmail()
                    + " | " + ranking.getScore() + " | " + ranking.getImagePath());
        }

        /*this.rankingsData.open();
        this.rankingsData.insertRankings(rankings);
        this.rankingsData.close();*/

        return rankings;
    }

    private List<Ranking> dataStoreRankings() {

        List<RankingRecord> rankingRecords = new ArrayList<>();

        try {
            this.datastore.sync();
            RankingsTable rankingsTable = new RankingsTable(datastore);
            rankingRecords.addAll(rankingsTable.getRankingsSorted());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<Ranking> rankings = new LinkedList<>();

        for(RankingRecord rankingRecord: rankingRecords) {

            Ranking ranking = new Ranking();

            ranking.setId(rankingRecord.getId());
            ranking.setName(rankingRecord.getName());
            ranking.setEmail(rankingRecord.getEmail());
            ranking.setScore(rankingRecord.getScore());
            ranking.setImagePath(rankingRecord.getImagePath());

            rankings.add(ranking);

            Log.d(TAG, "Add: " + ranking.getId() + " | " + ranking.getName() + " | " + ranking.getEmail()
                    + " | " + ranking.getScore() + " | " + ranking.getImagePath());
        }

        /*this.rankingsData.open();
        this.rankingsData.insertRankings(rankings);
        this.rankingsData.close();*/

        return rankings;
    }

    private List<Ranking> dataBaseRankings() {

        this.rankingsData.open();
        List<Ranking> rankings = this.rankingsData.getRankings();
        this.rankingsData.close();

        return rankings;
    }
}
