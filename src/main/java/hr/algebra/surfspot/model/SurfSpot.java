package hr.algebra.surfspot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SurfSpot {
    private Long id;
    private String name;
    private Location location;
    private WaveDetails waveDetails;
    private Integer windDirectionDegrees;
    private DifficultyLevel difficulty;
    private List<Month> bestSeason = new ArrayList<>();
    private List<Instructor> instructors = new ArrayList<>();

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
        private List<Month> bestSeason = new ArrayList<>();
        private List<Instructor> instructors = new ArrayList<>();

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

        public Builder bestSeason(List<Month> bestSeason) {
            this.bestSeason = bestSeason;
            return this;
        }

        public Builder instructor(List<Instructor> instructors) {
            this.instructors = instructors;
            return this;
        }

        public SurfSpot build() {
            return new SurfSpot(this);
        }
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
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

    public List<Month> getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(List<Month> bestSeason) {
        this.bestSeason = bestSeason;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructor(List<Instructor> instructors) {
        this.instructors = instructors;
    }

    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
    }

    @Override
    public String toString() {
        return String.format(
                "Surf Spot [ID %d]:%n" +
                        "Naziv: %s%n" +
                        "Lokacija: %s%n" +
                        "Tip valova: %s%n" +
                        "Smjer vjetra: %s%n" +
                        "Tezina: %s%n" +
                        "Mjeseci sezone: %s%n" +
                        "Instruktori: %s%n",
                id,
                name,
                (location != null ? location.getCoast().getName() : "Nije uneseno"),
                (waveDetails != null ? waveDetails.getWaveType().getDisplayValue() : "Nije uneseno"),
                windDirectionDegrees,
                (difficulty != null ? difficulty.getDisplayValue() : "Nije uneseno"),
                (!bestSeason.isEmpty() ? bestSeason.toString() : "Nije uneseno"),
                (!instructors.isEmpty() ? instructors.toString() : "Nije uneseno"));
    }
}