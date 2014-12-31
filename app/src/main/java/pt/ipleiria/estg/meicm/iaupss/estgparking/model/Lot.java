package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Lot implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String imagePath;

    public Lot() {}

    public Lot(Parcel in) {

        this.readFromParcel(in);
    }

    public Lot(String id, String name, String description, double latitude, double longitude, String imagePath) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getImagePath() {
        return imagePath;
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

        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.imagePath);
    }

    private void readFromParcel(Parcel in) {

        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<Lot> CREATOR = new Parcelable.Creator<Lot>() {

        @Override
        public Lot createFromParcel(Parcel source) {
            return new Lot(source);
        }

        @Override
        public Lot[] newArray(int size) {
            return new Lot[size];
        }
    };
}
