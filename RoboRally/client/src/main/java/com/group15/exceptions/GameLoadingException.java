package com.group15.exceptions;

public class GameLoadingException extends Exception {
    /**
     * Constructs an {@code GameLoadingException} without a
     * detail message.
     */
    public GameLoadingException() {
        super();
    }

    /**
     * Constructs an {@code GameLoadingException} with a detail message.
     * @param s The detail message.
     */
    public GameLoadingException(String s) {
        super(s);
    }
}
