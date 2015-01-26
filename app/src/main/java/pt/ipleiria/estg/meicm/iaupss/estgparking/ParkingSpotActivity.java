package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.ActionBar;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection;

public class ParkingSpotActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    private GoogleDirection googleDirection;
    private Document document;

    private ESTGParkingApplication app;

    private LatLng parkingLocation;
    private LatLng currentLocation;

    private LocationClient locationClient;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot);

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        locationClient = new LocationClient(this, this, this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        app = ESTGParkingApplication.getInstance();
        parkingLocation = app.getParkingLocation();
        setUpMapIfNeeded();
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
                googleDirection.animateDirection(googleMap, googleDirection.getDirection(document), GoogleDirection.SPEED_FAST, true, false, true, false, null, false, true, null);
                return true;
            case R.id.action_parking_spot_show_path:
                googleDirection.setLogging(true);
                googleDirection.request(currentLocation, parkingLocation, GoogleDirection.MODE_WALKING);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        // Show parking location if parked, else show current location.
        if (app.isParked()) {
            googleMap.addMarker(new MarkerOptions().position(parkingLocation).title("Veículo"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));
        } else if (currentLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Localização atual"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }

        googleDirection = new GoogleDirection(this);
        googleDirection.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                document = doc;
                googleMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                googleMap.addMarker(new MarkerOptions().position(parkingLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE)).title("Veículo"));

                googleMap.addMarker(new MarkerOptions().position(currentLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)).title("Localização atual"));
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
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // if not parked, show current location in the map.
        if (!app.isParked() && googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Localização atual"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
