package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import pt.ipleiria.estg.meicm.iaupss.estgparking.activityrecognition.DetectionRemover;
import pt.ipleiria.estg.meicm.iaupss.estgparking.activityrecognition.DetectionRequester;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    /*
     *  Intent filter for incoming broadcasts from the IntentService.
     */
    IntentFilter broadcastFilter;

    // Instance of a local broadcast manager
    private LocalBroadcastManager broadcastManager;

    // The activity recognition update request object
    private DetectionRequester detectionRequester;

    // The activity recognition update removal object
    private DetectionRemover detectionRemover;

    // Store the current request type (ADD or REMOVE)
    private ESTGParkingApplicationUtils.REQUEST_TYPE mRequestType;

    private ProgressBar progressBar;


    private Button aboutButton;
    private Button rankingsButton;
    private Button lotsButton;
    private Button facebookPostButton;
    private Button profileButton;
    private Button settings;

    private ESTGParkingApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminateVisibility(true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(false);


        app = ESTGParkingApplication.getInstance();

        // Location stuff
        // Set the broadcast receiver intent filer
        broadcastManager = LocalBroadcastManager.getInstance(this);

        // Create a new Intent filter for the broadcast receiver
        broadcastFilter = new IntentFilter(ESTGParkingApplicationUtils.ACTION_REFRESH_STATUS_LIST);
        broadcastFilter.addCategory(ESTGParkingApplicationUtils.CATEGORY_LOCATION_SERVICES);

        // Get detection requester and remover objects
        detectionRequester = new DetectionRequester(this);
        detectionRemover = new DetectionRemover(this);

        if (app.getSharedPreferences().getBoolean("automatic_park", true)) {
            Log.i(ESTGParkingApplicationUtils.APPTAG, "Starting activity recognition service...");
            detectionRequester.requestUpdates();
            app.setServiceRunning(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.main_progressBar);

        settings = (Button) findViewById(R.id.main_btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                settings.setBackground(getResources().getDrawable(R.drawable.settings_selected));
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        profileButton = (Button) findViewById(R.id.main_btn_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                profileButton.setBackground(getResources().getDrawable(R.drawable.map_marker_selected));
                Intent intent;
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        facebookPostButton = (Button) findViewById(R.id.main_btn_facebook_post);
        facebookPostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                facebookPostButton.setBackground(getResources().getDrawable(R.drawable.facebook_selected));
                Intent intent;
                intent = new Intent(MainActivity.this, FacebookPostPhotoActivity.class);
                startActivity(intent);
            }
        });

        lotsButton = (Button) findViewById(R.id.main_btn_lots);
        lotsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                lotsButton.setBackground(getResources().getDrawable(R.drawable.parking_lot_selected));
                Intent intent;
                intent = new Intent(MainActivity.this, LotsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        rankingsButton = (Button) findViewById(R.id.main_btn_rankings);
        rankingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            rankingsButton.setBackground(getResources().getDrawable(R.drawable.rankings_selected));
            Intent intent;
            intent = new Intent(MainActivity.this, RankingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            setSupportProgressBarIndeterminateVisibility(false);
            }
        });

        aboutButton = (Button) findViewById(R.id.main_btn_about);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                aboutButton.setBackground(getResources().getDrawable(R.drawable.about_selected));
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setMessage(R.string.about_msg);
                dlgAlert.setTitle(R.string.app_name);
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        aboutButton.setBackground(getResources().getDrawable(R.drawable.about));
                    }
                });
                dlgAlert.setCancelable(true);

                dlgAlert.create().show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (!this.app.getDatastoreManager().isShutDown())
                this.app.getDatastoreManager().shutDown();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        progressBar.setVisibility(ProgressBar.GONE);

        // Register the broadcast receiver
        broadcastManager.registerReceiver(updateListReceiver, broadcastFilter);

        // Reset button bitmaps
        settings.setBackground(getResources().getDrawable(R.drawable.settings));
        profileButton.setBackground(getResources().getDrawable(R.drawable.map_marker));
        facebookPostButton.setBackground(getResources().getDrawable(R.drawable.facebook));
        lotsButton.setBackground(getResources().getDrawable(R.drawable.parking_lot));
        rankingsButton.setBackground(getResources().getDrawable(R.drawable.rankings));
        aboutButton.setBackground(getResources().getDrawable(R.drawable.about));

        // Start or Stop the activity recognition service if the setting was changed
        if (app.isServiceOptionsChanged()) {
            if (app.getSharedPreferences().getBoolean("automatic_park", true) && !app.isServiceRunning()) {
                startActivityRecognitionService();
            } else if (!app.getSharedPreferences().getBoolean("automatic_park", true) && app.isServiceRunning()) {
                stopActivityRecognitionService();
            }
            app.setServiceOptionsChanged(false);
        }
    }

    @Override
    public void onPause() {
        // Stop listening to broadcasts when the Activity isn't visible.
        broadcastManager.unregisterReceiver(updateListReceiver);
        super.onPause();
    }


    private void stopActivityRecognitionService() {
        Log.i(ESTGParkingApplicationUtils.APPTAG, "Stopping activity recognition service...");
        Toast.makeText(this, "Serviço de deteção automática de estacionamento terminado", Toast.LENGTH_SHORT).show();
        detectionRemover.removeUpdates(detectionRequester.getRequestPendingIntent());
        app.setServiceRunning(false);
    }

    private void startActivityRecognitionService() {
        Log.i(ESTGParkingApplicationUtils.APPTAG, "Starting activity recognition service...");
        Toast.makeText(this, "Serviço de deteção automática de estacionamento iniciado", Toast.LENGTH_SHORT).show();
        detectionRequester.requestUpdates();
        app.setServiceRunning(true);
    }

    @Override
    public void onBackPressed() {

        app.setUserInfoProvider(null);
        app.getGoogleApiClient().disconnect();

        if (app.getSharedPreferences().getBoolean("automatic_park", true)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            MainActivity.super.onBackPressed();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            stopActivityRecognitionService();
                            MainActivity.super.onBackPressed();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("O serviço de detecção de estacionamento xpto encontra-se activo.\nDeseja manter o serviço em execução?").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
        } else {
             MainActivity.super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case ESTGParkingApplicationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to start activity recognition updates
                        if (ESTGParkingApplicationUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Restart the process of requesting activity recognition updates
                            detectionRequester.requestUpdates();

                            // If the request was to remove activity recognition updates
                        } else if (ESTGParkingApplicationUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                                /*
                                 * Restart the removal of all activity recognition updates for the
                                 * PendingIntent.
                                 */
                            detectionRemover.removeUpdates(detectionRequester.getRequestPendingIntent());
                            app.setServiceRunning(false);
                        }
                        break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(ESTGParkingApplicationUtils.APPTAG, getString(R.string.no_resolution));
                }

                break;

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d(ESTGParkingApplicationUtils.APPTAG, getString(R.string.unknown_activity_request_code, requestCode));

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(ESTGParkingApplicationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;

            // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }







    /**
     * Broadcast receiver that receives activity update intents
     * It checks to see if the ListView contains items. If it
     * doesn't, it pulls in history.
     * This receiver is local only. It can't read broadcast Intents from other apps.
     */
    BroadcastReceiver updateListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int a = 1;
            /*
             * When an Intent is received from the update listener IntentService, update
             * the displayed log.
             */
        }
    };
}
