package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by pedro on 12/01/15.
 */
public class FacebookUserInfo implements IUserInfo {

    private String id = "";

    private String name = "";

    private String email = "";

    private String photoURL = "";


    public FacebookUserInfo() {
        final Session session = Session.getActiveSession();

        //if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            id = user.getId();//user id
                            //profileName = user.getName();//user's profile name
                            //userNameView.setText(user.getName());
                            email = user.getProperty("email").toString();

                            name = user.getName();

                            photoURL = "https://graph.facebook.com/" + id + "/picture?type=large";
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        //}
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhotoURL() {
        return photoURL;
    }
}
