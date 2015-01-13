package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Lot implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double latitudeX;
    private double longitudeX;
    private double latitudeY;
    private double longitudeY;
    private String imagePath;

    public Lot() {}

    public Lot(Parcel in) {

        this.readFromParcel(in);
    }

    public Lot(String id, String name, String description, double latitudeX, double longitudeX, double latitudeY, double longitudeY, String imagePath) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.latitudeX = latitudeX;
        this.longitudeX = longitudeX;
        this.latitudeY = latitudeY;
        this.longitudeY = longitudeY;
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

    public double getLatitudeX() {
        return latitudeX;
    }

    public void setLatitudeX(double latitudeX) {
        this.latitudeX = latitudeX;
    }

    public double getLongitudeX() {
        return longitudeX;
    }

    public void setLongitudeX(double longitudeX) {
        this.longitudeX = longitudeX;
    }

    public double getLatitudeY() {
        return latitudeY;
    }

    public void setLatitudeY(double latitudeY) {
        this.latitudeY = latitudeY;
    }

    public double getLongitudeY() {
        return longitudeY;
    }

    public void setLongitudeY(double longitudeY) {
        this.longitudeY = longitudeY;
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
        dest.writeDouble(this.latitudeX);
        dest.writeDouble(this.longitudeX);
        dest.writeDouble(this.latitudeY);
        dest.writeDouble(this.longitudeY);
        dest.writeString(this.imagePath);
    }

    private void readFromParcel(Parcel in) {

        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.latitudeX = in.readDouble();
        this.longitudeX = in.readDouble();
        this.latitudeY = in.readDouble();
        this.longitudeY = in.readDouble();
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
