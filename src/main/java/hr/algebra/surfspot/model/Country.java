package hr.algebra.surfspot.model;

import hr.algebra.surfspot.exception.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Country (String code, String name){
    public Country {
        if (code == null || code.length() != 2) {
            throw new ValidationException("Country code length must be 2");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("Country name cannot be blank");
        }
        code = code.toUpperCase();
    }

    @Override
    @NotNull
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(code, country.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
