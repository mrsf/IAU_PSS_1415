package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.Calendar;

import pt.ipleiria.estg.meicm.iaupss.estgparking.notification.ScheduleClient;
import pt.ipleiria.estg.meicm.iaupss.estgparking.profile.IUserInfoProvider;

public class ProfileActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, DbxDatastore.SyncStatusListener {

    private static final String TAG = "PROFILE_ACTIVITY";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;

    /**
     * App singleton
     */
    private ESTGParkingApplication app;

    private ImageView imgProfilePic;
    private TextView txtUsername;
    private TextView txtEmail;
    private TextView txtMapHeader;
    private TextView txtStatus;
    private ProgressBar progressBar;
    private LocationClient locationClient;
    private LatLng currentLocation;

    private Button parkButton;

    private Marker currentLocationMarker;
    private Marker parkingLocationMarker;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private GoogleMap googleMap;

    private LatLng parkingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            // Create a new service client and bind our activity to this service
            this.scheduleClient = new ScheduleClient(this.getApplicationContext());
            this.scheduleClient.doBindService();
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        locationClient = new LocationClient(this, this, this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        app = ESTGParkingApplication.getInstance();

        imgProfilePic = (ImageView) findViewById(R.id.imagePhoto);
        txtUsername = (TextView) findViewById(R.id.textUsername);
        txtEmail = (TextView) findViewById(R.id.textEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fetchUserInfo();

        startLocationListener();


        txtStatus = (TextView) findViewById(R.id.profile_txt_status);
        txtMapHeader = (TextView) findViewById(R.id.profile_txt_map_header);

        parkButton = (Button) findViewById(R.id.profile_btn_park);
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Location location = locationClient.getLastLocation();

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                if (app.isParked()) {
                    boolean res = app.depart(new LatLng(lat, lng));
                    if (res) {
                        refreshLabels();
                        refreshParkingLocationMarker();
                    }
                } else {

                    //CharSequence[] items = {"1 hora","2 hora","4 horas","Indefinido"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.profile_parked_time_dialog_title)
                            .setItems(R.array.profile_park_time_items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case 0:
                                            defineAlarm(60);
                                            break;
                                        case 1:
                                            defineAlarm(120);
                                            break;
                                        case 2:
                                            defineAlarm(240);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();

                    boolean res = app.park(new LatLng(lat, lng));
                    if (res) {
                        refreshLabels();
                        refreshParkingLocationMarker();
                    }
                }
            }
        });

        refreshLabels();

        parkingLocation = app.getParkingLocation();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(this.scheduleClient != null)
            this.scheduleClient.doUnbindService();
        super.onStop();
    }

    private void defineAlarm(int waitTime) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE) + waitTime;
        int second = c.get(Calendar.SECOND);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);

        scheduleClient.setAlarmForNotification(c);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.app.initDatastore();
        try {
            this.app.getDatastore().addSyncStatusListener(this);
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), R.string.dropbox_connection_toas_text, Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            this.app.getDatastore().removeSyncStatusListener(this);
            this.app.getDatastore().close();
            this.app.setDatastore(null);
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    private void refreshLabels() {
        if (app.isParked()) {
            parkButton.setText(R.string.profile_depark_button_text);
            txtStatus.setText(R.string.profile_parked_status_text);
            txtMapHeader.setText(R.string.profile_car_location_text);
        } else {
            parkButton.setText(R.string.profile_park_button_text);
            txtStatus.setText(R.string.profile_deparked_status_text);
            txtMapHeader.setText(R.string.profile_actual_location_text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

            // if not parked, show current location in the map.
            if (googleMap != null) {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title(getResources().getString(R.string.profile_actual_location_text)));
                if (!app.isParked()) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            }

        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    private void startLocationListener() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                //Toast.makeText(ProfileActivity.this, "Localização Actualizada", Toast.LENGTH_SHORT).show();

                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                refreshCurrentLocationMarker();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else if (networkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            showGPSAlertMessage();
        }
    }

    private void showGPSAlertMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.profile_gps_dialog_text)
                .setCancelable(false)
                .setPositiveButton(R.string.profile_gps_activate_text, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.profile_gps_cancel_text, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void refreshCurrentLocationMarker() {
        if (googleMap != null) {
            if (currentLocationMarker != null) {
                currentLocationMarker.setPosition(currentLocation);
            } else {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation)
                        .title(getResources().getString(R.string.profile_actual_location_text))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        super.onDestroy();

        try {
            if (!this.app.getDatastoreManager().isShutDown())
                this.app.getDatastoreManager().shutDown();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        Log.d(TAG, datastore.getSyncStatus().toString());

        /*if (datastore.getSyncStatus().isUploading)
            Toast.makeText(getApplicationContext(), "Uploading ...", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Upload Finish", Toast.LENGTH_SHORT).show();*/

    }

    private void fetchUserInfo() {

        String name = null;
        String email = null;
        String photoUrl = null;
        IUserInfoProvider userInfoProvider = app.getUserInfoProvider();

        if (userInfoProvider != null) {
            name = app.getUserInfoProvider().getName();
            email = app.getUserInfoProvider().getEmail();
            photoUrl = app.getUserInfoProvider().getPhotoURL();
        }

        txtUsername.setText(name);
        txtEmail.setText(email);

        if (photoUrl != null) {
            new LoadProfileImage(imgProfilePic).execute(photoUrl);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        googleMap.clear();
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                Intent intent;
                intent = new Intent(ProfileActivity.this, ParkingSpotActivity.class);
                startActivity(intent);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent;
                intent = new Intent(ProfileActivity.this, ParkingSpotActivity.class);
                startActivity(intent);
                return true;
            }
        });

        refreshParkingLocationMarker();
    }

    private void refreshParkingLocationMarker() {
        if (parkingLocationMarker != null) {
            parkingLocationMarker.remove();
        }
        // Show parking location if parked
        if (app.isParked()) {
            if (parkingLocation != null) {
                parkingLocationMarker = googleMap.addMarker(new MarkerOptions().position(parkingLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).title(getResources().getString(R.string.profile_vehicle_mark_text)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));
            }
        }
    }
}
