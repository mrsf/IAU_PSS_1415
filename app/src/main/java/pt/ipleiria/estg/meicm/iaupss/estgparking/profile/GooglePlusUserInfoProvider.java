package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;

/**
 * Created by pedro on 12/01/15.
 */
public class GooglePlusUserInfoProvider implements IUserInfoProvider {

    private String name = "";

    private String email = "";

    private String photoURL = "";

    private boolean isInfoFetched;

    public GooglePlusUserInfoProvider() {
        ESTGParkingApplication app = ESTGParkingApplication.getInstance();
        GoogleApiClient googleApiClient = app.getGoogleApiClient();

        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            name = currentPerson.getDisplayName();
            Person.Image personPhoto = currentPerson.getImage();
            String personGooglePlusProfile = currentPerson.getUrl();
            email = Plus.AccountApi.getAccountName(googleApiClient);

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            photoURL = personPhoto.getUrl().substring(0,
                    personPhoto.getUrl().length() - 2)
                    + 512;

            isInfoFetched = true;
        }
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
