package hr.algebra.surfspot.model;

public class Coast {
    private Long id;
    private String name;
    private Country country;

    public Coast() {
    }

    public Coast(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }
}
