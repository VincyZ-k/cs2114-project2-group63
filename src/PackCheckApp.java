import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Text menu for PackCheck. This class owns all input handling: it reads whole
 * lines, re-prompts on bad input, and turns model exceptions into readable
 * messages. Travelers and reminders are stored in LinkedChains. If input ends
 * in the middle of an operation, that operation is discarded and the program
 * exits normally.
 *
 * @author erikkwon
 * @version October 11, 2026
 */
public class PackCheckApp
{
    private final Scanner input;
    private final PrintStream output;
    private final LinkedChain<Traveler> travelers;
    private final LinkedChain<TripReminder> reminders;
    private final Path storageFile;
    private Traveler selectedTraveler;

    /**
     * Creates an app that reads from and writes to the given streams.
     *
     * @param input
     *            where commands are read from
     * @param output
     *            where prompts and results are written
     * @throws IllegalArgumentException
     *             if either stream is null
     */
    public PackCheckApp(Scanner input, PrintStream output)
    {
        this(input, output, null);
    }


    /**
     * Creates an app with optional persistent storage. The two-argument
     * constructor intentionally disables storage so scripted unit tests remain
     * isolated from a user's real save file.
     *
     * @param input
     *            where commands are read from
     * @param output
     *            where prompts and results are written
     * @param storageFile
     *            save-file path, or null to keep the session in memory only
     */
    PackCheckApp(Scanner input, PrintStream output, Path storageFile)
    {
        if (input == null || output == null)
        {
            throw new IllegalArgumentException(
                "Input and output streams are required.");
        }
        this.input = input;
        this.output = output;
        this.storageFile = storageFile;

        LinkedChain<Traveler> loadedTravelers =
            new LinkedChain<Traveler>();
        LinkedChain<TripReminder> loadedReminders =
            new LinkedChain<TripReminder>();
        Traveler loadedSelection = null;
        if (storageFile != null)
        {
            try
            {
                PackCheckStorage.Snapshot snapshot =
                    PackCheckStorage.load(storageFile);
                loadedTravelers = snapshot.getTravelers();
                loadedReminders = snapshot.getReminders();
                loadedSelection = snapshot.getSelectedTraveler();
            }
            catch (IOException e)
            {
                output.println(
                    "Warning: saved data could not be loaded. "
                        + "Starting with an empty session.");
            }
        }
        travelers = loadedTravelers;
        reminders = loadedReminders;
        selectedTraveler = loadedSelection;
    }


    /**
     * Runs the main menu until the user chooses Exit or input ends.
     */
    public void run()
    {
        output.println("Welcome to PackCheck!");
        try
        {
            boolean running = true;
            while (running)
            {
                printMenu();
                int choice = readInt("Choose an option: ", 0, 10);
                running = handle(choice);
            }
        }
        catch (EndOfInput e)
        {
            output.println();
            output.println("Input ended. Unfinished changes discarded.");
        }
        output.println("Goodbye!");
    }


    /**
     * Starts PackCheck on standard input and output.
     *
     * @param args
     *            ignored
     */
    public static void main(String[] args)
    {
        new PackCheckApp(
            new Scanner(System.in),
            System.out,
            Paths.get("packcheck-data.txt")).run();
    }

    // ----------------------------------------------------------------
    // Menu dispatch


    /**
     * Prints the main menu and the current traveler.
     */
    private void printMenu()
    {
        output.println();
        output.println(
            "Current traveler: " + (selectedTraveler == null
                ? "(none)"
                : selectedTraveler.getName()));
        output.println(" 1. Create traveler");
        output.println(" 2. Select traveler");
        output.println(" 3. Add item");
        output.println(" 4. Insert item at position");
        output.println(" 5. Remove item");
        output.println(" 6. Show items");
        output.println(" 7. Compare required and packed");
        output.println(" 8. Add trip reminder");
        output.println(" 9. Show trip reminders");
        output.println("10. Remove trip reminder");
        output.println(" 0. Exit");
    }


    /**
     * Performs one menu choice.
     *
     * @param choice
     *            a validated menu number
     * @return false if the user chose Exit, true otherwise
     */
    private boolean handle(int choice)
    {
        switch (choice)
        {
            case 1:
                createTraveler();
                break;
            case 2:
                selectTraveler();
                break;
            case 3:
                addItem();
                break;
            case 4:
                insertItem();
                break;
            case 5:
                removeItem();
                break;
            case 6:
                showItems();
                break;
            case 7:
                compare();
                break;
            case 8:
                addReminder();
                break;
            case 9:
                showReminders();
                break;
            case 10:
                removeReminder();
                break;
            default:
                return false;
        }
        return true;
    }

    // ----------------------------------------------------------------
    // Travelers


    /**
     * Creates a traveler with a unique name. The first traveler is selected
     * automatically.
     */
    private void createTraveler()
    {
        Traveler traveler = null;
        while (traveler == null)
        {
            String name = readLine("Traveler name: ");
            try
            {
                Traveler candidate = new Traveler(name);
                if (findTraveler(candidate.getName()) != null)
                {
                    output.println(
                        "A traveler named \"" + candidate.getName()
                            + "\" already exists.");
                }
                else
                {
                    traveler = candidate;
                }
            }
            catch (IllegalArgumentException e)
            {
                output.println(e.getMessage());
            }
        }
        travelers.addToEnd(traveler);
        output.println("Created traveler " + traveler.getName() + ".");
        if (selectedTraveler == null)
        {
            selectedTraveler = traveler;
            output.println("Selected " + traveler.getName() + ".");
        }
        saveData();
    }


    /**
     * Lets the user pick a traveler by displayed position.
     */
    private void selectTraveler()
    {
        if (travelers.isEmpty())
        {
            output.println("No travelers yet. Create one first.");
            return;
        }
        for (int i = 0; i < travelers.size(); i++)
        {
            output.println((i + 1) + ". " + travelers.get(i).getName());
        }
        int position = readInt("Traveler number: ", 1, travelers.size());
        selectedTraveler = travelers.get(position - 1);
        output.println("Selected " + selectedTraveler.getName() + ".");
        saveData();
    }


    /**
     * Finds a traveler by normalized name.
     *
     * @param name
     *            a normalized name
     * @return the matching traveler, or null
     */
    private Traveler findTraveler(String name)
    {
        for (int i = 0; i < travelers.size(); i++)
        {
            if (travelers.get(i).getName().equals(name))
            {
                return travelers.get(i);
            }
        }
        return null;
    }


    /**
     * Checks that a traveler is selected and explains if not.
     *
     * @return true if item commands may proceed
     */
    private boolean requireTraveler()
    {
        if (selectedTraveler == null)
        {
            output.println("Create or select a traveler first.");
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------
    // Items


    /**
     * Appends an item to the selected traveler's chosen list.
     */
    private void addItem()
    {
        if (!requireTraveler())
        {
            return;
        }
        PackingList list = readList();
        PackingItem item = readItem();
        selectedTraveler.addItem(list, item);
        output.println("Added " + item + " to " + label(list) + ".");
        saveData();
    }


    /**
     * Inserts an item at a displayed position (1 through n + 1).
     */
    private void insertItem()
    {
        if (!requireTraveler())
        {
            return;
        }
        PackingList list = readList();
        int size = selectedTraveler.getItems(list).size();
        PackingItem item = readItem();
        int position =
            readInt("Position (1-" + (size + 1) + "): ", 1, size + 1);
        selectedTraveler.insertItem(list, position - 1, item);
        output.println(
            "Inserted " + item + " at position " + position + " of "
                + label(list) + ".");
        saveData();
    }


    /**
     * Removes the item at a displayed position (1 through n).
     */
    private void removeItem()
    {
        if (!requireTraveler())
        {
            return;
        }
        PackingList list = readList();
        LinkedChain<PackingItem> items = selectedTraveler.getItems(list);
        if (items.isEmpty())
        {
            output.println(
                "The " + label(list)
                    + " list is empty, so nothing can be removed.");
            return;
        }
        printNumbered(items);
        int position = readInt(
            "Position to remove (1-" + items.size() + "): ",
            1,
            items.size());
        PackingItem removed = selectedTraveler.removeItem(list, position - 1);
        output.println("Removed " + removed + " from " + label(list) + ".");
        saveData();
    }


    /**
     * Shows both of the selected traveler's lists.
     */
    private void showItems()
    {
        if (!requireTraveler())
        {
            return;
        }
        for (PackingList list : PackingList.values())
        {
            output.println(capitalize(label(list)) + " items:");
            LinkedChain<PackingItem> items = selectedTraveler.getItems(list);
            if (items.isEmpty())
            {
                output.println("  (empty)");
            }
            else
            {
                printNumbered(items);
            }
        }
    }


    /**
     * Shows missing and extra quantities for the selected traveler.
     */
    private void compare()
    {
        if (!requireTraveler())
        {
            return;
        }
        LinkedChain<PackingItem> required =
            selectedTraveler.getItems(PackingList.REQUIRED);
        LinkedChain<PackingItem> packed =
            selectedTraveler.getItems(PackingList.PACKED);
        output.println("Missing:");
        printQuantities(
            PackingComparator.missing(required, packed),
            "  Nothing is missing.");
        output.println("Extra:");
        printQuantities(
            PackingComparator.extra(required, packed),
            "  Nothing extra.");
    }


    /**
     * Prints each distinct item once with its quantity, in first-seen order.
     *
     * @param items
     *            the items to group
     * @param emptyNote
     *            the message to print when items is empty
     */
    private
        void
        printQuantities(LinkedChain<PackingItem> items, String emptyNote)
    {
        if (items.isEmpty())
        {
            output.println(emptyNote);
            return;
        }
        LinkedChain<PackingItem> printed = new LinkedChain<PackingItem>();
        for (int i = 0; i < items.size(); i++)
        {
            PackingItem item = items.get(i);
            if (!printed.contains(item))
            {
                printed.addToEnd(item);
                output.println("  " + items.count(item) + " x " + item);
            }
        }
    }

    // ----------------------------------------------------------------
    // Reminders


    /**
     * Adds a shared trip reminder.
     */
    private void addReminder()
    {
        TripReminder reminder = null;
        while (reminder == null)
        {
            String text = readLine("Reminder text: ");
            String topic = readLine("Topic: ");
            try
            {
                reminder = new TripReminder(text, topic);
            }
            catch (IllegalArgumentException e)
            {
                output.println(e.getMessage());
            }
        }
        reminders.addToEnd(reminder);
        output.println("Added reminder: " + reminder);
        saveData();
    }


    /**
     * Lists the shared trip reminders.
     */
    private void showReminders()
    {
        if (reminders.isEmpty())
        {
            output.println("No trip reminders.");
            return;
        }
        output.println("Trip reminders:");
        printNumbered(reminders);
    }


    /**
     * Removes a reminder by displayed position.
     */
    private void removeReminder()
    {
        if (reminders.isEmpty())
        {
            output.println("No trip reminders to remove.");
            return;
        }
        printNumbered(reminders);
        int position = readInt(
            "Reminder to remove (1-" + reminders.size() + "): ",
            1,
            reminders.size());
        output.println("Removed reminder: " + reminders.removeAt(position - 1));
        saveData();
    }

    // ----------------------------------------------------------------
    // Persistence


    /**
     * Writes the complete current state when persistence is enabled. A failed
     * save is reported without ending the interactive session.
     */
    private void saveData()
    {
        if (storageFile == null)
        {
            return;
        }
        try
        {
            PackCheckStorage.save(
                storageFile,
                travelers,
                reminders,
                selectedTraveler);
        }
        catch (IOException e)
        {
            output.println("Warning: current data could not be saved.");
        }
    }

    // ----------------------------------------------------------------
    // Input helpers


    /**
     * Prompts for and reads one whole line.
     *
     * @param prompt
     *            the prompt to show
     * @return the line read
     * @throws EndOfInput
     *             if no more input is available
     */
    private String readLine(String prompt)
    {
        output.print(prompt);
        if (!input.hasNextLine())
        {
            throw new EndOfInput();
        }
        return input.nextLine();
    }


    /**
     * Reads an integer in a range, re-prompting until one is entered.
     *
     * @param prompt
     *            the prompt to show
     * @param low
     *            the smallest accepted value
     * @param high
     *            the largest accepted value
     * @return the accepted value
     * @throws EndOfInput
     *             if no more input is available
     */
    private int readInt(String prompt, int low, int high)
    {
        while (true)
        {
            String line = readLine(prompt).trim();
            try
            {
                int value = Integer.parseInt(line);
                if (value >= low && value <= high)
                {
                    return value;
                }
            }
            catch (NumberFormatException e)
            {
                // fall through to the shared message
            }
            output.println(
                "Please enter a whole number from " + low + " to " + high
                    + ".");
        }
    }


    /**
     * Asks which list an item command applies to.
     *
     * @return the chosen list
     */
    private PackingList readList()
    {
        int choice = readInt("List (1 = required, 2 = packed): ", 1, 2);
        return choice == 1 ? PackingList.REQUIRED : PackingList.PACKED;
    }


    /**
     * Reads an item name and category, re-prompting until both are valid.
     *
     * @return the new item
     */
    private PackingItem readItem()
    {
        while (true)
        {
            String name = readLine("Item name: ");
            String category = readLine("Category: ");
            try
            {
                return new PackingItem(name, category);
            }
            catch (IllegalArgumentException e)
            {
                output.println(e.getMessage());
            }
        }
    }


    /**
     * Prints a chain with one-based positions.
     *
     * @param <T>
     *            the element type
     * @param values
     *            the chain to print
     */
    private <T> void printNumbered(LinkedChain<T> values)
    {
        for (int i = 0; i < values.size(); i++)
        {
            output.println("  " + (i + 1) + ". " + values.get(i));
        }
    }


    /**
     * Gets the lowercase display name of a list.
     *
     * @param list
     *            the list
     * @return "required" or "packed"
     */
    private static String label(PackingList list)
    {
        return list == PackingList.REQUIRED ? "required" : "packed";
    }


    /**
     * Capitalizes the first letter of a word.
     *
     * @param word
     *            a non-empty word
     * @return the word with its first letter in upper case
     */
    private static String capitalize(String word)
    {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /**
     * Signals that input ended while the app was waiting for a line.
     */
    private static class EndOfInput
        extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }
}
