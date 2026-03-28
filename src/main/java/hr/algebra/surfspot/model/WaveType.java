package hr.algebra.surfspot.model;

public enum WaveType {
    BEACH_BREAK("Beach Break"),
    REEF_BREAK("Reef Break"),
    POINT_BREAK("Point Break");

    private final String displayValue;

    WaveType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
