package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Application;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;

import com.facebook.Session;

import java.util.HashMap;

import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.IParkingLotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.IParkingSectionRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ParkingLotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ParkingSectionRepository;

/**
 * Created by francisco on 20-11-2014.
 */

// We're creating our own Application just to have a singleton off of which to hand the datastore manager.
public class ESTGParkingApplication extends Application {

    public enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private static final String PROPERTY_ID = "UA-43643199-8";


    private OAuthProvider oAuthProvider;

    private DbxAccountManager accountManager;
    private DbxAccount account;
    private DbxDatastoreManager datastoreManager;
    private DbxDatastore datastore;

    private static final String APP_KEY = "va8yje80i09ga1t";
    private static final String APP_SECRET = "pak1a4lgec50mh9";

    private IParkingLotRepository parkingLotRepository;
    private IParkingSectionRepository parkingSectionRepository;

    private static ESTGParkingApplication singleton;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient googleApiClient;

    private String currentUserActivity;

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

    public IParkingLotRepository getParkingLotRepository() {
        this.initDatastore();
        this.parkingLotRepository = new ParkingLotRepository(this.datastore);
        return parkingLotRepository;
    }

    public IParkingSectionRepository getParkingSectionRepository(String lotId) {
        this.initDatastore();
        this.parkingSectionRepository = new ParkingSectionRepository(this.datastore, lotId);
        return parkingSectionRepository;
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


    public OAuthProvider getoAuthProvider() {
        return oAuthProvider;
    }

    public void setoAuthProvider(OAuthProvider oAuthProvider) {
        this.oAuthProvider = oAuthProvider;
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID) : analytics
                    .newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}