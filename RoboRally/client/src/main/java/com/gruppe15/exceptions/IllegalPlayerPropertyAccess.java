package com.gruppe15.exceptions;

public class IllegalPlayerPropertyAccess extends Exception {
    /**
     * Constructs an {@code IllegalAccessException} without a
     * detail message.
     */
    public IllegalPlayerPropertyAccess() {
        super();
    }

    /**
     * Constructs an {@code IllegalAccessException} with a detail message.
     * @param s The detail message.
     */
    public IllegalPlayerPropertyAccess(String s) {
        super(s);
    }
}
