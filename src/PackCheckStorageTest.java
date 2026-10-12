import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests file persistence and the automatic save/load integration.
 *
 * @author Wenxin Zhang
 * @version October 12, 2026
 */
public class PackCheckStorageTest
    extends TestCase
{
    /**
     * A missing save file starts a new empty session.
     */
    public void testMissingFileLoadsEmptySnapshot() throws IOException
    {
        Path directory = Files.createTempDirectory("packcheck-missing-");
        Path file = directory.resolve("data.txt");
        try
        {
            PackCheckStorage.Snapshot snapshot = PackCheckStorage.load(file);
            assertTrue(snapshot.getTravelers().isEmpty());
            assertTrue(snapshot.getReminders().isEmpty());
            assertNull(snapshot.getSelectedTraveler());
        }
        finally
        {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }


    /**
     * A complete snapshot keeps travelers, both lists, reminders, duplicates,
     * ordering, and the selected traveler.
     */
    public void testRoundTrip() throws IOException
    {
        Path directory = Files.createTempDirectory("packcheck-roundtrip-");
        Path file = directory.resolve("data.txt");
        try
        {
            Traveler vincy = new Traveler("Vincy");
            PackingItem socks = new PackingItem("Socks", "Clothing");
            vincy.addItem(PackingList.REQUIRED, socks);
            vincy.addItem(PackingList.REQUIRED, socks);
            vincy.addItem(
                PackingList.PACKED,
                new PackingItem("Hat", "Clothing"));
            Traveler erik = new Traveler("Erik");

            LinkedChain<Traveler> travelers = new LinkedChain<Traveler>();
            travelers.addToEnd(vincy);
            travelers.addToEnd(erik);
            LinkedChain<TripReminder> reminders =
                new LinkedChain<TripReminder>();
            reminders.addToEnd(new TripReminder("Bring ID", "TRAVEL"));

            PackCheckStorage.save(file, travelers, reminders, erik);
            PackCheckStorage.Snapshot loaded = PackCheckStorage.load(file);

            assertEquals(2, loaded.getTravelers().size());
            assertEquals("vincy", loaded.getTravelers().get(0).getName());
            assertEquals(
                "[socks [clothing], socks [clothing]]",
                loaded.getTravelers().get(0)
                    .getItems(PackingList.REQUIRED).toString());
            assertEquals(
                "[hat [clothing]]",
                loaded.getTravelers().get(0)
                    .getItems(PackingList.PACKED).toString());
            assertEquals("erik", loaded.getSelectedTraveler().getName());
            assertEquals(
                "Bring ID [travel]",
                loaded.getReminders().get(0).toString());
        }
        finally
        {
            deleteSaveFiles(file);
            Files.deleteIfExists(directory);
        }
    }


    /**
     * Two separate app runs share the same automatically saved data.
     */
    public void testSeparateRunsShareSavedData() throws IOException
    {
        Path directory = Files.createTempDirectory("packcheck-app-");
        Path file = directory.resolve("data.txt");
        try
        {
            runWith(
                file,
                "1", "Vincy",
                "3", "1", "Socks", "Clothing",
                "3", "2", "Socks", "Clothing",
                "8", "Bring ID", "TRAVEL",
                "1", "Erik",
                "2", "2",
                "3", "2", "Hat", "Clothing",
                "0");

            String secondRun = runWith(
                file,
                "6",
                "2", "1",
                "6",
                "9",
                "0");

            assertTrue(secondRun.contains("Current traveler: erik"));
            assertTrue(secondRun.contains("Packed items:\n  1. hat [clothing]"));
            assertTrue(secondRun.contains("Selected vincy."));
            assertTrue(
                secondRun.contains(
                    "Required items:\n  1. socks [clothing]"));
            assertTrue(
                secondRun.contains("Trip reminders:\n  1. Bring ID [travel]"));
        }
        finally
        {
            deleteSaveFiles(file);
            Files.deleteIfExists(directory);
        }
    }


    /**
     * Malformed data is rejected and the app safely starts empty.
     */
    public void testMalformedFileDoesNotPartiallyLoad() throws IOException
    {
        Path directory = Files.createTempDirectory("packcheck-bad-");
        Path file = directory.resolve("data.txt");
        try
        {
            Files.write(
                file,
                Arrays.asList("not a PackCheck save file"),
                StandardCharsets.UTF_8);
            String output = runWith(file, "2", "0");
            assertTrue(output.contains("saved data could not be loaded"));
            assertTrue(output.contains("No travelers yet."));
        }
        finally
        {
            deleteSaveFiles(file);
            Files.deleteIfExists(directory);
        }
    }


    /**
     * Runs one isolated app session against the supplied save file.
     */
    private String runWith(Path file, String... lines)
    {
        String script = String.join("\n", lines) + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true);
        new PackCheckApp(new Scanner(script), output, file).run();
        return bytes.toString().replace(System.lineSeparator(), "\n");
    }


    /**
     * Removes a test save.
     */
    private void deleteSaveFiles(Path file) throws IOException
    {
        Files.deleteIfExists(file);
    }
}
