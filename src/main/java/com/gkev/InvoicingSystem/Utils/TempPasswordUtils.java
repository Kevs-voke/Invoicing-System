package com.gkev.InvoicingSystem.Utils;

import java.security.SecureRandom;

public final class TempPasswordUtils {

    private TempPasswordUtils() {} // prevent instantiation

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"; 
    private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SYMBOLS = "!@#$%&*";
    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a random 12-character temporary password containing at least
     * one uppercase letter, one lowercase letter, one digit, and one symbol.
     */
    public static String generate() {
        StringBuilder password = new StringBuilder(12);
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));

        for (int i = 4; i < 12; i++) {
            password.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        for (int i = password.length() - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }
}
