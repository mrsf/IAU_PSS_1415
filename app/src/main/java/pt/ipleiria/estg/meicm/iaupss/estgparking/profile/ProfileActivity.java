package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;

import java.io.InputStream;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ParkingSpotActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;

public class ProfileActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /**
     * App singleton
     */
    private ESTGParkingApplication app;

    private ImageView imgProfilePic;
    private TextView txtUsername;
    private TextView txtEmail;

    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ESTGParkingApplication.getInstance();
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        app.setSharedPreferences(sharedPreferences);

        GoogleApiClient googleApiClient = app.getGoogleApiClient();

        setContentView(R.layout.activity_profile);

        imgProfilePic = (ImageView) findViewById(R.id.imagePhoto);
        txtUsername = (TextView) findViewById(R.id.textUsername);
        txtEmail = (TextView) findViewById(R.id.textEmail);
        TextView txtUserActivity = (TextView) findViewById(R.id.txt_main_current_activity);

        fetchUserInfo();

        Button parkingSpotButton = (Button) findViewById(R.id.profile_btn_parking_spot);
        parkingSpotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(ProfileActivity.this, ParkingSpotActivity.class);
                startActivity(intent);
            }
        });


        final TextView txtStatus = (TextView) findViewById(R.id.profile_txt_status);

        final Button parkButton = (Button) findViewById(R.id.profile_btn_park);
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Location location = locationClient.getLastLocation();

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                String lotId = app.getLotRepository(true).findLot(lat, lng);

                if (lotId != null) {
                    app.getSectionRepository(lotId).occupySection(lat, lng);
                }

                if (app.isParked()) {
                    app.setParked(false);
                    txtStatus.setText("Nao estacionado");
                    parkButton.setText("Estacionar");
                } else {
                    app.setParked(true);
                    SharedPreferences.Editor editor = app.getSharedPreferences().edit();
                    editor.putBoolean("parked", true);
                    editor.putFloat(getString(R.string.user_parking_lat), (float)lat);
                    editor.putFloat(getString(R.string.user_parking_lng), (float)lng);
                    editor.commit();
                    txtStatus.setText("Estacionado");
                    parkButton.setText("Desestacionar");
                }
            }
        });


        if (app.isParked()) {
            parkButton.setText("Libertar estacionamento");
            txtStatus.setText("Nao estacionado");
        } else {
            parkButton.setText("Estacionar");
            txtStatus.setText("Estacionado");
        }

        txtStatus.setText("bla");

        locationClient = new LocationClient(this, this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
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
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }


    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadFacebookProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadFacebookProfileImage(ImageView bmImage) {
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
        }
    }

    private void fetchUserInfo() {
        String name = app.getUserInfo().getName();
        String email = app.getUserInfo().getEmail();
        String photoUrl = app.getUserInfo().getPhotoURL();

        txtUsername.setText(name);
        txtEmail.setText(email);

        new LoadProfileImage(imgProfilePic).execute(photoUrl);
    }
}
