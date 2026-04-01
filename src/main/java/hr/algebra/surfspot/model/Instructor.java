package hr.algebra.surfspot.model;

import java.util.Objects;

public class Instructor {
    private Long id;
    private String firstName;
    private String lastName;
    private SurfingSchool school;

    public Instructor() {
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
