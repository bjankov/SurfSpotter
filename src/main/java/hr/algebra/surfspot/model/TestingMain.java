package hr.algebra.surfspot.model;

public class TestingMain {
    static void main(String[] args) {
        // System.out.println(WindDirection.fromDegrees(Integer.parseInt(args[0])));

        SurfingSchool school = new SurfingSchool("Surfing School");
        Instructor instructor = new Instructor("Ivo", "Ivic", school);

        System.out.println(instructor);

        SurfSpot spot = new SurfSpot.Builder()
                .id(1L)
                .name("Pen Gu")
                .difficulty(DifficultyLevel.EXPERT)
                .windDirectionDegrees(270)
                .build();
        System.out.println(spot);
    }
}
