import student.TestCase;

/**
 * Tests TripReminder validation, normalization, and formatting.
 *
 * @author Wenxin Zhang
 * @version October 11, 2026
 */
public class TripReminderTest
    extends TestCase
{
    /**
     * Tests trimming, case behavior, getters, and display formatting.
     */
    public void testConstructionAndAccessors()
    {
        TripReminder reminder = new TripReminder(" Bring ID ", "TRAVEL");
        assertEquals("Bring ID", reminder.getText());
        assertEquals("travel", reminder.getTopic());
        assertEquals("Bring ID [travel]", reminder.toString());
    }


    /**
     * Tests constructor validation for reminder text and topic.
     */
    public void testBadConstructorInput()
    {
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    null,
                    "travel")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    "   ",
                    "travel")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    "x".repeat(201),
                    "t")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    "Bring ID",
                    null)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    "Bring ID",
                    "   ")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new TripReminder(
                    "Bring ID",
                    "x".repeat(31))) instanceof IllegalArgumentException);
    }


    /**
     * Tests maximum-length boundary values and text case preservation.
     */
    public void testMaximumLengths()
    {
        TripReminder reminder =
            new TripReminder("A".repeat(200), "B".repeat(30));
        assertEquals(200, reminder.getText().length());
        assertEquals("A".repeat(200), reminder.getText());
        assertEquals("b".repeat(30), reminder.getTopic());
    }


    /**
     * Confirms that reminders retain identity equality as specified.
     */
    public void testIdentityEquality()
    {
        TripReminder first = new TripReminder("Bring ID", "travel");
        TripReminder second = new TripReminder("Bring ID", "travel");
        assertTrue(first.equals(first));
        assertFalse(first.equals(second));
    }


    /**
     * Runs an action and returns its exception, if any.
     *
     * @param action
     *            the action to run
     * @return the thrown exception, or null
     */
    private static Exception thrownBy(Runnable action)
    {
        try
        {
            action.run();
        }
        catch (Exception exception)
        {
            return exception;
        }
        return null;
    }
}
