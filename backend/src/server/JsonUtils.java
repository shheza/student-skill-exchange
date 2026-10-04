package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight JSON parser and formatting utility.
 * Keeps the project 100% dependency-free for effortless compilation.
 */
public class JsonUtils {

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Extracts a string field value from a JSON string using regex.
     * @param json String payload
     * @param field Name of field (e.g. "name", "skillId")
     * @return Extracted value or empty string
     */
    public static String getString(String json, String field) {
        if (json == null || field == null) return "";
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return unescape(matcher.group(1));
        }
        return "";
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }
}