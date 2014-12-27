package pt.ipleiria.estg.meicm.iaupss.estgparking;


public enum OAuthProvider {

    GOGGLE_PLUS(1), FACEBOOK(2);

    private int code;

    private OAuthProvider(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
