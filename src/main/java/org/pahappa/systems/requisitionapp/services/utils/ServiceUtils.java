package org.pahappa.systems.requisitionapp.services.utils;


import java.util.Base64;

public class ServiceUtils {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final String USERNAME_PATTERN =
            "^[A-Za-z0-9]+$";

    private static final String NAME_PATTERN =
            "^[A-Za-z]+$";

    private static final String PHONE_NUMBER_PATTERN =
            "^[0-9]{10}$";


    public static boolean containsAlphabetsOnly(String input) {
        return input.matches(NAME_PATTERN);
    }

    public static boolean containsAlphanumericsOnly(String input) {
        return input.matches(USERNAME_PATTERN);
    }

    public static boolean validateEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }

    public static String testStringInput(String input, String id) throws IllegalArgumentException {
        // handling empty input
        if (input != null && !input.isEmpty()) {
            input = input.strip();
            if (containsAlphabetsOnly(input))
                return input;
            else {
                throw new IllegalArgumentException(id + " must contain alphabets only");
            }


        } else {
            throw new IllegalArgumentException(id + " must not be empty. Try Again!");
        }
    }

    public static String testUserNameInput(String input) throws IllegalArgumentException {
        // handling empty input
        if (input != null && !input.isEmpty()) {
            input = input.strip();
            if (containsAlphanumericsOnly(input))
                return input;
            else {
                throw new IllegalArgumentException("User Name must contain alphabets or/and numbers only");
            }

        } else {
            throw new IllegalArgumentException("User Name must not be empty. Try Again!");
        }
    }

    public static String testEmailInput(String input) throws IllegalArgumentException {
        // handling empty input
        if (input != null && !input.isEmpty()) {
            input = input.strip();
            if (validateEmail(input))
                return input;
            else {
                throw new IllegalArgumentException("Email must contain alphabets and numbers only");
            }

        } else {
            throw new IllegalArgumentException("Email must not be empty. Try Again!");
        }
    }

    public static String testPhoneNumberInput(String input) throws IllegalArgumentException {
        // handling empty input
        if (input != null && !input.isEmpty()) {
            input = input.strip();
            if (validatePhoneNumber(input))
                return input;
            else {
                throw new IllegalArgumentException("Phone Number must contain numbers only");
            }

        } else {
            throw new IllegalArgumentException("Phone Number must not be empty. Try Again!");
        }
    }

    public static String testPasswordInput(String input) throws IllegalArgumentException {
        if (input != null && !input.isEmpty()) {
            if (input.length() >= 3) {
                if ((input.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{3,}$"))) {
                    return Base64.getEncoder().encodeToString(input.getBytes());

                } else throw new IllegalArgumentException("Password must contain at least 1 (Uppercase, Lowercase letter and special character)");
            }
            else throw new IllegalArgumentException("Password must contain at least 3 Characters");
        }
        else throw new IllegalArgumentException("Password must not be empty. Try Again!");
    }
}
