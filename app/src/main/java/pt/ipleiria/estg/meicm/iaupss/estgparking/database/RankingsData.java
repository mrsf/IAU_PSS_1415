package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingsData extends ESTGParkingData {

    public static final String[] CREATE_TABLE_RANKING = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.RankingBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.RankingBase.ID
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.RankingBase.NAME
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.RankingBase.EMAIL
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.RankingBase.SCORE
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.RankingBase.IMAGE_PATH
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_RANKING = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.RankingBase.TABLE_NAME };

    public RankingsData(Context context, Boolean dbUpdate) {
        super(context, dbUpdate, CREATE_TABLE_RANKING, DELETE_TABLE_RANKING);
    }

    public void insertRankings(List<Ranking> rankings) {

        for (Ranking ranking : rankings)
            if (ranking != null)
                if (!this.rankingExist(ranking.getId()))
                    this.insertRanking(ranking);
    }

    public List<Ranking> getRankings() {

        List<Ranking> rankings = new LinkedList<>();
        Cursor cursor = database().query(
                ESTGParkingDBContract.RankingBase.TABLE_NAME,
                new String[] { ESTGParkingDBContract.RankingBase.ID,
                        ESTGParkingDBContract.RankingBase.NAME,
                        ESTGParkingDBContract.RankingBase.EMAIL,
                        ESTGParkingDBContract.RankingBase.SCORE,
                        ESTGParkingDBContract.RankingBase.IMAGE_PATH }, null, null,
                null, null, ESTGParkingDBContract.RankingBase.SCORE);

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    rankings.add(new Ranking(1, cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                                cursor.getInt(3), cursor.getString(4)));
                } while (cursor.moveToNext());

            cursor.close();
        }

        return rankings;
    }

    public Ranking getMyRanking(String email) {

        Ranking ranking = new Ranking();
        Cursor cursor = database().query(
                ESTGParkingDBContract.RankingBase.TABLE_NAME,
                new String[] { ESTGParkingDBContract.RankingBase.ID,
                        ESTGParkingDBContract.RankingBase.NAME,
                        ESTGParkingDBContract.RankingBase.EMAIL,
                        ESTGParkingDBContract.RankingBase.SCORE,
                        ESTGParkingDBContract.RankingBase.IMAGE_PATH }, null, null,
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ranking.setId(cursor.getString(0));
                ranking.setName(cursor.getString(1));
                ranking.setEmail(cursor.getString(2));
                ranking.setScore(cursor.getInt(3));
                ranking.setImagePath(cursor.getString(4));
            }

            cursor.close();
        }

        return ranking;
    }

    private boolean rankingExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.RankingBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.RankingBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() != 0);
                cursor.close();
            }

        return exist;
    }

    private long insertRanking(Ranking ranking) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.RankingBase.ID, ranking.getId());
        values.put(ESTGParkingDBContract.RankingBase.NAME, ranking.getName());
        values.put(ESTGParkingDBContract.RankingBase.EMAIL, ranking.getEmail());
        values.put(ESTGParkingDBContract.RankingBase.SCORE, ranking.getScore());
        values.put(ESTGParkingDBContract.RankingBase.IMAGE_PATH, ranking.getImagePath());
        return database().insert(ESTGParkingDBContract.RankingBase.TABLE_NAME,
                null, values);
    }
}
