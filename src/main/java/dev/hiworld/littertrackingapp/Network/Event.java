package dev.hiworld.littertrackingapp.Network;

public class Event {
    private double Latitude;
    private double Longitude;
    private String Bmp;

    public Event(double latitude, double longitude, String bmp) {
        Latitude = latitude;
        Longitude = longitude;
        Bmp = bmp;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(int latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(int longitude) {
        Longitude = longitude;
    }

    public String getBmp() {
        return Bmp;
    }

    public void setBmp(String bmp) {
        Bmp = bmp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", Bmp=" + Bmp +
                '}';
    }
}
