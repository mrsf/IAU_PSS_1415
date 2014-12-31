package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Statistic;

public class StatisticsData extends ESTGParkingData {

    public static final String[] CREATE_TABLE_STATISTIC = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.StatisticBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.StatisticBase.ID
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.StatisticBase.WEEK_DAY
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.StatisticBase.HOUR
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.StatisticBase.OCCUPATION
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.StatisticBase.SECTION_ID
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_STATISTIC = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.StatisticBase.TABLE_NAME };

    public StatisticsData(Context context, Boolean dbUpdate) {
        super(context, dbUpdate, CREATE_TABLE_STATISTIC, DELETE_TABLE_STATISTIC);
    }

    public void insertStatistics(List<Statistic> statistics) {

        for (Statistic statistic : statistics)
            if (statistic != null)
                if (!this.statisticExist(statistic.getId()))
                    this.insertStatistic(statistic);
    }

    public List<Statistic> getStatistics(String weekDay, String hour, String sectionId) {

        List<Statistic> statistics = new LinkedList<>();
        Cursor cursor = database().rawQuery("SELECT * FROM "
                        + ESTGParkingDBContract.StatisticBase.TABLE_NAME + " WHERE "
                        + ESTGParkingDBContract.StatisticBase.WEEK_DAY + " = ? AND "
                        + ESTGParkingDBContract.StatisticBase.HOUR + " = ? AND "
                        + ESTGParkingDBContract.StatisticBase.SECTION_ID + " = ?",
                new String[] { weekDay, hour, sectionId });

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    statistics.add(new Statistic(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                                    cursor.getInt(3), cursor.getString(4)));
                } while (cursor.moveToNext());

            cursor.close();
        }

        return statistics;
    }

    private boolean statisticExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.StatisticBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.StatisticBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() != 0);
                cursor.close();
            }

        return exist;
    }

    private long insertStatistic(Statistic statistic) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.StatisticBase.ID, statistic.getId());
        values.put(ESTGParkingDBContract.StatisticBase.WEEK_DAY, statistic.getWeekDay());
        values.put(ESTGParkingDBContract.StatisticBase.HOUR, statistic.getHour());
        values.put(ESTGParkingDBContract.StatisticBase.OCCUPATION, statistic.getOccupation());
        values.put(ESTGParkingDBContract.StatisticBase.SECTION_ID, statistic.getSectionId());
        return database().insert(ESTGParkingDBContract.StatisticBase.TABLE_NAME,
                null, values);
    }
}
