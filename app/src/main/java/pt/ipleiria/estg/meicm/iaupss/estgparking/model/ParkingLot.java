package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.sync.android.DbxRecord;

/**
 * Created by francisco on 05-12-2014.
 */
public class ParkingLot implements Parcelable {

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String imagePath;

    public ParkingLot() {}

    public ParkingLot(Parcel in) {

        this.readFromParcel(in);
    }

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

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.imagePath);
    }

    private void readFromParcel(Parcel in) {

        this.name = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<ParkingLot> CREATOR = new Parcelable.Creator<ParkingLot>() {

        @Override
        public ParkingLot createFromParcel(Parcel source) {
            return new ParkingLot(source);
        }

        @Override
        public ParkingLot[] newArray(int size) {
            return new ParkingLot[size];
        }
    };
}
