package com.group15.exceptions;

public class NoCoursesException extends Exception {
    /**
     * Constructs an {@code NoCoursesException} with default message.
     */
    public NoCoursesException() {
        super("No courses found in courses folder.");
    }

    /**
     * Constructs an {@code NoCoursesException} with a detail message.
     * @param s The detail message.
     */
    public NoCoursesException(String s) {
        super(s);
    }
}
