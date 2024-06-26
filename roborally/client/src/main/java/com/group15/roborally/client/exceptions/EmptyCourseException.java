package com.group15.roborally.client.exceptions;

public class EmptyCourseException extends Exception {
    /**
     * Constructs an {@code EmptyCourseException} without a
     * detail message.
     */
    public EmptyCourseException() {
        super();
    }

    /**
     * Constructs an {@code EmptyCourseException} with a detail message.
     * @param s The detail message.
     */
    public EmptyCourseException(String s) {
        super(s);
    }
}
