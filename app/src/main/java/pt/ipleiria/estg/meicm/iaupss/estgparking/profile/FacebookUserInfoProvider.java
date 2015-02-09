package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by pedro on 12/01/15.
 */
public class FacebookUserInfoProvider implements IUserInfoProvider {

    private String id = "";
    private String name = "";
    private String email = "";
    private String photoURL = "";
    private boolean isInfoFetched;

    public FacebookUserInfoProvider(Session session) {
        fetchUserInfo(session);
    }

    private void fetchUserInfo(Session s) {
        final Session session = s;//Session.getActiveSession();
        // If the session is open, make an API call to get user data
        // and define a new callback to handle the response
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

            @Override
            public void onCompleted (GraphUser user, Response response){

                synchronized(FacebookUserInfoProvider.this) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                id = user.getId();
                                name = user.getName();
                                email = user.getProperty("email").toString();
                                photoURL = "https://graph.facebook.com/" + id + "/picture?type=large";
                            }
                            isInfoFetched = true;

                        FacebookUserInfoProvider.this.notifyAll();
                        session.close();
                    }
                }
            }
        });
        Request.executeBatchAsync(request);
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

    @Override
    public boolean isInfoFetched() {
        return isInfoFetched;
    }
}
