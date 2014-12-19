package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

/**
 * Created by francisco on 19-12-2014.
 */
public class SectionsRepository extends ESTGParkingRepository {

    public static final String[] CREATE_TABLE_SECTION = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.SectionBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.SectionBase.NAME
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.DESCRIPTION
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LATITUDE
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LONGITUDE
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.CAPACITY
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.OCCUPATION
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LOT_ID
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_SECTION = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.SectionBase.TABLE_NAME };

    public SectionsRepository(Context context, Boolean dbUpdate) {

        super(context, dbUpdate, CREATE_TABLE_SECTION, DELETE_TABLE_SECTION);
    }

    public void insertSections(List<ParkingSection> sections) {

        for (ParkingSection section : sections)
            if (section != null)
                if (!this.sectionExist(section.getName()))
                    this.insertSection(section);
    }

    private boolean sectionExist(String name) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.SectionBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.SectionBase.NAME
                        + " = ?", new String[] { String.valueOf(name) });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() == 0 ? false : true);
                cursor.close();
            }

        return exist;
    }

    private long insertSection(ParkingSection section) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.SectionBase.NAME, section.getName());
        values.put(ESTGParkingDBContract.SectionBase.DESCRIPTION, section.getDescription());
        values.put(ESTGParkingDBContract.SectionBase.LATITUDE, section.getLatitude());
        values.put(ESTGParkingDBContract.SectionBase.LONGITUDE, section.getLongitude());
        values.put(ESTGParkingDBContract.SectionBase.CAPACITY, section.getCapacity());
        values.put(ESTGParkingDBContract.SectionBase.OCCUPATION, section.getOccupation());
        values.put(ESTGParkingDBContract.SectionBase.LOT_ID, section.getLotId());
        return database().insert(ESTGParkingDBContract.SectionBase.TABLE_NAME,
                null, values);
    }
}
