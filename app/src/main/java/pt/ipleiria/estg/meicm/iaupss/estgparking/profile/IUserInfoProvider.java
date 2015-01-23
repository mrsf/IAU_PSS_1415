package pt.ipleiria.estg.meicm.iaupss.estgparking.profile;

public interface IUserInfoProvider {

    String getName();
    String getEmail();
    String getPhotoURL();
    boolean isInfoFetched();

}
