package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
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

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    GoogleDirection gd;
    Document mDoc;

    private ESTGParkingApplication app;

    private LatLng parkingLocation;
    private LatLng userLocation;

    private LocationClient locationClient;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        app = ESTGParkingApplication.getInstance();
        parkingLocation = app.getParkingLocation();
        setUpMapIfNeeded();
        locationClient = new LocationClient(this, this, this);
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
                gd.animateDirection(mMap, gd.getDirection(mDoc), GoogleDirection.SPEED_FAST, true, false, true, false, null, false, true, null);
                return true;
            case R.id.action_parking_spot_show_path:
                gd.setLogging(true);
                gd.request(userLocation, parkingLocation, GoogleDirection.MODE_WALKING);
                return true;

            case R.id.action_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                menu.findItem(R.id.action_normal).setVisible(false);
                menu.findItem(R.id.action_sattelite).setVisible(true);
                menu.findItem(R.id.action_hybrid).setVisible(true);
                return true;

            case R.id.action_sattelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                menu.findItem(R.id.action_normal).setVisible(true);
                menu.findItem(R.id.action_sattelite).setVisible(false);
                menu.findItem(R.id.action_hybrid).setVisible(true);
                return true;

            case R.id.action_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(parkingLocation).title("Veículo"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 15));

        gd = new GoogleDirection(this);
        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                mDoc = doc;
                mMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                mMap.addMarker(new MarkerOptions().position(parkingLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                mMap.addMarker(new MarkerOptions().position(userLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));
            }
        });

        gd.setLogging(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
