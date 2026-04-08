package hr.algebra.surfspot.service;

import hr.algebra.surfspot.exception.DuplicateRecordException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.security.PasswordService;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public AuthService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public void register(String username, String password) {
        if (userRepository.findByName(username).isPresent()) {
            throw new DuplicateRecordException("Korisnik sa ovim korisnickim imenom vec postoji!");
        }
        String hashedPassword = passwordService.hash(password);
        User user =  User.builder()
                .username(username)
                .passwordHash(hashedPassword)
                .build();
        userRepository.save(user);
    }
}
