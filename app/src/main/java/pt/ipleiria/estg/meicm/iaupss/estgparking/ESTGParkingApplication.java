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

    private static final String APP_KEY = "2t0sxcmlop91dal";
    private static final String APP_SECRET = "biel5w67o22tinj";

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

    public void setDatastoreManager(DbxDatastoreManager datastoreManager) {
        this.datastoreManager = datastoreManager;
    }

    public DbxDatastore getDatastore() {
        if(this.datastore == null) {
            try {
                this.datastore = this.datastoreManager.openDatastore(".XKY75But_i5IprjZEcRGSc0V1tPq5iuO3-XBq4KJa7c");
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
        return datastore;
    }

    public IParkingLotRepository getParkingLotRepository() {
        this.parkingLotRepository = new ParkingLotRepository(getDatastore());
        return parkingLotRepository;
    }
}
