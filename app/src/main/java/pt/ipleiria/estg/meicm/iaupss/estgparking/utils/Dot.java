package pt.ipleiria.estg.meicm.iaupss.estgparking.utils;

public class Dot {

    private double lat;
    private double lng;

    public Dot(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
