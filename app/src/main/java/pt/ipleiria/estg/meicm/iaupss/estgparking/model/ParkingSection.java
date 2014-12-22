package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingSection implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private int capacity;
    private double occupation;
    private String lotId;

    public ParkingSection() {}

    public ParkingSection(Parcel in) {

        this.readFromParcel(in);
    }

    public ParkingSection(String id, String name, String description, double latitude, double longitude, int capacity, double occupation, String lotId) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getOccupation() {
        return occupation;
    }

    public void setOccupation(double occupation) {
        this.occupation = occupation;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
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
        dest.writeInt(this.capacity);
        dest.writeDouble(this.occupation);
        dest.writeString(this.lotId);
    }

    private void readFromParcel(Parcel in) {

        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.capacity = in.readInt();
        this.occupation = in.readDouble();
        this.lotId = in.readString();
    }

    public static final Parcelable.Creator<ParkingSection> CREATOR = new Parcelable.Creator<ParkingSection>() {

        @Override
        public ParkingSection createFromParcel(Parcel source) {
            return new ParkingSection(source);
        }

        @Override
        public ParkingSection[] newArray(int size) {
            return new ParkingSection[size];
        }
    };
}
