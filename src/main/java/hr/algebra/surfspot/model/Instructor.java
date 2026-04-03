package hr.algebra.surfspot.model;

import java.util.Objects;

public class Instructor {
    private Long id;
    private String firstName;
    private String lastName;
    private SurfingSchool school;

    public static Builder builder() {
        return new Builder();
    }

    public Instructor(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.school = builder.school;
    }

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private SurfingSchool school;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder school(SurfingSchool school) {
            this.school  = school;
            return this;
        }

        public Builder from(Instructor instructor) {
            this.id = instructor.id;
            this.firstName = instructor.firstName;
            this.lastName = instructor.lastName;
            this.school = instructor.school;
            return this;
        }

        public Instructor build() {
            return new Instructor(this);
        }
    }

    public Instructor() {
    }

    public Instructor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Instructor(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Instructor(String firstName, String lastName, SurfingSchool school) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public SurfingSchool getSchool() {
        return school;
    }

    public void setSchool(SurfingSchool school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return String.format(
                "Instruktor [ID %d]:%n" +
                "Ime: %s%n" +
                "Prezime: %s%n" +
                "Škola: %s%n",
                id, firstName, lastName, (school != null ? school.getName() : "Samostalan"));
    }

    // TODO: Provjeri validnost equals metode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
