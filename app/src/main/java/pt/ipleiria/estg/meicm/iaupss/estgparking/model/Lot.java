package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Lot implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double latitudeA;
    private double longitudeA;
    private double latitudeB;
    private double longitudeB;
    private double latitudeC;
    private double longitudeC;
    private double latitudeD;
    private double longitudeD;
    private String imagePath;

    public Lot() {}

    public Lot(Parcel in) {

        this.readFromParcel(in);
    }

    public Lot(String id, String name, String description, double latitudeA, double longitudeA,
               double latitudeB, double longitudeB, double latitudeC, double longitudeC,
               double latitudeD, double longitudeD, String imagePath) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.latitudeA = latitudeA;
        this.longitudeA = longitudeA;
        this.latitudeB = latitudeB;
        this.longitudeB = longitudeB;
        this.latitudeC = latitudeC;
        this.longitudeC = longitudeC;
        this.latitudeD = latitudeD;
        this.longitudeD = longitudeD;
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

    public double getLatitudeA() {
        return latitudeA;
    }

    public void setLatitudeA(double latitudeA) {
        this.latitudeA = latitudeA;
    }

    public double getLongitudeA() {
        return longitudeA;
    }

    public void setLongitudeA(double longitudeA) {
        this.longitudeA = longitudeA;
    }

    public double getLatitudeB() {
        return latitudeB;
    }

    public void setLatitudeB(double latitudeB) {
        this.latitudeB = latitudeB;
    }

    public double getLongitudeB() {
        return longitudeB;
    }

    public void setLongitudeB(double longitudeB) {
        this.longitudeB = longitudeB;
    }

    public double getLatitudeC() {
        return latitudeC;
    }

    public void setLatitudeC(double latitudeC) {
        this.latitudeC = latitudeC;
    }

    public double getLongitudeC() {
        return longitudeC;
    }

    public void setLongitudeC(double longitudeC) {
        this.longitudeC = longitudeC;
    }

    public double getLatitudeD() {
        return latitudeD;
    }

    public void setLatitudeD(double latitudeD) {
        this.latitudeD = latitudeD;
    }

    public double getLongitudeD() {
        return longitudeD;
    }

    public void setLongitudeD(double longitudeD) {
        this.longitudeD = longitudeD;
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
        dest.writeDouble(this.latitudeA);
        dest.writeDouble(this.longitudeA);
        dest.writeDouble(this.latitudeB);
        dest.writeDouble(this.longitudeB);
        dest.writeDouble(this.latitudeC);
        dest.writeDouble(this.longitudeC);
        dest.writeDouble(this.latitudeD);
        dest.writeDouble(this.longitudeD);
        dest.writeString(this.imagePath);
    }

    private void readFromParcel(Parcel in) {

        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.latitudeA = in.readDouble();
        this.longitudeA = in.readDouble();
        this.latitudeB = in.readDouble();
        this.longitudeB = in.readDouble();
        this.latitudeC = in.readDouble();
        this.longitudeC = in.readDouble();
        this.latitudeD = in.readDouble();
        this.longitudeD = in.readDouble();
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
