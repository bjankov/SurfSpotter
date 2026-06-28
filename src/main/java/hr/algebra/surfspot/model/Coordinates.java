package hr.algebra.surfspot.model;

import hr.algebra.surfspot.exception.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public record Coordinates(BigDecimal latitude, BigDecimal longitude) {
    public Coordinates {
        if (
                latitude == null ||
                latitude.compareTo(new BigDecimal("-90")) <= 0 ||
                latitude.compareTo(new BigDecimal("90")) >= 0)
        {
            throw new ValidationException("Geografska širina mora biti između -90 i 90!");
        }

        if (
                longitude == null ||
                longitude.compareTo(new BigDecimal("-180")) <= 0 ||
                longitude.compareTo(new BigDecimal("180")) >= 0)
        {
            throw new ValidationException("Geografska dužina mora biti između -180 i 180!");
        }
    }

    @Override
    @NotNull
    public String toString() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}
