package hr.algebra.surfspot.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import hr.algebra.surfspot.util.DisplayConstants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JacksonXmlRootElement(localName = "SurfSpot")
public class SurfSpot implements Serializable {
    private transient Long id;
    private String name;
    private transient Location location;
    private transient WaveDetails waveDetails;
    private transient Integer windDirectionDegrees;
    private DifficultyLevel difficulty;
    @JacksonXmlElementWrapper(localName = "bestSeason")
    @JacksonXmlProperty(localName = "month")
    private Set<Month> bestSeason = EnumSet.noneOf(Month.class);
    private transient Set<Instructor> instructors = new HashSet<>();
    private transient String imagePath;

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
        this.imagePath = builder.imagePath;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Location location;
        private WaveDetails waveDetails;
        private Integer windDirectionDegrees;
        private DifficultyLevel difficulty;
        private String imagePath;
        private Set<Month> bestSeason = EnumSet.noneOf(Month.class);
        private Set<Instructor> instructors = new HashSet<>();

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

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
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
            this.imagePath = surfSpot.imagePath;
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

    public String getFormattedWindDetails() {
        WindDirection direction = WindDirection.fromDegrees(windDirectionDegrees);
        return String.format("%s (%d°)", direction.getDisplayValue(), windDirectionDegrees);
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

    public String getCountryName() {
        return location.getCoast().getCountry().name();
    }

    public Long getCoastId() {
        return location.getCoast().getId();
    }

    public WaveType getWaveType() {
        return waveDetails.getWaveType();
    }

    public String getDifficultyDisplayValue() {
        return difficulty.getDisplayValue();
    }

    public Double getWaveHeight() {
        return waveDetails.getWaveHeight();
    }

    public String getFormattedBestSeason() {
        if (bestSeason == null || bestSeason.isEmpty()) {
            return DisplayConstants.NOT_ENTERED;
        }
        return bestSeason.stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format(
                "Surf Spot [ID %d]:%n" +
                "Naziv: %s%n" +
                "Lokacija: %s%n" +
                "Tip vala: %s%n" +
                "Visina vala: %s%n" +
                "Smjer vjetra: %s%n" +
                "Težina: %s%n" +
                "Sezona: %s%n" +
                "Instruktori: %s%n" +
                "Fotografija: %s",
                id,
                name,
                location,
                waveDetails.getWaveType().getDisplayValue(),
                waveDetails.getWaveHeight() != null ? waveDetails.getWaveHeight() + "m" : DisplayConstants.NOT_ENTERED,
                windDirectionDegrees != null ? windDirectionDegrees + "° " + WindDirection.fromDegrees(windDirectionDegrees).getDisplayValue() : DisplayConstants.NOT_ENTERED,
                difficulty != null ? difficulty.getDisplayValue() : DisplayConstants.NOT_ENTERED,
                !bestSeason.isEmpty() ? bestSeason.toString() : DisplayConstants.NOT_ENTERED,
                !instructors.isEmpty() ? instructors.toString() : DisplayConstants.NOT_ENTERED,
                imagePath != null ? imagePath : DisplayConstants.NOT_ENTERED
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurfSpot surfSpot = (SurfSpot) o;

        return id != null && id.equals(surfSpot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}