package hr.algebra.surfspot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class SurfSpot {
    private static final Logger log = LoggerFactory.getLogger(SurfSpot.class);

    private Long id;
    private String name;
    private Location location;
    private WaveDetails waveDetails;
    private Integer windDirectionDegrees;
    private DifficultyLevel difficulty;
    private Set<Month> bestSeason = EnumSet.noneOf(Month.class);
    private Set<Instructor> instructors = new HashSet<>();
    private String imagePath;

    public static Builder builder() {
        return new Builder();
    }

    public SurfSpot() {
    }

    public SurfSpot(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.location = builder.location;
        this.waveDetails = builder.waveDetails;
        this.windDirectionDegrees = builder.windDirectionDegrees;
        this.difficulty = builder.difficulty;
        this.bestSeason = builder.bestSeason;
        this.instructors = builder.instructors;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Location location;
        private WaveDetails waveDetails;
        private Integer windDirectionDegrees;
        private DifficultyLevel difficulty;
        private Set<Month> bestSeason = EnumSet.noneOf(Month.class);
        private Set<Instructor> instructors = new HashSet<>();
        private Coordinates coordinates;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder waveDetails(WaveDetails waveDetails) {
            this.waveDetails = waveDetails;
            return this;
        }

        public Builder windDirectionDegrees(Integer windDirectionDegrees) {
            this.windDirectionDegrees = windDirectionDegrees;
            return this;
        }

        public Builder difficulty(DifficultyLevel difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder bestSeason(Set<Month> bestSeason) {
            this.bestSeason = bestSeason;
            return this;
        }

        public Builder instructor(Set<Instructor> instructors) {
            this.instructors = instructors;
            return this;
        }

        public Builder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public Builder from(SurfSpot surfSpot) {
            this.id = surfSpot.id;
            this.name = surfSpot.name;
            this.location = surfSpot.location;
            this.waveDetails = surfSpot.waveDetails;
            this.windDirectionDegrees = surfSpot.windDirectionDegrees;
            this.difficulty = surfSpot.difficulty;
            this.bestSeason = surfSpot.bestSeason;
            this.instructors = surfSpot.instructors;
            this.coordinates = surfSpot.location.getCoordinates();
            return this;
        }

        public SurfSpot build() {
            return new SurfSpot(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public WaveDetails getWaveDetails() {
        return waveDetails;
    }

    public void setWaveDetails(WaveDetails waveDetails) {
        this.waveDetails = waveDetails;
    }

    public Integer getWindDirectionDegrees() {
        return windDirectionDegrees;
    }

    public void setWindDirectionDegrees(Integer windDirectionDegrees) {
        this.windDirectionDegrees = windDirectionDegrees;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public Set<Month> getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(Set<Month> bestSeason) {
        this.bestSeason = bestSeason;
    }

    public Set<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(Set<Instructor> instructors) {
        this.instructors = instructors;
    }

    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getLatitude() {
        return location.getCoordinates().latitude();
    }

    public BigDecimal getLongitude() {
        return location.getCoordinates().longitude();
    }

    public String getCountryCode() {
        return location.getCoast().getCountry().code();
    }

    public Long getCoastId() {
        return location.getCoast().getId();
    }

    public WaveType getWaveType() {
        return waveDetails.getWaveType();
    }

    public Double getWaveHeight() {
        return waveDetails.getWaveHeight();
    }


    @Override
    public String toString() {
        return String.format(
            "Surf Spot [ID %d]:%n" +
            "Naziv: %s%n" +
            "Lokacija: %s%n" +
            "Tip valova: %s%n" +
            "Smjer vjetra: %s (%s)%n" +
            "Tezina: %s%n" +
            "Mjeseci sezone: %s%n" +
            "Instruktori: %s%n" +
            "Path fotografije: %s",
            id,
            name,
            (location != null ? location : "Nije uneseno"),
            (waveDetails != null ? waveDetails.getWaveType().getDisplayValue() : "Nije uneseno"),
            windDirectionDegrees, WindDirection.fromDegrees(windDirectionDegrees).getDisplayValue(),
            (difficulty != null ? difficulty.getDisplayValue() : "Nije uneseno"),
            (!bestSeason.isEmpty() ? bestSeason.toString() : "Nije uneseno"),
            (!instructors.isEmpty() ? instructors.toString() : "Nije uneseno"),
            imagePath);
    }
}