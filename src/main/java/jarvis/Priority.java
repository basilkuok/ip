package jarvis;

import java.util.Locale;

/**
 * Represents the priority level of a task.
 */
public enum Priority {
    NONE("none"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private static final String PRIORITY_USAGE =
            "Please use: priority <taskNumber> <low|medium|high|none>";

    private final String storageValue;

    Priority(String storageValue) {
        this.storageValue = storageValue;
    }

    /**
     * Returns a priority parsed from user input.
     *
     * @param raw User input priority text.
     * @throws JarvisException If the text is invalid.
     */
    public static Priority parseUserInput(String raw) throws JarvisException {
        Priority parsed = parseTokenOrNull(raw);
        if (parsed == null) {
            throw new JarvisException(PRIORITY_USAGE);
        }
        return parsed;
    }

    /**
     * Returns a priority parsed from the stored data file value.
     *
     * @param raw Stored priority value.
     * @throws JarvisException If the value is invalid.
     */
    static Priority parseStoredValue(String raw, int lineNumber) throws JarvisException {
        if (raw == null || raw.isBlank()) {
            return NONE;
        }

        Priority parsed = parseTokenOrNull(raw);
        if (parsed == null) {
            throw new JarvisException("Data file corrupted at line "
                    + lineNumber + ": invalid priority.");
        }
        return parsed;
    }

    /**
     * Returns the string to write to the data file.
     */
    String getStorageValue() {
        return storageValue;
    }

    /**
     * Returns the string to show to the user.
     */
    String getDisplayValue() {
        return name();
    }

    private static Priority parseTokenOrNull(String raw) {
        if (raw == null) {
            return null;
        }

        String token = raw.trim().toLowerCase(Locale.ROOT);
        switch (token) {
        case "none":
            return NONE;
        case "low":
            return LOW;
        case "medium":
            return MEDIUM;
        case "high":
            return HIGH;
        default:
            return null;
        }
    }
}
