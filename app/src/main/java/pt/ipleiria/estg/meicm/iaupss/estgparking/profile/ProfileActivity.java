package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import android.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ParkingSpotActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;

public class ProfileActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

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

    private LocationListener locationListener;
    private LocationManager locationManager;

    private GoogleMap googleMap;

    private LatLng parkingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getActionBar();
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
                    }
                } else {
                    boolean res = app.park(new LatLng(lat, lng));
                    if (res) {
                        refreshLabels();
                    }
                }
            }
        });

        refreshLabels();

        parkingLocation = app.getParkingLocation();
        setUpMapIfNeeded();
    }

    private void refreshLabels() {
        if (app.isParked()) {
            parkButton.setText("Libertar estacionamento");
            txtStatus.setText("Estacionado");
            txtMapHeader.setText("Localização do veículo");
        } else {
            parkButton.setText("Estacionar");
            txtStatus.setText("Não Estacionado");
            txtMapHeader.setText("Localização atual");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // if not parked, show current location in the map.
        if (googleMap != null) {
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Localização atual"));
            if (!app.isParked()) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
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
                Toast.makeText(ProfileActivity.this, "bla", Toast.LENGTH_SHORT).show();

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
            // Alert
        }
    }

    private void refreshCurrentLocationMarker() {
        // if not parked, show current location in the map.
        if (googleMap != null) {
            if (currentLocationMarker != null) {
                currentLocationMarker.setPosition(currentLocation);
            } else {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation)
                        .title("Localização atual")
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

    /**
     * Sets up the map
     */
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


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
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

        // Show parking location if parked, else show current location.
        if (app.isParked()) {
            googleMap.addMarker(new MarkerOptions().position(parkingLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).title("Veículo"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));
        }
    }
}
