package hr.algebra.surfspot.model;

public class Location {
    private Coordinates coordinates;
    private Coast coast;

    public Location() {
    }

    public Location(Coordinates coordinates, Coast coast) {
        this.coordinates = coordinates;
        this.coast = coast;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coast getCoast() {
        return coast;
    }

    public void setCoast(Coast coast) {
        this.coast = coast;
    }
}
