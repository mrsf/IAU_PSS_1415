package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import com.dropbox.sync.android.DbxRecord;

/**
 * Created by francisco on 05-12-2014.
 */
public class ParkingLot {

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String imagePath;

    public ParkingLot() {}

    public ParkingLot(String name, String description, double latitude, double longitude, String imagePath) {

        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
