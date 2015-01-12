package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ESTGParkingData {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ESTGParking.db";

    private ESTGParkingDBHelper dbHelper;
    private SQLiteDatabase mDb;

    private final Context context;
    private final Boolean dbUpdate;
    private final String[] sql_create_entries;
    private final String[] sql_delete_entries;

    public ESTGParkingData(Context context, Boolean dbUpdate,
                           String[] sql_create_entries, String[] sql_delete_entries) {

        this.context = context;
        this.dbUpdate = dbUpdate;
        this.sql_create_entries = sql_create_entries;
        this.sql_delete_entries = sql_delete_entries;
    }

    public ESTGParkingData open() throws SQLException {

        dbHelper = new ESTGParkingDBHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION, sql_create_entries, sql_delete_entries);

        if (!dbUpdate)
            mDb = dbHelper.getReadableDatabase();
        else
            mDb = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {

        dbHelper.close();
    }

    public SQLiteDatabase database() {

        return mDb;
    }
}
