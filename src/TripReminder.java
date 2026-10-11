/**
 * An immutable trip reminder with case-preserved text and a normalized topic.
 * Reminders intentionally retain identity equality.
 *
 * @author Wenxin Zhang
 * @version October 9, 2026
 */
public class TripReminder
{
    // ~ Fields ................................................................
    private final String text;
    private final String topic;

    // ~ Constructors ..........................................................
    /**
     * Creates a reminder after validating and normalizing its fields.
     *
     * @param text
     *            reminder text, at most 200 characters after trimming
     * @param topic
     *            reminder topic, at most 30 characters after trimming
     * @throws IllegalArgumentException
     *             if either field is null, blank, or too long
     */
    public TripReminder(String text, String topic)
    {
        this.text = TextRules
            .trimmed(text, "Reminder text", TextRules.MAX_REMINDER_TEXT);
        this.topic =
            TextRules.normalized(topic, "Topic", TextRules.MAX_CATEGORY);
    }


    // ~ Public Methods ........................................................
    /**
     * Gets the trimmed reminder text with its original case preserved.
     *
     * @return the reminder text
     */
    public String getText()
    {
        return text;
    }


    /**
     * Gets the normalized reminder topic.
     *
     * @return the topic
     */
    public String getTopic()
    {
        return topic;
    }


    /**
     * Formats the reminder as "Bring ID [travel]".
     *
     * @return the formatted reminder
     */
    @Override
    public String toString()
    {
        return text + " [" + topic + "]";
    }
}
