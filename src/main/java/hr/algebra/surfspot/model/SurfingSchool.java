package hr.algebra.surfspot.model;

public class SurfingSchool {
    private Long id;
    private String name;

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
}
