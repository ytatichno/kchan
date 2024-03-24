package com.maxdev.kchan.exceptions;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public class NonUniqueIdentifierException extends RuntimeException {
    boolean isEmail = false;

    public NonUniqueIdentifierException(String identifier, String message) {
        super(identifier + " : " + message);
    }

    public NonUniqueIdentifierException(String identifier, String message,
                                        boolean isEmail) {
        this(identifier, message);
        isEmail = true;
    }

    public boolean isEmail() {
        return isEmail;
    }

}
