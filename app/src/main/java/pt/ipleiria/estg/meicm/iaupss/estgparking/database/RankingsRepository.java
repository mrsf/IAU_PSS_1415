package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.UserRanking;

public class RankingsRepository extends ESTGParkingRepository {

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
                    + ESTGParkingDBContract.RankingBase.POINTS
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.RankingBase.IMAGE_PATH
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_RANKING = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.RankingBase.TABLE_NAME };

    public RankingsRepository(Context context, Boolean dbUpdate) {
        super(context, dbUpdate, CREATE_TABLE_RANKING, DELETE_TABLE_RANKING);
    }

    public void insertRankings(List<UserRanking> rankings) {

        for (UserRanking ranking : rankings)
            if (ranking != null)
                if (!this.rankingExist(ranking.getId()))
                    this.insertRanking(ranking);
    }

    public List<UserRanking> getRankings() {

        List<UserRanking> rankings = new LinkedList<>();
        Cursor cursor = database().query(
                ESTGParkingDBContract.RankingBase.TABLE_NAME,
                new String[] { ESTGParkingDBContract.RankingBase.ID,
                        ESTGParkingDBContract.RankingBase.NAME,
                        ESTGParkingDBContract.RankingBase.POINTS,
                        ESTGParkingDBContract.RankingBase.IMAGE_PATH }, null, null,
                null, null, ESTGParkingDBContract.RankingBase.NAME);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if (cursor.moveToFirst()) {
            do {
                rankings.add(new UserRanking(cursor.getString(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return rankings;
    }

    private boolean rankingExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.RankingBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.RankingBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() == 0 ? false : true);
                cursor.close();
            }

        return exist;
    }

    private long insertRanking(UserRanking ranking) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.RankingBase.ID, ranking.getId());
        values.put(ESTGParkingDBContract.RankingBase.NAME, ranking.getName());
        values.put(ESTGParkingDBContract.RankingBase.POINTS, ranking.getPoints());
        values.put(ESTGParkingDBContract.RankingBase.IMAGE_PATH, ranking.getImagePath());
        return database().insert(ESTGParkingDBContract.RankingBase.TABLE_NAME,
                null, values);
    }
}
