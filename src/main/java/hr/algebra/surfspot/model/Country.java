package hr.algebra.surfspot.model;

public class Country {
    private Long id;
    private String name;

    public Country(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "Država [ID: %d]%n" +
                "Naziv: %s%n",
                id, name);
    }
}
