import student.TestCase;

/**
 * Tests Traveler ownership, list selection, ordered edits, and defensive
 * structural copies.
 *
 * @author Wenxin Zhang
 * @version October 9, 2026
 */
public class TravelerTest
    extends TestCase
{
    private Traveler traveler;
    private PackingItem socks;
    private PackingItem hat;
    private PackingItem shoes;

    /**
     * Creates a traveler and reusable items before each test.
     */
    @Override
    public void setUp()
    {
        traveler = new Traveler(" VINCY ");
        socks = new PackingItem("socks", "clothing");
        hat = new PackingItem("hat", "clothing");
        shoes = new PackingItem("shoes", "clothing");
    }


    /**
     * Tests normalized names and initially empty independent lists.
     */
    public void testConstructor()
    {
        assertEquals("vincy", traveler.getName());
        assertTrue(traveler.getItems(PackingList.REQUIRED).isEmpty());
        assertTrue(traveler.getItems(PackingList.PACKED).isEmpty());
    }


    /**
     * Tests invalid traveler names.
     */
    public void testBadConstructorInput()
    {
        assertTrue(
            thrownBy(
                () -> new Traveler(null)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new Traveler("   ")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new Traveler(
                    "x".repeat(41))) instanceof IllegalArgumentException);
    }


    /**
     * Tests appending to REQUIRED and PACKED without cross-list changes.
     */
    public void testAddItemAndListIsolation()
    {
        traveler.addItem(PackingList.REQUIRED, socks);
        traveler.addItem(PackingList.PACKED, hat);

        LinkedChain<PackingItem> required =
            traveler.getItems(PackingList.REQUIRED);
        LinkedChain<PackingItem> packed = traveler.getItems(PackingList.PACKED);
        assertEquals(1, required.size());
        assertEquals(socks, required.get(0));
        assertEquals(1, packed.size());
        assertEquals(hat, packed.get(0));
    }


    /**
     * Tests null arguments to addItem().
     */
    public void testAddItemBadInput()
    {
        assertTrue(
            thrownBy(
                () -> traveler
                    .addItem(null, socks)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> traveler.addItem(
                    PackingList.REQUIRED,
                    null)) instanceof IllegalArgumentException);
        assertTrue(traveler.getItems(PackingList.REQUIRED).isEmpty());
    }


    /**
     * Tests insertion at the beginning, middle, and end.
     */
    public void testInsertItem()
    {
        traveler.addItem(PackingList.PACKED, socks);
        traveler.addItem(PackingList.PACKED, shoes);
        traveler.insertItem(PackingList.PACKED, 1, hat);
        PackingItem coat = new PackingItem("coat", "clothing");
        traveler.insertItem(PackingList.PACKED, 0, coat);
        traveler.insertItem(
            PackingList.PACKED,
            4,
            new PackingItem("belt", "clothing"));

        assertEquals(
            "[coat [clothing], socks [clothing], hat [clothing], "
                + "shoes [clothing], belt [clothing]]",
            traveler.getItems(PackingList.PACKED).toString());
    }


    /**
     * Tests invalid insertion arguments and unchanged contents.
     */
    public void testInsertItemBadInput()
    {
        traveler.addItem(PackingList.REQUIRED, socks);
        assertTrue(
            thrownBy(
                () -> traveler.insertItem(
                    null,
                    0,
                    hat)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> traveler.insertItem(
                    PackingList.REQUIRED,
                    0,
                    null)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> traveler.insertItem(
                    PackingList.REQUIRED,
                    2,
                    hat)) instanceof IndexOutOfBoundsException);
        assertEquals(
            "[socks [clothing]]",
            traveler.getItems(PackingList.REQUIRED).toString());
    }


    /**
     * Tests removing one selected duplicate occurrence.
     */
    public void testRemoveItem()
    {
        traveler.addItem(PackingList.REQUIRED, socks);
        traveler.addItem(PackingList.REQUIRED, socks);
        assertEquals(socks, traveler.removeItem(PackingList.REQUIRED, 0));
        assertEquals(1, traveler.getItems(PackingList.REQUIRED).size());
    }


    /**
     * Tests invalid removal list and index values.
     */
    public void testRemoveItemBadInput()
    {
        assertTrue(
            thrownBy(
                () -> traveler
                    .removeItem(null, 0)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> traveler.removeItem(
                    PackingList.REQUIRED,
                    0)) instanceof IndexOutOfBoundsException);
    }


    /**
     * Tests that getItems() returns an independent structural copy.
     */
    public void testGetItemsReturnsCopy()
    {
        traveler.addItem(PackingList.REQUIRED, socks);
        LinkedChain<PackingItem> copy = traveler.getItems(PackingList.REQUIRED);
        copy.removeAt(0);
        copy.addToEnd(hat);

        LinkedChain<PackingItem> original =
            traveler.getItems(PackingList.REQUIRED);
        assertEquals("[socks [clothing]]", original.toString());
        assertEquals("[hat [clothing]]", copy.toString());
    }


    /**
     * Tests that getItems() rejects a null selector.
     */
    public void testGetItemsBadInput()
    {
        assertTrue(
            thrownBy(
                () -> traveler
                    .getItems(null)) instanceof IllegalArgumentException);
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
