import student.TestCase;

/**
 * Tests for PackingComparator.
 *
 * @author Erik Kwon (original tests); OpenAI Codex (TestCase revision)
 * @version October 18, 2026
 */
public class PackingComparatorTest
    extends TestCase
{
    // ~ Fields ................................................................
    private PackingItem socks;
    private PackingItem hat;
    private PackingItem shirt;

    // ~ Setup
    // ..................................................................
    /**
     * Sets up common test items.
     */
    @Override
    public void setUp()
    {
        socks = new PackingItem("socks", "clothing");
        hat = new PackingItem("hat", "clothing");
        shirt = new PackingItem("shirt", "clothing");
    }

    // ~Public Methods ........................................................


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

}
