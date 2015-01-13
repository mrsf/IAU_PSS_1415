package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public class LotsData extends ESTGParkingData {

    public static final String[] CREATE_TABLE_LOT = new String[] {
            "CREATE TABLE IF NOT EXISTS "
                    + ESTGParkingDBContract.LotBase.TABLE_NAME + " ("
                    + ESTGParkingDBContract.LotBase.ID
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.PRIMARY_KEY
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.NAME
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.DESCRIPTION
                    + ESTGParkingDBContract.TEXT_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.LATITUDE_X
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.LONGITUDE_X
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.LATITUDE_Y
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.LONGITUDE_Y
                    + ESTGParkingDBContract.NUMERIC_TYPE
                    + ESTGParkingDBContract.COMMA_SEP
                    + ESTGParkingDBContract.LotBase.IMAGE_PATH
                    + ESTGParkingDBContract.TEXT_TYPE + ")" };

    public static final String[] DELETE_TABLE_LOT = new String[] {
            "DROP TABLE IF EXISTS " + ESTGParkingDBContract.LotBase.TABLE_NAME };

    public LotsData(Context context, Boolean dbUpdate) {

        super(context, dbUpdate, CREATE_TABLE_LOT, DELETE_TABLE_LOT);
    }

    public void insertLots(List<Lot> lots) {

        for (Lot lot : lots)
            if (lot != null)
                if (!this.lotExist(lot.getId()))
                    this.insertLot(lot);
    }

    public List<Lot> getLots() {

        List<Lot> lots = new LinkedList<>();
        Cursor cursor = database().query(
                ESTGParkingDBContract.LotBase.TABLE_NAME,
                new String[]{ESTGParkingDBContract.LotBase.ID,
                        ESTGParkingDBContract.LotBase.NAME,
                        ESTGParkingDBContract.LotBase.DESCRIPTION,
                        ESTGParkingDBContract.LotBase.LATITUDE_X,
                        ESTGParkingDBContract.LotBase.LONGITUDE_X,
                        ESTGParkingDBContract.LotBase.LATITUDE_Y,
                        ESTGParkingDBContract.LotBase.LONGITUDE_Y,
                        ESTGParkingDBContract.LotBase.IMAGE_PATH}, null, null,
                null, null, ESTGParkingDBContract.LotBase.NAME);

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    lots.add(new Lot(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5),
                            cursor.getDouble(6), cursor.getString(7)));
                } while (cursor.moveToNext());

            cursor.close();
        }

        return lots;
    }

    private boolean lotExist(String id) {

        boolean exist = false;
        Cursor cursor = database().rawQuery(
                "SELECT * FROM " + ESTGParkingDBContract.LotBase.TABLE_NAME
                        + " WHERE " + ESTGParkingDBContract.LotBase.ID
                        + " = ?", new String[] { id });

        if (cursor != null)
            if (cursor.moveToFirst()) {
                exist = (cursor.getCount() != 0);
                cursor.close();
            }

        return exist;
    }

    private long insertLot(Lot lot) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(ESTGParkingDBContract.LotBase.ID, lot.getId());
        values.put(ESTGParkingDBContract.LotBase.NAME, lot.getName());
        values.put(ESTGParkingDBContract.LotBase.DESCRIPTION, lot.getDescription());
        values.put(ESTGParkingDBContract.LotBase.LATITUDE_X, lot.getLatitudeX());
        values.put(ESTGParkingDBContract.LotBase.LONGITUDE_X, lot.getLongitudeX());
        values.put(ESTGParkingDBContract.LotBase.LATITUDE_Y, lot.getLatitudeY());
        values.put(ESTGParkingDBContract.LotBase.LONGITUDE_Y, lot.getLongitudeY());
        values.put(ESTGParkingDBContract.LotBase.IMAGE_PATH, lot.getImagePath());
        return database().insert(ESTGParkingDBContract.LotBase.TABLE_NAME,
                null, values);
    }
}
