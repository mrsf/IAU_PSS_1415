package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

import org.w3c.dom.Document;

import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection;

public class ParkingSpotActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private GoogleMap googleMap;

    private GoogleDirection googleDirection;
    private Document document;

    private ESTGParkingApplication app;

    private LatLng parkingLocation;
    private LatLng currentLocation;

    private LocationClient locationClient;

    private Menu menu;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private Marker currentLocationMarker;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        startLocationListener();

        locationClient = new LocationClient(this, this, this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        app = ESTGParkingApplication.getInstance();
        parkingLocation = app.getParkingLocation();
        setUpMapIfNeeded();

        googleDirection.setOnAnimateListener(new GoogleDirection.OnAnimateListener() {
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onProgress(int progress, int total) {
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.map_progressbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_parking_spot, menu);
        menu.findItem(R.id.action_normal).setVisible(false);
        menu.findItem(R.id.action_sattelite).setVisible(true);
        menu.findItem(R.id.action_hybrid).setVisible(true);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_animate:
                if (googleDirection != null) {
                    googleDirection.animateDirection(googleMap, googleDirection.getDirection(document), GoogleDirection.SPEED_FAST, true, false, true, false, null, false, true, null);
                }
                return true;
            case R.id.action_parking_spot_show_path:
                googleDirection.setLogging(true);
                if (currentLocation != null && parkingLocation != null) {
                    googleDirection.request(currentLocation, parkingLocation, GoogleDirection.MODE_WALKING);
                }
                return true;

            case R.id.action_normal:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                menu.findItem(R.id.action_normal).setVisible(false);
                menu.findItem(R.id.action_sattelite).setVisible(true);
                menu.findItem(R.id.action_hybrid).setVisible(true);
                return true;

            case R.id.action_sattelite:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                menu.findItem(R.id.action_normal).setVisible(true);
                menu.findItem(R.id.action_sattelite).setVisible(false);
                menu.findItem(R.id.action_hybrid).setVisible(true);
                return true;

            case R.id.action_hybrid:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                menu.findItem(R.id.action_normal).setVisible(true);
                menu.findItem(R.id.action_sattelite).setVisible(true);
                menu.findItem(R.id.action_hybrid).setVisible(false);
                return true;

            case R.id.action_current_location:
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                return true;

            case R.id.action_parking_location:
                if (parkingLocation != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        super.onDestroy();
    }

    /**
     * Sets up the map
     */
    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

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

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // Show parking location if parked, else show current location.
        if (app.isParked()) {
            if (parkingLocation != null) {
                googleMap.addMarker(new MarkerOptions().position(parkingLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).title(getResources().getString(R.string.profile_vehicle_mark_text)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));
            }
        } else {
            if (currentLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }

        googleDirection = new GoogleDirection(this);
        googleDirection.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                document = doc;
                googleMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
            }
        });

        googleDirection.setLogging(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

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

    }

    private void startLocationListener() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                //Toast.makeText(ParkingSpotActivity.this, "bla", Toast.LENGTH_SHORT).show();

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
        // if not parked, show current location in the map.
        if (googleMap != null) {
            if (currentLocationMarker != null) {
                currentLocationMarker.setPosition(currentLocation);
            } else {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation)
                        .title(getResources().getString(R.string.profile_actual_location_text))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        }
    }
}
