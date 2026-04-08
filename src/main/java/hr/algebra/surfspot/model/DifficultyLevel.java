package hr.algebra.surfspot.model;

public enum DifficultyLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert"),
    PROFESSIONAL("Professional");

    private final String displayValue;

    DifficultyLevel(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
