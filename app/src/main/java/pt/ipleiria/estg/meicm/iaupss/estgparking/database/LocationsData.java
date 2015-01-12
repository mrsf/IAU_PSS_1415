package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Location;

public class LocationsData extends ESTGParkingData {

    public static final String[] CREATE_TABLE_LOCATION = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.LocationBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.LocationBase.ID
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LocationBase.LATITUDE
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LocationBase.LONGITUDE
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LocationBase.TIME
                    + ESTGParkingDBContract.INTEGER_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LocationBase.EMAIL
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LocationBase.SECTION_ID
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_LOCATION = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.LocationBase.TABLE_NAME };

    public LocationsData(Context context, Boolean dbUpdate) {
        super(context, dbUpdate, CREATE_TABLE_LOCATION, DELETE_TABLE_LOCATION);
    }

    public void insertMyLocation(Location location) {

            if (location != null)
                if (!this.locationExist(location.getId()))
                    this.insertLocation(location);
    }

    public Location getMyLocation(String email) {

        Location location = new Location();
        Cursor cursor = database().query(
                ESTGParkingDBContract.LocationBase.TABLE_NAME,
                new String[] { ESTGParkingDBContract.LocationBase.ID,
                        ESTGParkingDBContract.LocationBase.LATITUDE,
                        ESTGParkingDBContract.LocationBase.LONGITUDE,
                        ESTGParkingDBContract.LocationBase.TIME,
                        ESTGParkingDBContract.LocationBase.EMAIL,
                        ESTGParkingDBContract.LocationBase.SECTION_ID }, null, null,
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                location.setId(cursor.getString(0));
                location.setLatitude(cursor.getDouble(1));
                location.setLongitude(cursor.getDouble(2));
                location.setTime(cursor.getInt(3));
                location.setEmail(cursor.getString(4));
                location.setSectionId(cursor.getString(5));
            }

            cursor.close();
        }

        return location;
    }

    private boolean locationExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.LocationBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.LocationBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() != 0);
                cursor.close();
            }

        return exist;
    }

    private long insertLocation(Location location) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.LocationBase.ID, location.getId());
        values.put(ESTGParkingDBContract.LocationBase.LATITUDE, location.getLatitude());
        values.put(ESTGParkingDBContract.LocationBase.LONGITUDE, location.getLongitude());
        values.put(ESTGParkingDBContract.LocationBase.TIME, location.getTime());
        values.put(ESTGParkingDBContract.LocationBase.EMAIL, location.getEmail());
        values.put(ESTGParkingDBContract.LocationBase.SECTION_ID, location.getSectionId());
        return database().insert(ESTGParkingDBContract.LocationBase.TABLE_NAME,
                null, values);
    }
}
