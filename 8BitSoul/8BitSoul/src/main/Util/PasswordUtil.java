package main.Util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            RANDOM.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return ALGORITHM + "$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Imposible generar hash de contraseña.", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        if (!storedHash.contains("$")) {
            // Compatibilidad con registros antiguos sin hash
            return password.equals(storedHash);
        }

        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) {
            return password.equals(storedHash);
        }

        String algorithm = parts[0];
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
            byte[] actualHash = skf.generateSecret(spec).getEncoded();
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    public static String generateRecoveryCode() {
        String charset = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder token = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            token.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return token.toString();
    }
}
