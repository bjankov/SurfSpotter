package hr.algebra.surfspot.context;

import hr.algebra.surfspot.repository.*;
import hr.algebra.surfspot.security.BCryptPasswordService;
import hr.algebra.surfspot.security.PasswordService;
import hr.algebra.surfspot.service.*;
import hr.algebra.surfspot.service.impl.*;
import hr.algebra.surfspot.validation.UserValidator;

public class ServiceRegistry {
    private final AuthService authService;
    private final InstructorService instructorService;
    private final SurfSpotService surfSpotService;
    private final SurfingSchoolService surfingSchoolService;
    private final CoastService coastService;
    private final CountryService countryService;
    private final UserService userService;
    private final PasswordService passwordService;

    public ServiceRegistry(RepositoryRegistry repositoryRegistry) {
        UserRepository userRepository = repositoryRegistry.getRepository(UserRepository.class);
        UserValidator userValidator = new UserValidator(userRepository);

        this.passwordService = new BCryptPasswordService();

        this.authService = new AuthServiceImpl(userRepository, passwordService, userValidator);

        this.surfSpotService = new SurfSpotServiceImpl(
                repositoryRegistry.getRepository(SurfSpotRepository.class)
        );
        this.instructorService = new InstructorServiceImpl(
                repositoryRegistry.getRepository(InstructorRepository.class)
        );

        this.surfingSchoolService = new SurfingSchoolServiceImpl(
                repositoryRegistry.getRepository(SurfingSchoolRepository.class)
        );

        this.coastService = new CoastServiceImpl(
                repositoryRegistry.getRepository(CoastRepository.class)
        );

        this.countryService = new CountryServiceImpl(
                repositoryRegistry.getRepository(CountryRepository.class)
        );

        this.userService = new UserServiceImpl(
                repositoryRegistry.getRepository(UserRepository.class)
        );
    }

    public AuthService getAuthService() { return authService; }

    public InstructorService getInstructorService() { return instructorService; }

    public SurfingSchoolService getSurfingSchoolService() { return surfingSchoolService; }

    public SurfSpotService getSurfSpotService() { return surfSpotService; }

    public CoastService getCoastService() { return coastService; }

    public CountryService getCountryService() { return countryService; }

    public UserService getUserService() { return userService; }

    public PasswordService getPasswordService() { return passwordService; }
}