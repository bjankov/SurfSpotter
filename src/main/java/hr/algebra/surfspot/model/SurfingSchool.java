package hr.algebra.surfspot.model;

public class SurfingSchool {
    private Long id;
    private String name;

    public static Builder builder() {
        return new Builder();
    }

    public SurfingSchool(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public static class Builder {
        private Long id;
        private String name;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public SurfingSchool build() {
            return new SurfingSchool(this);
        }

        public Builder from(SurfingSchool school) {
            this.id = school.id;
            this.name = school.name;
            return this;
        }
    }

    public SurfingSchool() {
    }

    public SurfingSchool(String name) {
        this.name = name;
    }

    public SurfingSchool(Long id, String name) {
        this.id = id;
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
        return this.name;
    }
}
