package center.bedwars.api.util;

import org.bukkit.ChatColor;

public final class TextUtil {

    private static final int MAX_PREFIX_SUFFIX_LENGTH = 16;
    private static final int MAX_TITLE_LENGTH = 32;
    private static final int MAX_LINE_LENGTH = 40;

    private TextUtil() {
    }

    public static String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    public static String truncateForPrefixSuffix(String text) {
        return truncate(text, MAX_PREFIX_SUFFIX_LENGTH);
    }

    public static String truncateForTitle(String text) {
        return truncate(text, MAX_TITLE_LENGTH);
    }

    public static String truncateForLine(String text) {
        return truncate(text, MAX_LINE_LENGTH);
    }

    public static String escapeJson(String text) {
        if (text == null)
            return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String extractLastColor(String text) {
        if (text == null || text.isEmpty())
            return "";

        StringBuilder color = new StringBuilder();
        for (int i = text.length() - 2; i >= 0; i--) {
            if (text.charAt(i) == ChatColor.COLOR_CHAR) {
                char code = text.charAt(i + 1);
                if (isValidColorCode(code)) {
                    color.insert(0, String.valueOf(ChatColor.COLOR_CHAR) + code);
                    if (!isFormatCode(code)) {
                        break;
                    }
                }
            }
        }
        return color.toString();
    }

    public static boolean isValidColorCode(char code) {
        return "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(code) > -1;
    }

    public static boolean isFormatCode(char code) {
        return "KkLlMmNnOo".indexOf(code) > -1;
    }

    public static String[] splitLine(String line) {
        if (line == null)
            line = "";

        String truncated = truncateForLine(line);

        if (truncated.length() <= MAX_PREFIX_SUFFIX_LENGTH) {
            return new String[] { truncated, "" };
        }

        String prefix = truncated.substring(0, MAX_PREFIX_SUFFIX_LENGTH);
        String remaining = truncated.substring(MAX_PREFIX_SUFFIX_LENGTH);

        String lastColor = extractLastColor(prefix);
        String suffix = lastColor + remaining;

        if (suffix.length() > MAX_PREFIX_SUFFIX_LENGTH) {
            suffix = suffix.substring(0, MAX_PREFIX_SUFFIX_LENGTH);
        }

        return new String[] { prefix, suffix };
    }
}
