package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Application;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.google.android.gms.common.api.GoogleApiClient;

import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ILotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ISectionRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.IRankingRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.LotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.SectionRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.RankingRepository;

// We're creating our own Application just to have a singleton off of which to hand the datastore manager.
public class ESTGParkingApplication extends Application {

    private static ESTGParkingApplication singleton;

    private static final String APP_KEY = "va8yje80i09ga1t";
    private static final String APP_SECRET = "pak1a4lgec50mh9";

    private DbxAccountManager accountManager;
    private DbxDatastoreManager datastoreManager;
    private DbxDatastore datastore;

    // Client used to interact with Google APIs.
    private GoogleApiClient googleApiClient;

    private String currentUserActivity;

    public static ESTGParkingApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        this.accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public String getCurrentUserActivity() {
        return currentUserActivity;
    }

    public void setCurrentUserActivity(String currentUserActivity) {
        this.currentUserActivity = currentUserActivity;
    }

    public DbxAccountManager getAccountManager() {
        return accountManager;
    }

    public ILotRepository getLotRepository(boolean update) {
        this.initDatastore();
        return new LotRepository(this.getApplicationContext(), this.datastore, update);
    }

    public ISectionRepository getSectionRepository(String lotId) {
        this.initDatastore();
        return new SectionRepository(this.getApplicationContext(), this.datastore, lotId);
    }

    public IRankingRepository getRankingRepository() {
        this.initDatastore();
        return new RankingRepository(this.getApplicationContext(), this.datastore);
    }

    public DbxDatastoreManager getDatastoreManager() {
        return datastoreManager;
    }

    public void setDatastoreManager(DbxDatastoreManager datastoreManager) {
        this.datastoreManager = datastoreManager;
    }

    public DbxDatastore getDatastore() {
        return datastore;
    }

    public void setDatastore(DbxDatastore datastore) {
        this.datastore = datastore;
    }

    public void initDatastore() {
        if (this.datastore == null) {
            try {
                this.datastore = this.datastoreManager.openDatastore(".hhSgxW6D63kRmT-eiB69OuFN6jP8SydcsQopEJ1QGFs");
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }
}