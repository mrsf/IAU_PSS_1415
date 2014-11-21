package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Application;

import com.dropbox.sync.android.DbxDatastoreManager;

/**
 * Created by francisco on 20-11-2014.
 */

// We're creating our own Application just to have a singleton off of which to hand the datastore manager.
public class ESTGParkingApplication extends Application {

    public DbxDatastoreManager datastoreManager;

    private static ESTGParkingApplication singleton;

    public static ESTGParkingApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

}
