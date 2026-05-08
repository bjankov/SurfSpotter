package hr.algebra.surfspot.context;

import hr.algebra.surfspot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSession {
    private static final Logger log = LoggerFactory.getLogger(UserSession.class);

    private User currentUser;

    public void login(User user) {
        this.currentUser = user;
        log.info("Korisnik session postavljen: {}", user != null ? user.getUsername() : "null");
    }

    public void logout() {
        log.info("Korisnik {} se odjavljuje", currentUser != null ? currentUser.getUsername() : "unknown");
        this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}