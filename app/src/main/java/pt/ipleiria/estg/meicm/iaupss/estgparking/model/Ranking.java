package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Ranking implements Parcelable {

    private int position;
    private String id;
    private String name;
    private String email;
    private int score;
    private String imagePath;
    private int topScore;

    public Ranking() {}

    public Ranking(Parcel in) {

        this.readFromParcel(in);
    }

    public Ranking(int position, String id, String name, String email, int score, String imagePath) {

        this.position = position;
        this.id = id;
        this.name = name;
        this.email = email;
        this.score = score;
        this.imagePath = imagePath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public float getRating() {
        return (this.getScore() * 5f) / topScore;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.position);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeInt(this.score);
        dest.writeString(this.imagePath);
    }

    private void readFromParcel(Parcel in) {

        this.position = in.readInt();
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.score = in.readInt();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<Ranking> CREATOR = new Parcelable.Creator<Ranking>() {

        @Override
        public Ranking createFromParcel(Parcel source) {
            return new Ranking(source);
        }

        @Override
        public Ranking[] newArray(int size) {
            return new Ranking[size];
        }
    };
}
