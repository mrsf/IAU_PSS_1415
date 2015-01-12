package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

public class Location {

    private String id;
    private double latitude;
    private double longitude;
    private int time;
    private String email;
    private String sectionId;

    public Location() {}

    public Location(String id, double latitude, double longitude, int time, String email, String sectionId) {

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.email = email;
        this.sectionId = sectionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
