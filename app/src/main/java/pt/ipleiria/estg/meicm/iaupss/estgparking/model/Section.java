package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class Section implements Parcelable {

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
    private int capacity;
    private int occupation;
    private String lotId;

    public Section() {}

    public Section(Parcel in) {

        this.readFromParcel(in);
    }

    public Section(String id, String name, String description, double latitudeA, double longitudeA,
                   double latitudeB, double longitudeB, double latitudeC, double longitudeC,
                   double latitudeD, double longitudeD, int capacity, int occupation, String lotId) {

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
        this.capacity = capacity;
        this.occupation = occupation;
        this.lotId = lotId;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupation() {
        return occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public int getStatusColor() {
        int color;
        double o = (Double.valueOf(String.valueOf(this.occupation)) / Double.valueOf(String.valueOf(this.capacity)));
        if (o <= 0.5)
            color = Color.GREEN;
        else if (o <= 0.75)
            color = Color.YELLOW;
        else
            color = Color.RED;

        return color;
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
        dest.writeInt(this.capacity);
        dest.writeInt(this.occupation);
        dest.writeString(this.lotId);
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
        this.capacity = in.readInt();
        this.occupation = in.readInt();
        this.lotId = in.readString();
    }

    public static final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {

        @Override
        public Section createFromParcel(Parcel source) {
            return new Section(source);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };
}
