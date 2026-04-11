package hr.algebra.surfspot.model;

import hr.algebra.surfspot.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public record Coordinates(BigDecimal latitude, BigDecimal longitude) {
    private static final Logger logger = LoggerFactory.getLogger(Coordinates.class);
    // TODO: Provjeri metode za validaciju
    public Coordinates {
        if (
                latitude == null ||
                latitude.compareTo(new BigDecimal("-90")) <= 0 ||
                latitude.compareTo(new BigDecimal("90")) >= 0)
        {
            throw new ValidationException("Geografska dužina mora biti između -90 i 90!");
        }

        if (
                longitude == null ||
                longitude.compareTo(new BigDecimal("-180")) <= 0 ||
                longitude.compareTo(new BigDecimal("180")) >= 0)
        {
            throw new ValidationException("Geografska širina mora biti između -180 i 180!");
        }
    }

    // TODO: Popravi toString() implementaciju zbog promjene double -> BigDecimal
    @Override
    public String toString() {
        return String.format("%.6f, %.6f", latitude.doubleValue(), longitude.doubleValue());
    }
}
