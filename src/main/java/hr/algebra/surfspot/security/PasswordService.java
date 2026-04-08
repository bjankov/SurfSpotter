package hr.algebra.surfspot.security;

public interface PasswordService {
    String hash(String plainPassword);
    boolean verify(String plainPassword, String hashedPassword);
}
