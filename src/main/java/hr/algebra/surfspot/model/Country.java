package hr.algebra.surfspot.model;

public record Country (String code, String name){

    public Country {
        if (code == null || code.length() != 2) {
            throw new IllegalArgumentException("Country code length must be 2");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Country name cannot be blank");
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
