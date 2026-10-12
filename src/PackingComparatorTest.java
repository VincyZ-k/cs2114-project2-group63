/**
 * Tests for PackingComparator.
 *
 * @author 
 * @version October 11, 2026
 */
public class PackingComparatorTest
{
    //~ Fields ................................................................
    private PackingItem socks;
    private PackingItem hat;
    private PackingItem shirt;

    //~ Constructors ..........................................................
    /**
     * Sets up common test items.
     */
    public PackingComparatorTest()
    {
        socks = new PackingItem("socks", "clothing");
        hat = new PackingItem("hat", "clothing");
        shirt = new PackingItem("shirt", "clothing");
    }

    //~Public  Methods ........................................................

    /**
     * Tests missing items when some required units are not packed.
     */
    public void testMissingNormal()
    {
        LinkedChain<PackingItem> required = new LinkedChain<>();
        required.addToEnd(socks);
        required.addToEnd(socks);
        required.addToEnd(socks);

        LinkedChain<PackingItem> packed = new LinkedChain<>();
        packed.addToEnd(socks);
        packed.addToEnd(socks);

        LinkedChain<PackingItem> result =
            PackingComparator.missing(required, packed);

        assertEquals(1, result.size());
        assertTrue(result.contains(socks));
        assertEquals(1, result.count(socks));
    }

    /**
     * Tests that extra packed items are returned correctly.
     */
    public void testExtraNormal()
    {
        LinkedChain<PackingItem> required = new LinkedChain<>();
        required.addToEnd(socks);

        LinkedChain<PackingItem> packed = new LinkedChain<>();
        packed.addToEnd(socks);
        packed.addToEnd(hat);

        LinkedChain<PackingItem> result =
            PackingComparator.extra(required, packed);

        assertEquals(1, result.size());
        assertTrue(result.contains(hat));
    }

    /**
     * Tests empty chains produce empty results.
     */
    public void testEmptyChains()
    {
        LinkedChain<PackingItem> empty1 = new LinkedChain<>();
        LinkedChain<PackingItem> empty2 = new LinkedChain<>();

        assertTrue(PackingComparator.missing(empty1, empty2).isEmpty());
        assertTrue(PackingComparator.extra(empty1, empty2).isEmpty());
    }

    /**
     * Tests that all packed items are extra when required is empty.
     */
    public void testAllExtraWhenRequiredEmpty()
    {
        LinkedChain<PackingItem> required = new LinkedChain<>();
        LinkedChain<PackingItem> packed = new LinkedChain<>();
        packed.addToEnd(hat);
        packed.addToEnd(shirt);

        LinkedChain<PackingItem> result =
            PackingComparator.extra(required, packed);

        assertEquals(2, result.size());
        assertTrue(result.contains(hat));
        assertTrue(result.contains(shirt));
    }

    /**
     * Tests that null chains throw IllegalArgumentException.
     */
    public void testNullChains()
    {
        LinkedChain<PackingItem> chain = new LinkedChain<>();

        try
        {
            PackingComparator.missing(null, chain);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }

        try
        {
            PackingComparator.extra(chain, null);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    /**
     * Tests that original chains are not modified.
     */
    public void testOriginalsUnchanged()
    {
        LinkedChain<PackingItem> required = new LinkedChain<>();
        required.addToEnd(socks);
        required.addToEnd(socks);

        LinkedChain<PackingItem> packed = new LinkedChain<>();
        packed.addToEnd(socks);

        int reqSize = required.size();
        int pacSize = packed.size();

        PackingComparator.missing(required, packed);
        PackingComparator.extra(required, packed);

        assertEquals(reqSize, required.size());
        assertEquals(pacSize, packed.size());
        assertEquals(2, required.count(socks));
        assertEquals(1, packed.count(socks));
    }

    //~ Private Methods .......................................................

    private void assertEquals(int expected, int actual)
    {
        if (expected != actual)
        {
            throw new AssertionError(
                "Expected " + expected + " but was " + actual);
        }
    }

    private void assertTrue(boolean condition)
    {
        if (!condition)
        {
            throw new AssertionError("Expected true");
        }
    }

    private void fail(String message)
    {
        throw new AssertionError(message);
    }
}
