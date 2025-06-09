package br.com.savemed.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    private static final String SALT_PASS = "!#S@V3#!";

    public static String cryptPassword(String texto, boolean integracao) {
        if (texto.length() == 64 && integracao) {
            return texto;
        } else {
            return computeSHA256(texto + SALT_PASS).toUpperCase();
        }
    }

    private static String computeSHA256(String mensagem) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(mensagem.getBytes());

            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02X", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
