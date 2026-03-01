package me.timur.secondhanduz.common.util;

import org.springframework.stereotype.Component;

/**
 * Utility for sanitizing user-generated content to prevent XSS.
 */
@Component
public class ContentSanitizer implements InputSanitizer {

    private static final String[] DANGEROUS_PATTERNS = {
            "<script", "</script>", "javascript:", "onerror=", "onload=",
            "<iframe", "</iframe>", "eval(", "expression("
    };

    /**
     * Remove potentially dangerous HTML/JS patterns from user input.
     *
     * @param input raw user text
     * @return sanitized string, or null if input is null
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String result = input.trim();
        for (String pattern : DANGEROUS_PATTERNS) {
            result = result.replace(pattern, "");
            result = result.replace(pattern.toUpperCase(), "");
        }
        return result;
    }
}
