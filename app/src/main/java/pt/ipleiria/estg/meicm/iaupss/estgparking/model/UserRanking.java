package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

public class UserRanking {

    private String id;
    private String name;
    private int points;
    private String imagePath;

    public UserRanking() {}

    public UserRanking(String id, String name, int points, String imagePath) {

        this.id = id;
        this.name = name;
        this.points = points;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
