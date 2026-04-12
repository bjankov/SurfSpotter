package hr.algebra.surfspot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Coast {
    private static final Logger log = LoggerFactory.getLogger(Coast.class);

    private Long id;
    private String name;
    private Country country;

    public static Builder builder() {
        return new Builder();
    }

    public Coast(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.country = builder.country;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Country country;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder country(Country country) {
            this.country  = country;
            return this;
        }

        public Builder from(Coast coast) {
            this.id = coast.id;
            this.name = coast.name;
            this.country = coast.country;
            return this;
        }

        public Coast build() {
            return new Coast(this);
        }
    }

    public Coast() {
    }

    public Coast(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public Coast(Long id, String name, Country country) {
        this.id = id;
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

    @Override
    public String toString() {
        return String.format("Country: %s, Name: %s", country, name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coast coast = (Coast) o;
        return Objects.equals(id, coast.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
