package hr.algebra.surfspot.model;

import hr.algebra.surfspot.exception.ValidationException;

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
    public String toString() {
        return String.format(
                "Država [ID: %s]%n" +
                "Naziv: %s%n",
                code, name);
    }
}
