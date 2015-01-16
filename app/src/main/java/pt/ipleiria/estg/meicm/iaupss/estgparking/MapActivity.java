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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection;
import pt.ipleiria.estg.meicm.iaupss.estgparking.directions.GoogleDirection.OnAnimateListener;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;;

public class MapActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private Section section;

    LatLng start = new LatLng(39.6359523,-8.8211917);
    LatLng end = new LatLng(39.6359523,-8.8211917);

    TextView textProgress;
    Button buttonAnimate, buttonRequest;

    GoogleMap mMap;
    GoogleDirection gd;
    Document mDoc;

    Location mCurrentLocation;

    private LocationClient mLocationClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        if (this.section == null) {
            Intent i = getIntent();
            this.section = i.getParcelableExtra("Section");
            if (this.section != null)
                end = new LatLng(this.section.getLatitudeA(), this.section.getLongitudeA());
        }

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        gd = new GoogleDirection(this);
        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                mDoc = doc;
                mMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                mMap.addMarker(new MarkerOptions().position(start)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                mMap.addMarker(new MarkerOptions().position(end)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                buttonAnimate.setVisibility(View.VISIBLE);
            }
        });

        gd.setOnAnimateListener(new OnAnimateListener() {
            public void onStart() {
                textProgress.setVisibility(View.VISIBLE);
            }

            public void onProgress(int progress, int total) {
                textProgress.setText(progress + " / " + total);
            }

            public void onFinish() {
                buttonAnimate.setVisibility(View.VISIBLE);
                textProgress.setVisibility(View.GONE);
            }
        });

        textProgress = (TextView) findViewById(R.id.textProgress);
        textProgress.setVisibility(View.GONE);

        buttonRequest = (Button) findViewById(R.id.buttonRequest);
        buttonRequest.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                gd.setLogging(true);
                gd.request(start, end, GoogleDirection.MODE_DRIVING);
            }
        });

        buttonAnimate = (Button) findViewById(R.id.buttonAnimate);
        buttonAnimate.setVisibility(View.GONE);
        buttonAnimate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                gd.animateDirection(mMap, gd.getDirection(mDoc), GoogleDirection.SPEED_FAST
                        , true, false, true, false, null, false, true, null);
            }
        });


        mLocationClient = new LocationClient(this, this, this);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
            case R.id.action_settings:
                gd.setLogging(true);
                gd.request(start, end, GoogleDirection.MODE_DRIVING);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gd.cancelAnimated();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = mLocationClient.getLastLocation();
        start = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }


    @Override
    public void onStop() {

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }


    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSAlertMessage();
        } else {
            /*
             * Connect the client. Don't re-start any requests here;
             * instead, wait for onResume()
             */
            mLocationClient.connect();
        }

    }

    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
        super.onResume();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSAlertMessage();
        } else {
            /*
             * Connect the client. Don't re-start any requests here;
             * instead, wait for onResume()
             */
            mLocationClient.connect();
        }
    }
}
