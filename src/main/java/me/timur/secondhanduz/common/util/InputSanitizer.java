package me.timur.secondhanduz.common.util;

/**
 * Port for input sanitization — enables clean mocking in unit tests.
 */
public interface InputSanitizer {

    String sanitize(String input);
}
