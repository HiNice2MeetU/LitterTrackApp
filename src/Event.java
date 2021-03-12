public class Event {
    private double Latitude;
    private double Longitude;
    private String Bmp;
    private String DisplayName;

    public Event(double latitude, double longitude, String bmp) {
        Latitude = latitude;
        Longitude = longitude;
        Bmp = bmp;
    }

    public Event(double latitude, double longitude, String bmp, String displayName) {
        Latitude = latitude;
        Longitude = longitude;
        Bmp = bmp;
        DisplayName = displayName;
    }

    public Event() {
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getBmp() {
        return Bmp;
    }

    public void setBmp(String bmp) {
        Bmp = bmp;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
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

