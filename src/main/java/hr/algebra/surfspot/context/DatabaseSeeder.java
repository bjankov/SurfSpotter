package hr.algebra.surfspot.context;

import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.RoleRepository;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.security.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DatabaseSeeder {
    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    public DatabaseSeeder(UserRepository userRepository, RoleRepository roleRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
    }

    public void seed() {
        seedAdmin();
    }

    private void seedAdmin() {
        if (userRepository.findByName("admin").isEmpty()) {

            Optional<Role> adminRoleOptional = roleRepository.findByName("ADMIN");

            if (adminRoleOptional.isPresent()) {
                Role adminRole = adminRoleOptional.get();

                User admin = User.builder()
                        .username("admin")
                        .email("admin@surfspot.hr")
                        .passwordHash(passwordService.hash("admin"))
                        .addRole(adminRole)
                        .build();

                userRepository.save(admin);
                log.info("Admin user successfully seeded.");
            } else {
                log.error("Cannot seed admin user: Role 'ADMIN' not found in database. Ensure init.sql is executed.");
            }
        }
    }
}