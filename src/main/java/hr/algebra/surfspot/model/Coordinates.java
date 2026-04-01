package hr.algebra.surfspot.model;

import java.math.BigDecimal;

public record Coordinates(BigDecimal latitude, BigDecimal longitude) {
    // TODO: Provjeri metode za validaciju
    public Coordinates {
        if (
                latitude == null ||
                latitude.compareTo(new BigDecimal("-90")) <= 0 ||
                latitude.compareTo(new BigDecimal("90")) >= 0)
        {
            throw new IllegalArgumentException("Geografska dužina mora biti između -90 i 90!");
        }

        if (
                longitude == null ||
                longitude.compareTo(new BigDecimal("-180")) <= 0 ||
                longitude.compareTo(new BigDecimal("180")) >= 0)
        {
            throw new IllegalArgumentException("Geografska širina mora biti između -180 i 180!");
        }
    }

    // TODO: Popravi toString() implementaciju zbog promjene double -> BigDecimal
    @Override
    public String toString() {
        return String.format("%.6f, %.6f", latitude.doubleValue(), longitude.doubleValue());
    }
}
