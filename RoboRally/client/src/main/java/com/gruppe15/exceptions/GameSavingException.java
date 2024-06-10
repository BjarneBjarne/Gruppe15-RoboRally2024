package com.gruppe15.exceptions;

public class GameSavingException extends Exception {
    /**
     * Constructs an {@code GameSavingException} without a
     * detail message.
     */
    public GameSavingException() {
        super();
    }

    /**
     * Constructs an {@code GameSavingException} with a detail message.
     * @param s The detail message.
     */
    public GameSavingException(String s) {
        super(s);
    }
}
