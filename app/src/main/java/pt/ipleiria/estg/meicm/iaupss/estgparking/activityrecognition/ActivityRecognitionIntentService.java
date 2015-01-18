package pt.ipleiria.estg.meicm.iaupss.estgparking.activityrecognition;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplicationUtils;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    /**
     * Whether to write logs to file or not
     */
    private static boolean DEBUG_LOGS = true;
    private static String LOG_FILE_PATH = "/sdcard/estgparking.txt";
    private static long CURRENT_ACTIVITY_DELTA = 120000;

    private ESTGParkingApplication app;

    private LocationClient locationClient;

    private FileOutputStream fileOutputStream;
    private OutputStreamWriter outputStreamWriter;
    private static Location activityChangeLocation;
    private static Long activityChangeTime;

    public ActivityRecognitionIntentService() {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");

        app = ESTGParkingApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = new LocationClient(this, this, this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationClient.requestLocationUpdates(locationRequest, this);

        if (DEBUG_LOGS) {
            try {
                File myFile = new File(LOG_FILE_PATH);
                myFile.createNewFile();
                fileOutputStream = new FileOutputStream(myFile, true);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (DEBUG_LOGS) {
            try {
                outputStreamWriter.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = sdf.format(new Date());
        return dateString;
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {

            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            // Get the confidence percentage for the most probable activity
            int confidence = mostProbableActivity.getConfidence();

            // Get the type of activity
            int activityType = mostProbableActivity.getType();
            app.setCurrentUserActivity(getNameFromType(activityType));

            // Check to see if the repository contains a previous activity
            if (!app.getSharedPreferences().contains(ESTGParkingApplicationUtils.KEY_PREVIOUS_ACTIVITY_TYPE) &&
                    (activityType == DetectedActivity.ON_FOOT || activityType == DetectedActivity.STILL)) {

                // This is the first type an activity has been detected. Store the type
                Editor editor = app.getSharedPreferences().edit();
                editor.putInt(ESTGParkingApplicationUtils.KEY_PREVIOUS_ACTIVITY_TYPE, activityType);
                editor.commit();

                Log.i(ESTGParkingApplicationUtils.APPTAG, "Initialized shared preferences activity type with " + getNameFromType(activityType));

                // If the repository contains a type
            } else if (confidence >= 50) {
                checkForActivityChange(activityType, confidence);
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
                .setContentIntent(getContentIntent());

        // Get an instance of the Notification Manager
        NotificationManager notifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

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
    private void checkForActivityChange(int currentType, int confidence) {

        // Get the previous type, otherwise return the "unknown" type
        int previousType = app.getSharedPreferences().getInt(ESTGParkingApplicationUtils.KEY_PREVIOUS_ACTIVITY_TYPE, DetectedActivity.UNKNOWN);

        if (DEBUG_LOGS) {
            try {
                String log = "Activity: " + getNameFromType(currentType) + "; Confidence: " + confidence + "; Previous: " + getNameFromType(previousType) + "\r\n";
                Log.i(ESTGParkingApplicationUtils.APPTAG, log);
                outputStreamWriter.append(getDateString() + " - " + log);
                if (activityChangeTime != null)
                    outputStreamWriter.append(getDateString() + " - " + (System.currentTimeMillis() - activityChangeTime));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (previousType == DetectedActivity.IN_VEHICLE && currentType != DetectedActivity.IN_VEHICLE) {

            if (activityChangeTime == null) {
                activityChangeTime = System.currentTimeMillis();    // Get time for activity change
                locationClient.connect();                           // Get location for activity change
            } else if (System.currentTimeMillis() - activityChangeTime > CURRENT_ACTIVITY_DELTA) {
                Editor editor = app.getSharedPreferences().edit();
                editor.putInt(ESTGParkingApplicationUtils.KEY_PREVIOUS_ACTIVITY_TYPE, currentType);
                editor.commit();
                app.park(new LatLng(activityChangeLocation.getLatitude(), activityChangeLocation.getLongitude()));
                sendNotification("Parked");
                activityChangeTime = null;

                if (DEBUG_LOGS) {
                    try {
                        outputStreamWriter.append(getDateString() + " - Parked at (" + activityChangeLocation.getLatitude() + ", " + activityChangeLocation.getLongitude() + ")\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (previousType != DetectedActivity.IN_VEHICLE && currentType == DetectedActivity.IN_VEHICLE) {

            if (activityChangeTime == null) {
                activityChangeTime = System.currentTimeMillis();    // Get time for activity change
                locationClient.connect();                           // Get location for activity change
            } else if (System.currentTimeMillis() - activityChangeTime > CURRENT_ACTIVITY_DELTA) {
                Editor editor = app.getSharedPreferences().edit();
                editor.putInt(ESTGParkingApplicationUtils.KEY_PREVIOUS_ACTIVITY_TYPE, currentType);
                editor.commit();
                app.depart(new LatLng(activityChangeLocation.getLatitude(), activityChangeLocation.getLongitude()));
                sendNotification("Departed");
                activityChangeTime = null;

                if (DEBUG_LOGS) {
                    try {
                        outputStreamWriter.append(getDateString() + " - Departed at (" + activityChangeLocation.getLatitude() + ", " + activityChangeLocation.getLongitude() + ")\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            activityChangeTime = null;
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
    public void onConnected(Bundle bundle) {
        activityChangeLocation = locationClient.getLastLocation();
        locationClient.disconnect();
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
