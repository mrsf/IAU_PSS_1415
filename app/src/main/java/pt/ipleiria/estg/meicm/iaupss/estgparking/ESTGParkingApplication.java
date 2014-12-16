package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Application;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

/**
 * Created by francisco on 20-11-2014.
 */

// We're creating our own Application just to have a singleton off of which to hand the datastore manager.
public class ESTGParkingApplication extends Application {

    private static final String APP_KEY = "va8yje80i09ga1t";
    private static final String APP_SECRET = "pak1a4lgec50mh9";

    //private SharedPreferences appConfigurations;

    private DbxAccountManager accountManager;
    private DbxAccount account;
    private DbxDatastoreManager datastoreManager;
    private DbxDatastore datastore;

    private IParkingLotRepository parkingLotRepository;

    private static ESTGParkingApplication singleton;

    public static ESTGParkingApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*this.appConfigurations = PreferenceManager
                .getDefaultSharedPreferences(this);*/
        singleton = this;

        this.accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);
    }

    public DbxAccountManager getAccountManager() {
        return accountManager;
    }

    public IParkingLotRepository getParkingLotRepository() {
        this.initDatastore();
        this.parkingLotRepository = new ParkingLotRepository(this.datastore);
        return parkingLotRepository;
    }

    public void setDatastoreManager(DbxDatastoreManager datastoreManager) {
        this.datastoreManager = datastoreManager;
    }

    public DbxDatastore getDatastore() {
        return datastore;
    }

    public void initDatastore() {
        if(this.datastore == null) {
            try {
                this.datastore = this.getDatastoreManager().openDatastore(".hhSgxW6D63kRmT-eiB69OuFN6jP8SydcsQopEJ1QGFs");
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    public DbxDatastoreManager getDatastoreManager() {
        return datastoreManager;
    }
}
