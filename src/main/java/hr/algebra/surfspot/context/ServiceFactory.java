package hr.algebra.surfspot.context;

import hr.algebra.surfspot.security.BCryptPasswordService;
import hr.algebra.surfspot.security.PasswordService;
import hr.algebra.surfspot.service.*;
import hr.algebra.surfspot.service.impl.*;
import hr.algebra.surfspot.validation.UserValidator;

public class ServiceFactory {
    private final PasswordService passwordService;
    private final AuthService authService;
    private final InstructorService instructorService;
    private final SurfSpotService surfSpotService;
    private final SurfingSchoolService surfingSchoolService;
    private final CoastService coastService;
    private final CountryService countryService;
    private final UserService userService;

    public ServiceFactory(RepositoryFactory repositories) {
        this.passwordService = new BCryptPasswordService();

        UserValidator userValidator = new UserValidator(repositories.getUserRepository());
        this.authService = new AuthServiceImpl(
                repositories.getUserRepository(),
                passwordService,
                userValidator
        );

        this.instructorService = new InstructorServiceImpl(repositories.getInstructorRepository());
        this.surfSpotService = new SurfSpotServiceImpl(repositories.getSurfSpotRepository());
        this.surfingSchoolService = new SurfingSchoolServiceImpl(repositories.getSurfingSchoolRepository());
        this.coastService = new CoastServiceImpl(repositories.getCoastRepository());
        this.countryService = new CountryServiceImpl(repositories.getCountryRepository());
        this.userService = new UserServiceImpl(repositories.getUserRepository());
    }

    public PasswordService getPasswordService() { return passwordService; }
    public AuthService getAuthService() { return authService; }
    public InstructorService getInstructorService() { return instructorService; }
    public SurfSpotService getSurfSpotService() { return surfSpotService; }
    public SurfingSchoolService getSurfingSchoolService() { return surfingSchoolService; }
    public CoastService getCoastService() { return coastService; }
    public CountryService getCountryService() { return countryService; }
    public UserService getUserService() { return userService; }
}