package pt.ipleiria.estg.meicm.iaupss.estgparking;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection;
import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection.OnAnimateListener;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Dot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Rectangle;;

public class MapActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationManager manager;

    private Section section;

    private LatLng userLocation;
    private LatLng targetLocation;

    private GoogleMap googleMap;
    private GoogleDirection googleDirection;
    private Document document;

    private LocationClient locationClient;

    private Menu menu;

    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (this.section == null) {
            Intent i = getIntent();
            this.section = i.getParcelableExtra("Section");
            if (this.section != null) {
                Rectangle rectangle = new Rectangle();
                rectangle.setDotA(new Dot(this.section.getLatitudeA(), this.section.getLongitudeA()));
                rectangle.setDotB(new Dot(this.section.getLatitudeB(), this.section.getLongitudeB()));
                rectangle.setDotC(new Dot(this.section.getLatitudeC(), this.section.getLongitudeC()));
                rectangle.setDotD(new Dot(this.section.getLatitudeD(), this.section.getLongitudeD()));
                Dot dot = rectangle.getCenterDot();
                targetLocation = new LatLng(dot.getLat(), dot.getLng());
            }
        }

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15));

        googleDirection = new GoogleDirection(this);
        googleDirection.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                document = doc;
                googleMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                googleMap.addMarker(new MarkerOptions().position(userLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED)).title("Localização atual"));

                googleMap.addMarker(new MarkerOptions().position(targetLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_spot_marker)).title("Estacionamento"));
            }
        });

        googleDirection.setOnAnimateListener(new OnAnimateListener() {
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

        locationClient = new LocationClient(this, this, this);

        this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showGPSAlertMessage();
    }

    private void showGPSAlertMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("É necessária a activação do GPS?")
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
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
                googleDirection.cancelAnimated();
                googleDirection.animateDirection(googleMap, googleDirection.getDirection(document), GoogleDirection.SPEED_FAST, true, false, true, false, null, false, true, null);
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
    public void onPause() {
        super.onPause();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            googleDirection.cancelAnimated();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Location location = locationClient.getLastLocation();
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleDirection.request(userLocation, targetLocation, GoogleDirection.MODE_WALKING);

                if (this.section != null) {
                    // Instantiates a new Polyline object and adds points to define a rectangle
                    PolylineOptions rectOptions = new PolylineOptions()
                            .add(new LatLng(this.section.getLatitudeA(), this.section.getLongitudeA()))
                            .add(new LatLng(this.section.getLatitudeB(), this.section.getLongitudeB()))  // North of the previous point, but at the same longitude
                            .add(new LatLng(this.section.getLatitudeC(), this.section.getLongitudeC()))  // Same latitude, and 30km to the west
                            .add(new LatLng(this.section.getLatitudeD(), this.section.getLongitudeD()))  // Same longitude, and 16km to the south
                            .add(new LatLng(this.section.getLatitudeA(), this.section.getLongitudeA())); // Closes the polyline.

                    // Get back the mutable Polyline
                    Polyline polyline = googleMap.addPolyline(rectOptions);
                }

            } else {
                finish();
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
                // Log the error
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStop() {

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // After disconnect() is called, the client is considered "dead".
            locationClient.disconnect();
        }

        super.onStop();
    }


    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /*
             * Connect the client. Don't re-start any requests here;
             * instead, wait for onResume()
             */
            locationClient.connect();
        }
    }

    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /*
             * Connect the client. Don't re-start any requests here;
             * instead, wait for onResume()
             */
            locationClient.connect();
        }
    }
}
