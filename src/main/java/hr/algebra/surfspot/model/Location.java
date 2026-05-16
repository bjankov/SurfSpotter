package hr.algebra.surfspot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Location {
    private static final Logger log = LoggerFactory.getLogger(Location.class);

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
