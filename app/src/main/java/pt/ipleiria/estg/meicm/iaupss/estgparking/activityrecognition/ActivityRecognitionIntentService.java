package pt.ipleiria.estg.meicm.iaupss.estgparking.activityrecognition;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    // Store the app's shared preferences repository
    private SharedPreferences mPrefs;

    private ESTGParkingApplication app;

    private LocationClient locationClient;

    public ActivityRecognitionIntentService() {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");

        app = ESTGParkingApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = new LocationClient(this, this, this);
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Get a handle to the repository
        mPrefs = getApplicationContext().getSharedPreferences(ActivityUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {

            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Log the update
            logActivityRecognitionResult(result);

            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            // Get the confidence percentage for the most probable activity
            int confidence = mostProbableActivity.getConfidence();

            // Get the type of activity
            int activityType = mostProbableActivity.getType();
            app.setCurrentUserActivity(getNameFromType(activityType));

            Log.i("activityType", "activityType " + getNameFromType(activityType));

            activityChanged(activityType);

            // Check to see if the repository contains a previous activity
            if (!mPrefs.contains(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE)) {

                // This is the first type an activity has been detected. Store the type
                Editor editor = mPrefs.edit();
                editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, activityType);
                editor.commit();

                // If the repository contains a type
            } else if (
                // If the current type is "moving"
//                    isMoving(activityType)
//
//                            &&

                            // The activity has changed from the previous activity
                            activityChanged(activityType)

                            // The confidence level for the current activity is > 50%
                            && (confidence >= 50)) {

                // Notify the user
                //sendNotification();
            }
        }
    }

    /**
     * Post a notification to the user. The notification prompts the user to click it to open the device's GPS settings
     */
    private void sendNotification(String msg) {

        // Create a notification builder that's compatible with platforms >= version 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        // Set the title, text, and icon
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_notification)

                        // Get the Intent that starts the Location settings panel
                .setContentIntent(getContentIntent());

        // Get an instance of the Notification Manager
        NotificationManager notifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Build the notification and post it
        notifyManager.notify(0, builder.build());
    }

    /**
     * Get a content Intent for the notification
     *
     * @return A PendingIntent that starts the device's Location Settings panel.
     */
    private PendingIntent getContentIntent() {

        // Set the Intent action to open Location Settings
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        // Create a PendingIntent to start an Activity
        return PendingIntent.getActivity(getApplicationContext(), 0, gpsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Tests to see if the activity has changed
     *
     * @param currentType The current activity type
     * @return true if the user's current activity is different from the previous most probable
     * activity; otherwise, false.
     */
    private boolean activityChanged(int currentType) {

        // Get the previous type, otherwise return the "unknown" type
        int previousType = mPrefs.getInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, DetectedActivity.UNKNOWN);

        if (previousType == DetectedActivity.IN_VEHICLE && currentType == DetectedActivity.ON_FOOT) {
            sendNotification("transition from IN_VEHICLE to ON_FOOT");
            Editor editor = mPrefs.edit();
            editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, currentType);
            editor.commit();
            Location location = locationClient.getLastLocation();
            app.park(new LatLng(location.getLatitude(), location.getLongitude()));
            return true;
        }

        if (previousType == DetectedActivity.ON_FOOT && currentType == DetectedActivity.IN_VEHICLE) {
            sendNotification("transition from ON_FOOT to IN_VEHICLE");
            Editor editor = mPrefs.edit();
            editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, currentType);
            editor.commit();
            Location location = locationClient.getLastLocation();
            app.park(new LatLng(location.getLatitude(), location.getLongitude()));
            return true;
        }

        //locationClient = new LocationClient(this, this, this);
        locationClient.connect();

        // If the previous type isn't the same as the current type, the activity has changed
        if (previousType != currentType) {

            sendNotification("transition from " + previousType + " to " + currentType);

            return true;

            // Otherwise, it hasn't.
        } else {
            return false;
        }
    }

    /**
     * Determine if an activity means that the user is moving.
     *
     * @param type The type of activity the user is doing (see DetectedActivity constants)
     * @return true if the user seems to be moving from one location to another, otherwise false
     */
    private boolean isMoving(int type) {
        switch (type) {
            // These types mean that the user is probably not moving
            case DetectedActivity.STILL :
            case DetectedActivity.TILTING :
            case DetectedActivity.UNKNOWN :
                return false;
            default:
                return true;
        }
    }

    /**
     * Write the activity recognition update to the log file
     *
     * @param result The result extracted from the incoming Intent
     */
    private void logActivityRecognitionResult(ActivityRecognitionResult result) {
        // Get all the probably activities from the updated result
        for (DetectedActivity detectedActivity : result.getProbableActivities()) {

            // Get the activity type, confidence level, and human-readable name
            int activityType = detectedActivity.getType();
            int confidence = detectedActivity.getConfidence();
            String activityName = getNameFromType(activityType);

            // Get the current log file or create a new one, then log the activity
//            LogFile.getInstance(getApplicationContext()).log(getString(R.string.log_message, activityType, activityName, confidence)
 //           );
        }
    }

    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        app.park(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
