import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests for PackCheckApp. Each test feeds a scripted session through run() and
 * checks the printed output, so these also act as the specification's
 * integration tests for menu workflows and bad input.
 *
 * @author erikkwon
 * @version October 11, 2026
 */
public class PackCheckAppTest
    extends TestCase
{
    // ~ Public Methods ........................................................
    /**
     * Tests that valid streams construct an app that can run.
     */
    public void testConstructor()
    {
        PackCheckApp app = new PackCheckApp(
            new Scanner("0\n"),
            new PrintStream(new ByteArrayOutputStream()));
        assertNotNull(app);
    }


    /**
     * Tests that null streams are rejected.
     */
    public void testConstructorBadInput()
    {
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        assertTrue(
            thrownBy(
                () -> new PackCheckApp(
                    null,
                    out)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackCheckApp(
                    new Scanner(""),
                    null)) instanceof IllegalArgumentException);
    }


    /**
     * Tests creating a traveler, editing lists, comparing, and exiting.
     */
    public void testRunFullWorkflow()
    {
        String out = runWith(
            "1",
            "Vincy",
            "3",
            "1",
            "Socks",
            "Clothing",
            "3",
            "1",
            "socks",
            "clothing",
            "3",
            "2",
            "socks",
            "CLOTHING",
            "3",
            "2",
            "Hat",
            "Clothing",
            "7",
            "0");
        assertTrue(out.contains("Created traveler vincy."));
        assertTrue(out.contains("Selected vincy."));
        assertTrue(out.contains("Missing:\n  1 x socks [clothing]"));
        assertTrue(out.contains("Extra:\n  1 x hat [clothing]"));
        assertTrue(out.endsWith("Goodbye!\n"));
    }


    /**
     * Tests that bad menu choices re-prompt instead of crashing.
     */
    public void testRunBadMenuChoice()
    {
        String out = runWith("abc", "11", "-1", "", "0");
        assertEquals(4, count(out, "Please enter a whole number from 0"));
        assertTrue(out.endsWith("Goodbye!\n"));
    }


    /**
     * Tests that input ending mid-operation exits without the change.
     */
    public void testRunEndOfInputMidOperation()
    {
        String out = runWith("1", "Vincy", "3", "1", "socks");
        assertTrue(out.contains("Input ended."));
        assertTrue(out.endsWith("Goodbye!\n"));
        assertFalse(out.contains("Added socks"));
    }


    /**
     * Tests that immediate end of input exits normally.
     */
    public void testRunImmediateEndOfInput()
    {
        assertTrue(runWith().contains("Input ended."));
    }


    /**
     * Tests that invalid item text is explained and re-prompted.
     */
    public void testRunInvalidItemText()
    {
        String out = runWith(
            "1",
            "Erik",
            "3",
            "1",
            "   ",
            "clothing",
            repeat('x', 61),
            "clothing",
            "socks",
            "",
            "socks",
            "clothing",
            "0");
        assertTrue(out.contains("Item name cannot be blank."));
        assertTrue(out.contains("Item name must be at most 60"));
        assertTrue(out.contains("Category cannot be blank."));
        assertTrue(out.contains("Added socks [clothing] to required."));
    }


    /**
     * Tests that duplicate and blank traveler names are rejected.
     */
    public void testRunDuplicateTraveler()
    {
        String out = runWith("1", "Erik", "1", " ERIK ", "", "Sophy", "0");
        assertTrue(out.contains("A traveler named \"erik\" already exists."));
        assertTrue(out.contains("Traveler name cannot be blank."));
        assertTrue(out.contains("Created traveler sophy."));
    }


    /**
     * Tests that item commands require a selected traveler.
     */
    public void testRunNoTravelerSelected()
    {
        String out = runWith("3", "5", "6", "7", "2", "0");
        assertEquals(4, count(out, "Create or select a traveler first."));
        assertTrue(out.contains("No travelers yet."));
    }


    /**
     * Tests that removing from an empty list is explained.
     */
    public void testRunRemoveFromEmptyList()
    {
        String out = runWith("1", "Erik", "5", "2", "6", "0");
        assertTrue(out.contains("The packed list is empty"));
        assertEquals(2, count(out, "(empty)"));
    }


    /**
     * Tests insert and remove at displayed positions, with bad positions.
     */
    public void testRunInsertAndRemovePositions()
    {
        String out = runWith(
            "1",
            "Erik",
            "3",
            "2",
            "sock",
            "clothing",
            "3",
            "2",
            "shoe",
            "footwear",
            "4",
            "2",
            "hat",
            "clothing",
            "9",
            "0",
            "2",
            "5",
            "2",
            "4",
            "3",
            "6",
            "0");
        assertTrue(out.contains("Inserted hat [clothing] at position 2"));
        assertTrue(out.contains("Please enter a whole number from 1 to 3."));
        assertTrue(out.contains("Removed shoe [footwear] from packed."));
        assertTrue(
            out.contains(
                "Packed items:\n  1. sock [clothing]\n  2. hat [clothing]\n"));
    }


    /**
     * Tests that only the chosen list changes.
     */
    public void testRunCollectionSelection()
    {
        String out =
            runWith("1", "Erik", "3", "2", "sock", "clothing", "6", "0");
        assertTrue(out.contains("Required items:\n  (empty)"));
        assertTrue(out.contains("Packed items:\n  1. sock [clothing]"));
    }


    /**
     * Tests that reminders stay visible after switching travelers.
     */
    public void testRunSharedReminders()
    {
        String out = runWith(
            "1",
            "Vincy",
            "1",
            "Erik",
            "8",
            "Bring ID",
            "TRAVEL",
            "2",
            "2",
            "9",
            "10",
            "5",
            "1",
            "9",
            "10",
            "0");
        assertTrue(out.contains("Selected erik."));
        assertTrue(out.contains("Trip reminders:\n  1. Bring ID [travel]"));
        assertTrue(out.contains("Removed reminder: Bring ID [travel]"));
        assertTrue(out.contains("No trip reminders.\n"));
        assertTrue(out.contains("No trip reminders to remove."));
    }


    /**
     * Tests that invalid reminder text is explained and re-prompted.
     */
    public void testRunInvalidReminder()
    {
        String out = runWith("8", "", "travel", "Bring ID", "travel", "0");
        assertTrue(out.contains("Reminder text cannot be blank."));
        assertTrue(out.contains("Added reminder: Bring ID [travel]"));
    }


    /**
     * Tests traveler isolation through the menus.
     */
    public void testRunTravelerIsolation()
    {
        String out = runWith(
            "1",
            "Vincy",
            "3",
            "1",
            "sock",
            "clothing",
            "3",
            "1",
            "sock",
            "clothing",
            "3",
            "2",
            "sock",
            "clothing",
            "1",
            "Erik",
            "2",
            "2",
            "3",
            "2",
            "sock",
            "clothing",
            "7",
            "0");
        int erikSection = out.lastIndexOf("Missing:");
        assertTrue(
            out.substring(erikSection).contains(
                "Missing:\n  Nothing is missing.\nExtra:\n  1 x sock [clothing]"));
    }


    /**
     * Tests that main() exits normally on an Exit command.
     */
    public void testMain()
    {
        assertTrue(runMain("0\n", new String[0]).endsWith("Goodbye!\n"));
    }


    /**
     * Tests that main() tolerates null arguments and immediate end of input.
     */
    public void testMainBadInput()
    {
        assertTrue(runMain("", null).contains("Input ended."));
    }


    // ~ Helpers ...............................................................
    /**
     * Runs the app on the given lines and returns everything it printed.
     *
     * @param lines
     *            the user's input, one entry per line
     * @return the app's output
     */
    private String runWith(String... lines)
    {
        String script =
            lines.length == 0 ? "" : String.join("\n", lines) + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true);
        new PackCheckApp(new Scanner(script), out).run();
        return bytes.toString().replace(System.lineSeparator(), "\n");
    }


    /**
     * Runs main() with temporary standard streams.
     *
     * @param script
     *            the text to supply as standard input
     * @param args
     *            the arguments to pass
     * @return what main() printed
     */
    private String runMain(String script, String[] args)
    {
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream(script.getBytes()));
            System.setOut(new PrintStream(bytes, true));
            PackCheckApp.main(args);
        }
        finally
        {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }
        return bytes.toString().replace(System.lineSeparator(), "\n");
    }


    /**
     * Counts non-overlapping occurrences of a phrase.
     *
     * @param text
     *            the text to search
     * @param phrase
     *            the phrase to find
     * @return the number of occurrences
     */
    private static int count(String text, String phrase)
    {
        int total = 0;
        int at = text.indexOf(phrase);
        while (at >= 0)
        {
            total++;
            at = text.indexOf(phrase, at + phrase.length());
        }
        return total;
    }


    /**
     * Runs an action and returns the exception it threw, or null.
     *
     * @param action
     *            the code expected to throw
     * @return the thrown exception, or null if none was thrown
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


    /**
     * Builds a string of one repeated character (Java 8 has no String.repeat).
     *
     * @param letter
     *            the character to repeat
     * @param times
     *            how many copies
     * @return the repeated string
     */
    private static String repeat(char letter, int times)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++)
        {
            result.append(letter);
        }
        return result.toString();
    }
}
