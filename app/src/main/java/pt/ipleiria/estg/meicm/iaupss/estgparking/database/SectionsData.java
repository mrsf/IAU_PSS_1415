package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionsData extends ESTGParkingData {

    public static final String[] CREATE_TABLE_SECTION = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.SectionBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.SectionBase.ID
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.NAME
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.DESCRIPTION
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LATITUDE_A
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LONGITUDE_A
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LATITUDE_B
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LONGITUDE_B
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LATITUDE_C
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LONGITUDE_C
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LATITUDE_D
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LONGITUDE_D
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.CAPACITY
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.OCCUPATION
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.SectionBase.LOT_ID
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_SECTION = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.SectionBase.TABLE_NAME };

    public SectionsData(Context context, Boolean dbUpdate) {

        super(context, dbUpdate, CREATE_TABLE_SECTION, DELETE_TABLE_SECTION);
    }

    public void insertSections(List<Section> sections) {

        for (Section section : sections)
            if (section != null)
                if (!this.sectionExist(section.getId()))
                    this.insertSection(section);
    }

    public List<Section> getSections(String lotId) {

        List<Section> sections = new LinkedList<>();
        Cursor cursor = database().rawQuery("SELECT * FROM "
                        + ESTGParkingDBContract.SectionBase.TABLE_NAME + " WHERE "
                        + ESTGParkingDBContract.SectionBase.LOT_ID + " = ? ORDER BY "
                        + ESTGParkingDBContract.SectionBase.NAME,
                        new String[] { lotId });

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    sections.add(new Section(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getDouble(6),
                            cursor.getDouble(7), cursor.getDouble(8), cursor.getDouble(9), cursor.getDouble(10),
                            cursor.getInt(11), cursor.getInt(12), cursor.getString(13)));
                } while (cursor.moveToNext());

            cursor.close();
        }

        return sections;
    }

    private boolean sectionExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.SectionBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.SectionBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() != 0);
                cursor.close();
            }

        return exist;
    }

    private long insertSection(Section section) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.SectionBase.ID, section.getId());
        values.put(ESTGParkingDBContract.SectionBase.NAME, section.getName());
        values.put(ESTGParkingDBContract.SectionBase.DESCRIPTION, section.getDescription());
        values.put(ESTGParkingDBContract.SectionBase.LATITUDE_A, section.getLatitudeA());
        values.put(ESTGParkingDBContract.SectionBase.LONGITUDE_A, section.getLongitudeA());
        values.put(ESTGParkingDBContract.SectionBase.LATITUDE_B, section.getLatitudeB());
        values.put(ESTGParkingDBContract.SectionBase.LONGITUDE_B, section.getLongitudeB());
        values.put(ESTGParkingDBContract.SectionBase.LATITUDE_C, section.getLatitudeC());
        values.put(ESTGParkingDBContract.SectionBase.LONGITUDE_C, section.getLongitudeC());
        values.put(ESTGParkingDBContract.SectionBase.LATITUDE_D, section.getLatitudeD());
        values.put(ESTGParkingDBContract.SectionBase.LONGITUDE_D, section.getLongitudeD());
        values.put(ESTGParkingDBContract.SectionBase.CAPACITY, section.getCapacity());
        values.put(ESTGParkingDBContract.SectionBase.OCCUPATION, section.getOccupation());
        values.put(ESTGParkingDBContract.SectionBase.LOT_ID, section.getLotId());
        return database().insert(ESTGParkingDBContract.SectionBase.TABLE_NAME,
                null, values);
    }
}
