package jarvis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * Parses and formats user-supplied dates/times for Jarvis.
 */
public class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");

    private static final DateTimeFormatter STORAGE_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter STORAGE_DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    private static final List<DateTimeFormatter> STORAGE_DATE_TIME_FORMATTERS = List.of(
            formatter("uuuu-MM-dd HH:mm"),
            formatter("uuuu-MM-dd HHmm"));

    private static final List<DateTimeFormatter> STORAGE_DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"));

    private static final List<DateTimeFormatter> USER_DATE_TIME_FORMATTERS = List.of(
            formatter("uuuu-MM-dd HHmm"),
            formatter("uuuu-MM-dd HH:mm"),
            formatter("d/M/uuuu HHmm"),
            formatter("d/M/uuuu HH:mm"));

    private static final List<DateTimeFormatter> USER_DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"),
            formatter("d/M/uuuu"));

    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Returns a date/time parsed from user input.
     *
     * @param raw User-supplied text.
     * @param usageMessage Message to show if parsing fails.
     */
    public static ParsedDateTime parseUserDateTime(String raw, String usageMessage) throws JarvisException {
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new JarvisException(usageMessage);
        }

        for (DateTimeFormatter dateTimeFormatter : USER_DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime value = LocalDateTime.parse(trimmed, dateTimeFormatter);
                return new ParsedDateTime(value, true);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }

        for (DateTimeFormatter dateFormatter : USER_DATE_FORMATTERS) {
            try {
                LocalDate value = LocalDate.parse(trimmed, dateFormatter);
                return new ParsedDateTime(value.atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }

        throw new JarvisException(usageMessage);
    }

    /**
     * Returns a date/time parsed from the data file.
     *
     * @param raw Stored value.
     * @param lineNumber Data file line number (index starts from 1).
     */
    public static ParsedDateTime parseStoredDateTime(String raw, int lineNumber) throws JarvisException {
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new JarvisException("Data file corrupted at line " + lineNumber + ": empty date/time.");
        }

        for (DateTimeFormatter dateTimeFormatter : STORAGE_DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(trimmed, dateTimeFormatter), true);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }

        for (DateTimeFormatter dateFormatter : STORAGE_DATE_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDate.parse(trimmed, dateFormatter).atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }

        throw new JarvisException("Data file corrupted at line " + lineNumber + ": invalid date/time.");
    }

    /**
     * Formats a date/time for display to the user.
     */
    public static String formatForDisplay(LocalDateTime value, boolean hasTime) {
        if (hasTime) {
            return value.format(DISPLAY_DATE_TIME);
        }
        return value.format(DISPLAY_DATE);
    }

    /**
     * Formats a date/time for saving to the data file.
     */
    public static String formatForStorage(LocalDateTime value, boolean hasTime) {
        if (hasTime) {
            return value.format(STORAGE_DATE_TIME);
        }
        return value.format(STORAGE_DATE);
    }

    /**
     * Returns a date/time value guaranteed to contain only minute-level precision.
     */
    public static LocalDateTime normalize(LocalDateTime value) {
        return value.withSecond(0).withNano(0);
    }

    /**
     * Container for a parsed date/time and whether a time component was provided.
     */
    public static class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        private ParsedDateTime(LocalDateTime value, boolean hasTime) {
            this.value = normalize(value);
            this.hasTime = hasTime;
        }

        public LocalDateTime getValue() {
            return value;
        }

        public boolean hasTime() {
            return hasTime;
        }
    }
}
