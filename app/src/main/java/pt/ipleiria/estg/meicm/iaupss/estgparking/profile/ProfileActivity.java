package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ParkingSpotActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;

public class ProfileActivity extends Activity {

    ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ESTGParkingApplication app = ESTGParkingApplication.getInstance();

        GoogleApiClient googleApiClient = app.getGoogleApiClient();

        setContentView(R.layout.activity_profile);

        imgProfilePic = (ImageView) findViewById(R.id.imagePhoto);
        TextView txtUsername = (TextView) findViewById(R.id.textUsername);
        TextView txtEmail = (TextView) findViewById(R.id.textEmail);
        TextView txtUserActivity = (TextView) findViewById(R.id.txt_main_current_activity);

        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String personName = currentPerson.getDisplayName();
            Person.Image personPhoto = currentPerson.getImage();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(googleApiClient);

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            String personPhotoUrl = personPhoto.getUrl().substring(0,
                    personPhoto.getUrl().length() - 2)
                    + 512;

            new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);


            txtUsername.setText(currentPerson.getDisplayName());
            txtEmail.setText(email);

            txtUserActivity.setText(app.getCurrentUserActivity());
        }

        Button parkingSpotButton = (Button) findViewById(R.id.profile_btn_parking_spot);
        parkingSpotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(ProfileActivity.this, ParkingSpotActivity.class);
                startActivity(intent);
            }
        });
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

    private String facebookUserId;

    private void fetchFacebookUserInfo() {
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            facebookUserId = user.getId();//user id
                            //profileName = user.getName();//user's profile name
                            //userNameView.setText(user.getName());
                            new LoadProfileImage(imgProfilePic).execute("https://graph.facebook.com/" + facebookUserId + "/picture?type=large");

                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }
    }
}
