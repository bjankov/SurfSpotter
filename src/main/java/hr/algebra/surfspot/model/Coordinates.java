package hr.algebra.surfspot.model;

public record Coordinates(Double latitude, Double longitude) {
    public Coordinates {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Geografska dužina mora biti između -90 i 90!");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Geografska širina mora biti između -180 i 180!");
        }
    }

    @Override
    public String toString() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}
