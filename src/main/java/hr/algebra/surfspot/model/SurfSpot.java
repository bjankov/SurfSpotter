package hr.algebra.surfspot.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import hr.algebra.surfspot.util.DisplayConstants;

import java.math.BigDecimal;
import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JacksonXmlRootElement(localName = "SurfSpot")
public class SurfSpot {
    @JsonIgnore
    private Long id;
    private String name;
    @JsonIgnore
    private Location location;
    @JsonIgnore
    private WaveDetails waveDetails;
    @JsonIgnore
    private Integer windDirectionDegrees;
    private DifficultyLevel difficulty;
    @JacksonXmlElementWrapper(localName = "bestSeason")
    @JacksonXmlProperty(localName = "month")
    private Set<Month> bestSeason = EnumSet.noneOf(Month.class);
    @JsonIgnore
    private Set<Instructor> instructors = new HashSet<>();
    @JsonIgnore
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

    public WaveDetails getWaveDetails() {
        return waveDetails;
    }

    public Integer getWindDirectionDegrees() {
        return windDirectionDegrees;
    }

    public String getFormattedWindDetails() {
        WindDirection direction = WindDirection.fromDegrees(windDirectionDegrees);
        return String.format("%s (%d°)", direction.getDisplayValue(), windDirectionDegrees);
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    // Used reflectively
    @SuppressWarnings("unused")
    public String getDifficultyDisplayValue() {
        return difficulty.getDisplayValue();
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

    public String getImagePath() {
        return imagePath;
    }

    public BigDecimal getLatitude() {
        return location.getCoordinates().latitude();
    }

    public BigDecimal getLongitude() {
        return location.getCoordinates().longitude();
    }

    public String getCountryName() {
        return location.getCoast().getCountry().name();
    }

    public Long getCoastId() {
        return location.getCoast().getId();
    }

    public String getCoastName() {
        return location.getCoast().getName();
    }

    public WaveType getWaveType() {
        return waveDetails.getWaveType();
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

    public Country getCountry() {
        return location.getCoast().getCountry();
    }

    @Override
    public String toString() {
        return getName() + ", " + getCoastName() + ", " + getCountryName();
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