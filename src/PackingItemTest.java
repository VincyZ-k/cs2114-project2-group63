import student.TestCase;

/**
 * Tests the immutable PackingItem value object.
 *
 * @author Wenxin Zhang
 * @version October 9, 2026
 */
public class PackingItemTest
    extends TestCase
{
    /**
     * Tests trimming, normalization, getters, and display formatting.
     */
    public void testConstructionAndAccessors()
    {
        PackingItem item = new PackingItem(" Socks ", "CLOTHING");
        assertEquals("socks", item.getName());
        assertEquals("clothing", item.getCategory());
        assertEquals("socks [clothing]", item.toString());
    }


    /**
     * Tests constructor validation for both fields.
     */
    public void testBadConstructorInput()
    {
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    null,
                    "clothing")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    "   ",
                    "clothing")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    "x".repeat(61),
                    "c")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    "socks",
                    null)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    "socks",
                    "   ")) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> new PackingItem(
                    "socks",
                    "x".repeat(31))) instanceof IllegalArgumentException);
    }


    /**
     * Tests maximum-length boundary values.
     */
    public void testMaximumLengths()
    {
        PackingItem item = new PackingItem("A".repeat(60), "B".repeat(30));
        assertEquals(60, item.getName().length());
        assertEquals(30, item.getCategory().length());
    }


    /**
     * Tests equality and the matching hash-code contract.
     */
    public void testEqualsAndHashCode()
    {
        PackingItem item = new PackingItem("Socks", "Clothing");
        PackingItem same = new PackingItem(" socks ", "CLOTHING");
        PackingItem otherName = new PackingItem("shirt", "clothing");
        PackingItem otherCategory = new PackingItem("socks", "other");

        assertTrue(item.equals(item));
        assertTrue(item.equals(same));
        assertEquals(item.hashCode(), same.hashCode());
        assertFalse(item.equals(otherName));
        assertFalse(item.equals(otherCategory));
        assertFalse(item.equals(null));
        assertFalse(item.equals("socks"));
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
