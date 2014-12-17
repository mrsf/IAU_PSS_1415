package pt.ipleiria.estg.meicm.iaupss.estgparking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by francisco on 17-12-2014.
 */
public class ESTGParkingDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "ESTGPARKING_DBHELPER";

    private final String[] sql_create_entries;
    private final String[] sql_delete_entries;

    public ESTGParkingDBHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version, String[] sql_create_entries,
                              String[] sql_delete_entries) {

        super(context, name, factory, version);
        this.sql_create_entries = sql_create_entries;
        this.sql_delete_entries = sql_delete_entries;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.w(TAG, "A criar a base de dados ...");
        for (String s : sql_create_entries)
            db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "A actualizar da versão " + oldVersion + " para a versão "
                + newVersion + "...");
        for (String s : sql_delete_entries)
            db.execSQL(s);

        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);
    }

}
