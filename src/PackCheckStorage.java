import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Saves and loads PackCheck data in a simple text file.
 *
 * @author Wenxin Zhang
 * @version October 12, 2026
 */
final class PackCheckStorage
{
    /**
     * This class only contains static methods, so it does not need objects.
     */
    private PackCheckStorage()
    {
        // Nothing to initialize.
    }


    /**
     * Saves all current PackCheck data to a text file.
     *
     * @param path
     *            location of the save file
     * @param travelers
     *            all travelers
     * @param reminders
     *            all reminders
     * @param selected
     *            the currently selected traveler
     * @throws IOException
     *             if the file cannot be written
     */
    static void save(
        Path path,
        LinkedChain<Traveler> travelers,
        LinkedChain<TripReminder> reminders,
        Traveler selected) throws IOException
    {
        PrintWriter writer = new PrintWriter(new FileWriter(path.toFile()));

        if (selected == null)
        {
            writer.println("SELECTED|");
        }
        else
        {
            writer.println("SELECTED|" + selected.getName());
        }

        for (int i = 0; i < travelers.size(); i++)
        {
            Traveler traveler = travelers.get(i);
            writer.println("TRAVELER|" + traveler.getName());
            writeItems(writer, traveler, PackingList.REQUIRED);
            writeItems(writer, traveler, PackingList.PACKED);
            writer.println("END");
        }

        for (int i = 0; i < reminders.size(); i++)
        {
            TripReminder reminder = reminders.get(i);
            writer.println(
                "REMINDER|" + reminder.getText() + "|"
                    + reminder.getTopic());
        }

        writer.close();
    }


    /**
     * Writes all items from one of a traveler's packing lists.
     */
    private static void writeItems(
        PrintWriter writer,
        Traveler traveler,
        PackingList list)
    {
        LinkedChain<PackingItem> items = traveler.getItems(list);
        for (int i = 0; i < items.size(); i++)
        {
            PackingItem item = items.get(i);
            writer.println(
                "ITEM|" + list.name() + "|" + item.getName() + "|"
                    + item.getCategory());
        }
    }


    /**
     * Loads PackCheck data from a text file. If the file does not exist, an
     * empty session is returned.
     *
     * @param path
     *            location of the save file
     * @return the loaded PackCheck data
     * @throws IOException
     *             if the file cannot be read or has an invalid line
     */
    static Snapshot load(Path path) throws IOException
    {
        File file = path.toFile();
        if (!file.exists())
        {
            return emptySnapshot();
        }

        LinkedChain<Traveler> travelers = new LinkedChain<Traveler>();
        LinkedChain<TripReminder> reminders =
            new LinkedChain<TripReminder>();
        Traveler currentTraveler = null;
        String selectedName = null;

        try (Scanner reader = new Scanner(file))
        {
            while (reader.hasNextLine())
            {
                String[] parts = reader.nextLine().split("\\|", -1);

                if (parts[0].equals("SELECTED") && parts.length == 2)
                {
                    selectedName = parts[1].isEmpty() ? null : parts[1];
                }
                else if (parts[0].equals("TRAVELER") && parts.length == 2)
                {
                    currentTraveler = new Traveler(parts[1]);
                    travelers.addToEnd(currentTraveler);
                }
                else if (parts[0].equals("ITEM") && parts.length == 4
                    && currentTraveler != null)
                {
                    PackingList list = PackingList.valueOf(parts[1]);
                    PackingItem item = new PackingItem(parts[2], parts[3]);
                    currentTraveler.addItem(list, item);
                }
                else if (parts[0].equals("END") && parts.length == 1)
                {
                    currentTraveler = null;
                }
                else if (parts[0].equals("REMINDER") && parts.length == 3)
                {
                    reminders.addToEnd(
                        new TripReminder(parts[1], parts[2]));
                }
                else
                {
                    throw new IOException("Invalid PackCheck save data.");
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new IOException("Invalid PackCheck save data.");
        }

        Traveler selected = findTraveler(travelers, selectedName);
        return new Snapshot(travelers, reminders, selected);
    }


    /**
     * Returns an empty set of saved data.
     */
    private static Snapshot emptySnapshot()
    {
        return new Snapshot(
            new LinkedChain<Traveler>(),
            new LinkedChain<TripReminder>(),
            null);
    }


    /**
     * Finds a traveler by name.
     */
    private static Traveler findTraveler(
        LinkedChain<Traveler> travelers,
        String name)
    {
        if (name == null)
        {
            return null;
        }
        for (int i = 0; i < travelers.size(); i++)
        {
            Traveler traveler = travelers.get(i);
            if (traveler.getName().equals(name))
            {
                return traveler;
            }
        }
        return null;
    }


    /**
     * Stores everything that was read from the save file.
     */
    static final class Snapshot
    {
        private final LinkedChain<Traveler> travelers;
        private final LinkedChain<TripReminder> reminders;
        private final Traveler selectedTraveler;

        private Snapshot(
            LinkedChain<Traveler> travelers,
            LinkedChain<TripReminder> reminders,
            Traveler selectedTraveler)
        {
            this.travelers = travelers;
            this.reminders = reminders;
            this.selectedTraveler = selectedTraveler;
        }


        LinkedChain<Traveler> getTravelers()
        {
            return travelers;
        }


        LinkedChain<TripReminder> getReminders()
        {
            return reminders;
        }


        Traveler getSelectedTraveler()
        {
            return selectedTraveler;
        }
    }
}
