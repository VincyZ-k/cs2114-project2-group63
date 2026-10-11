/**
 * One person on the trip. A traveler owns two separate item chains, REQUIRED
 * and PACKED, and never hands out the originals: getItems() returns a
 * structural copy.
 *
 * @author Wenxin Zhang
 * @version October 9, 2026
 */
public class Traveler
{
    // ~ Fields ................................................................
    private final String name;
    private final LinkedChain<PackingItem> required;
    private final LinkedChain<PackingItem> packed;

    // ~ Constructors ..........................................................
    /**
     * Creates a traveler with a normalized name and two empty item chains.
     *
     * @param name
     *            the traveler name, at most 40 characters after trimming
     * @throws IllegalArgumentException
     *             if the name is null, blank, or too long
     */
    public Traveler(String name)
    {
        this.name = TextRules
            .normalized(name, "Traveler name", TextRules.MAX_TRAVELER_NAME);
        required = new LinkedChain<PackingItem>();
        packed = new LinkedChain<PackingItem>();
    }


    // ~ Public Methods ........................................................
    /**
     * Gets the normalized traveler name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Appends an item to the selected packing list.
     *
     * @param list
     *            REQUIRED or PACKED
     * @param item
     *            the non-null item to append
     * @throws IllegalArgumentException
     *             if list or item is null
     */
    public void addItem(PackingList list, PackingItem item)
    {
        selectList(list).addToEnd(item);
    }


    /**
     * Inserts an item at a zero-based position in the selected packing list.
     *
     * @param list
     *            REQUIRED or PACKED
     * @param index
     *            a position from zero through the selected list's size
     * @param item
     *            the non-null item to insert
     * @throws IllegalArgumentException
     *             if list or item is null
     * @throws IndexOutOfBoundsException
     *             if index is outside the insertion range
     */
    public void insertItem(PackingList list, int index, PackingItem item)
    {
        selectList(list).insert(index, item);
    }


    /**
     * Removes and returns one selected occurrence from a packing list.
     *
     * @param list
     *            REQUIRED or PACKED
     * @param index
     *            the zero-based position to remove
     * @return the removed item
     * @throws IllegalArgumentException
     *             if list is null
     * @throws IndexOutOfBoundsException
     *             if index does not identify an item
     */
    public PackingItem removeItem(PackingList list, int index)
    {
        return selectList(list).removeAt(index);
    }


    /**
     * Returns a structural copy of the selected packing list.
     *
     * @param list
     *            REQUIRED or PACKED
     * @return an independent chain containing the same immutable items
     * @throws IllegalArgumentException
     *             if list is null
     */
    public LinkedChain<PackingItem> getItems(PackingList list)
    {
        return selectList(list).copy();
    }


    // ~ Private Methods .......................................................
    /**
     * Chooses one of this traveler's two item chains.
     *
     * @param list
     *            REQUIRED or PACKED
     * @return the selected internal chain
     * @throws IllegalArgumentException
     *             if list is null
     */
    private LinkedChain<PackingItem> selectList(PackingList list)
    {
        if (list == null)
        {
            throw new IllegalArgumentException("Packing list is required.");
        }
        if (list == PackingList.REQUIRED)
        {
            return required;
        }
        return packed;
    }
}
