package hr.algebra.surfspot.model;

public class Location {
    private final Coordinates coordinates;
    private Coast coast;

    public Location(Coordinates coordinates, Coast coast) {
        this.coordinates = coordinates;
        this.coast = coast;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Coast getCoast() {
        return coast;
    }

    public void setCoast(Coast coast) {
        this.coast = coast;
    }

    @Override
    public String toString() {
        return coast.getName() + " (" + coast.getCountry().code() + ")";
    }
}
