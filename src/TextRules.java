import java.util.Locale;

/**
 * Shared text validation for the PackCheck model classes. Every model
 * constructor routes its fields through here so the trimming, length, and
 * capitalization rules from the specification live in one place.
 *
 * @author erikkwon
 * @version October 11, 2026
 */
final class TextRules
{
    /** Maximum length of an item name after trimming. */
    static final int MAX_ITEM_NAME = 60;
    /** Maximum length of an item category or reminder topic. */
    static final int MAX_CATEGORY = 30;
    /** Maximum length of reminder text after trimming. */
    static final int MAX_REMINDER_TEXT = 200;
    /** Maximum length of a traveler name after trimming. */
    static final int MAX_TRAVELER_NAME = 40;

    /**
     * Not instantiable.
     */
    private TextRules()
    {
        // static helpers only
    }

    /**
     * Trims a field and checks that it is present and short enough.
     *
     * @param value     the raw text
     * @param fieldName the field's name, for error messages
     * @param maxLength the longest allowed trimmed length
     * @return the trimmed text, with its case preserved
     * @throws IllegalArgumentException if value is null, blank, or too long
     */
    static String trimmed(String value, String fieldName, int maxLength)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        String result = value.trim();
        if (result.isEmpty())
        {
            throw new IllegalArgumentException(
                fieldName + " cannot be blank.");
        }
        if (result.length() > maxLength)
        {
            throw new IllegalArgumentException(fieldName
                + " must be at most " + maxLength + " characters.");
        }
        return result;
    }

    /**
     * Trims, validates, and lowercases a field using Locale.ROOT.
     *
     * @param value     the raw text
     * @param fieldName the field's name, for error messages
     * @param maxLength the longest allowed trimmed length
     * @return the trimmed, lowercased text
     * @throws IllegalArgumentException if value is null, blank, or too long
     */
    static String normalized(String value, String fieldName, int maxLength)
    {
        return trimmed(value, fieldName, maxLength).toLowerCase(Locale.ROOT);
    }
}