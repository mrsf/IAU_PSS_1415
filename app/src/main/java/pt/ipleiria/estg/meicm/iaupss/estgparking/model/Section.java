package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class Section implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double latitudeX;
    private double longitudeX;
    private double latitudeY;
    private double longitudeY;
    private int capacity;
    private int occupation;
    private String lotId;

    public Section() {}

    public Section(Parcel in) {

        this.readFromParcel(in);
    }

    public Section(String id, String name, String description, double latitudeX, double longitudeX, double latitudeY, double longitudeY, int capacity, int occupation, String lotId) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.latitudeX = latitudeX;
        this.longitudeX = longitudeX;
        this.latitudeY = latitudeY;
        this.longitudeY = longitudeY;
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
        dest.writeDouble(this.latitudeX);
        dest.writeDouble(this.longitudeX);
        dest.writeDouble(this.latitudeY);
        dest.writeDouble(this.longitudeY);
        dest.writeInt(this.capacity);
        dest.writeInt(this.occupation);
        dest.writeString(this.lotId);
    }

    private void readFromParcel(Parcel in) {

        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.latitudeX = in.readDouble();
        this.longitudeX = in.readDouble();
        this.latitudeY = in.readDouble();
        this.longitudeY = in.readDouble();
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
