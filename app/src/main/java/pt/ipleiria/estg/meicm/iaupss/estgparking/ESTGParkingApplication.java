package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import pt.ipleiria.estg.meicm.iaupss.estgparking.profile.IUserInfoProvider;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ILotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.ISectionRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.IRankingRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.LotRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.SectionRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.repository.RankingRepository;

// We're creating our own Application just to have a singleton off of which to hand the datastore manager.
public class ESTGParkingApplication extends Application {

    /**
     * Google Analytics property Id
     */
    private static final String PROPERTY_ID = "UA-43643199-8";

    /**
     * Dropbox constants
     */
    private static final String APP_KEY = "va8yje80i09ga1t";
    private static final String APP_SECRET = "pak1a4lgec50mh9";

    public enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
    }

    private static ESTGParkingApplication singleton;

    /**
     * Google Analytics trackers list
     */
    private HashMap<TrackerName, Tracker> trackers = new HashMap<>();

    /**
     * Dropbox API stuff
     */
    private DbxAccountManager accountManager;
    private DbxDatastoreManager datastoreManager;
    private DbxDatastore datastore;

    /**
     * User info provider service
     */
    private IUserInfoProvider userInfoProvider;

    /**
     * Indicates whether the car is parked or not
     */
    private Boolean isParked;

    /**
     * Indicates the OAuth provider being used
     */
    private OAuthProvider oAuthProvider;

    /**
     * Client used to interact with Google APIs
     */
    private GoogleApiClient googleApiClient;

    /**
     * Application shared preferences
     */
    private SharedPreferences sharedPreferences;

    private String currentUserActivity;

    public static ESTGParkingApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        this.accountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        sharedPreferences = this.getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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

    public void initDatastore() {
        if (this.datastore == null) {
            try {
                this.datastore = this.datastoreManager.openDatastore(".hhSgxW6D63kRmT-eiB69OuFN6jP8SydcsQopEJ1QGFs");
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Get Google Analytics tracker
     * @param trackerId
     * @return
     */
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID) : analytics
                    .newTracker(R.xml.global_tracker);
            trackers.put(trackerId, t);

        }
        return trackers.get(trackerId);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Boolean isParked() {

        if (isParked == null) {
            isParked = getSharedPreferences().getBoolean("parked", false);
        }

        return isParked;
    }

    private void setParked(Boolean isParked) {
        this.isParked = isParked;
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("parked", isParked);
        editor.commit();
    }

    public LatLng getParkingLocation() {
        double latitude = (double)getSharedPreferences().getFloat(getString(R.string.user_parking_lat), (float)0);
        double longitude = (double)getSharedPreferences().getFloat(getString(R.string.user_parking_lng), (float)0);
        return new LatLng(latitude, longitude);
    }

    public void park(LatLng location) {

        setParked(true);

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("parked", true);
        editor.putFloat(getString(R.string.user_parking_lat), (float)location.latitude);
        editor.putFloat(getString(R.string.user_parking_lng), (float)location.longitude);
        editor.commit();

        String lotId = getLotRepository(true).findLot(location.latitude, location.longitude);

        if (lotId != null) {
            getSectionRepository(lotId).occupySection(location.latitude, location.longitude);
        }

        Log.i(this.getClass().getSimpleName(), "Parked in (" + location.latitude + ", " + location.longitude);
    }

    public void depart() {

        setParked(false);

        LatLng location = getParkingLocation();

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean("parked", false);
        editor.commit();

        String lotId = getLotRepository(true).findLot(location.latitude, location.longitude);

        if (lotId != null) {
            getSectionRepository(lotId).occupySection(location.latitude, location.longitude);
        }
    }

    // <editor-fold desc="Getters and setters">

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public IUserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public void setUserInfoProvider(IUserInfoProvider userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
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

    public OAuthProvider getoAuthProvider() {
        return oAuthProvider;
    }

    public void setoAuthProvider(OAuthProvider oAuthProvider) {
        this.oAuthProvider = oAuthProvider;
    }

    // </editor-fold>
}