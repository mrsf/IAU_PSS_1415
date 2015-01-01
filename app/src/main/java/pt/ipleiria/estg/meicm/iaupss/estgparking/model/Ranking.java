package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

public class Ranking {

    private String id;
    private String name;
    private String email;
    private int score;
    private String imagePath;

    public Ranking() {}

    public Ranking(String id, String name, String email, int score, String imagePath) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.score = score;
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

    public float getRating() {
        return (this.score * 5) / 10000;
    }
}
