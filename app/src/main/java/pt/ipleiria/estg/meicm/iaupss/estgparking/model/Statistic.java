package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

public class Statistic {

    private String id;
    private String weekDay;
    private String hour;
    private int occupation;
    private String sectionId;

    public Statistic() {}

    public Statistic(String id, String weekDay, String hour, int occupation, String sectionId) {

        this.id = id;
        this.weekDay = weekDay;
        this.hour = hour;
        this.occupation = occupation;
        this.sectionId = sectionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getOccupation() {
        return occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
