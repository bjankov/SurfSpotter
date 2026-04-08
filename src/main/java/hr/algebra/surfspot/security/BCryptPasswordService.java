package hr.algebra.surfspot.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordService implements PasswordService {

   private static final Integer ROUNDS = 12;

    @Override
    public String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(ROUNDS));
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
