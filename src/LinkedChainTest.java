import student.TestCase;

/**
 * Tests for LinkedChain, including the lastNode, insert, removeAt, and copy
 * additions. Each method has a normal case and a bad-input or boundary case,
 * following the specification's test plan.
 *
 * @author Erik Kwon
 * @version October 11 2026
 */
public class LinkedChainTest
    extends TestCase
{
    // ~ Fields ................................................................
    private LinkedChain<String> chain;

    // ~ Setup .................................................................
    /**
     * Creates an empty chain before each test.
     */
    @Override
    public void setUp()
    {
        chain = new LinkedChain<String>();
    }


    // ~ Public Methods ........................................................
    /**
     * Tests that new chains are empty and independent.
     */
    public void testConstructor()
    {
        assertEquals(0, chain.size());
        LinkedChain<String> other = new LinkedChain<String>();
        chain.add("A");
        assertEquals(0, other.size());
    }


    /**
     * Tests that add() places entries at the front.
     */
    public void testAdd()
    {
        assertTrue(chain.add("A"));
        assertTrue(chain.add("B"));
        assertEquals("[B, A]", chain.toString());
    }


    /**
     * Tests that add() rejects null and leaves the chain unchanged.
     */
    public void testAddNull()
    {
        assertTrue(
            thrownBy(
                () -> chain.add(null)) instanceof IllegalArgumentException);
        assertEquals(0, chain.size());
    }


    /**
     * Tests that addToEnd() preserves order.
     */
    public void testAddToEnd()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        assertEquals("[A, B]", chain.toString());
    }


    /**
     * Tests that addToEnd() rejects null.
     */
    public void testAddToEndNull()
    {
        assertTrue(
            thrownBy(
                () -> chain
                    .addToEnd(null)) instanceof IllegalArgumentException);
        assertEquals(0, chain.size());
    }


    /**
     * Tests that mixing add() and addToEnd() keeps the tail correct.
     */
    public void testAddThenAddToEnd()
    {
        chain.add("A");
        chain.addToEnd("B");
        chain.add("Z");
        chain.addToEnd("C");
        assertEquals("[Z, A, B, C]", chain.toString());
    }


    /**
     * Tests that remove() takes from the front and empties cleanly.
     */
    public void testRemoveFirst()
    {
        chain.addToEnd("A");
        assertEquals("A", chain.remove());
        assertTrue(chain.isEmpty());
        chain.addToEnd("B");
        assertEquals("[B]", chain.toString());
    }


    /**
     * Tests that remove() on an empty chain returns null.
     */
    public void testRemoveFirstEmpty()
    {
        assertNull(chain.remove());
    }


    /**
     * Tests insert() at the front, middle, and end.
     */
    public void testInsert()
    {
        chain.insert(0, "B");
        chain.insert(0, "A");
        chain.insert(2, "D");
        chain.insert(2, "C");
        assertEquals("[A, B, C, D]", chain.toString());
        chain.addToEnd("E");
        assertEquals("[A, B, C, D, E]", chain.toString());
    }


    /**
     * Tests that insert() rejects bad indexes and null without changes.
     */
    public void testInsertBadInput()
    {
        chain.addToEnd("A");
        assertTrue(
            thrownBy(
                () -> chain
                    .insert(-1, "X")) instanceof IndexOutOfBoundsException);
        assertTrue(
            thrownBy(
                () -> chain
                    .insert(2, "X")) instanceof IndexOutOfBoundsException);
        assertTrue(
            thrownBy(
                () -> chain
                    .insert(0, null)) instanceof IllegalArgumentException);
        assertEquals("[A]", chain.toString());
    }


    /**
     * Tests that clear() empties the chain and appends still work.
     */
    public void testClear()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        chain.clear();
        assertEquals(0, chain.size());
        chain.addToEnd("C");
        assertEquals("[C]", chain.toString());
    }


    /**
     * Tests that clear() on an empty chain is harmless.
     */
    public void testClearEmpty()
    {
        chain.clear();
        assertTrue(chain.isEmpty());
    }


    /**
     * Tests that toArray() copies in chain order.
     */
    public void testToArray()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        String[] values = chain.toArray(new String[2]);
        assertEquals("A", values[0]);
        assertEquals("B", values[1]);
    }


    /**
     * Tests that toArray() rejects null and undersized arrays.
     */
    public void testToArrayBadInput()
    {
        chain.addToEnd("A");
        assertTrue(
            thrownBy(
                () -> chain.toArray(null)) instanceof IllegalArgumentException);
        assertTrue(
            thrownBy(
                () -> chain.toArray(
                    new String[0])) instanceof IllegalArgumentException);
    }


    /**
     * Tests the "[a, b, c]" format.
     */
    public void testToString()
    {
        assertEquals("[]", chain.toString());
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");
        assertEquals("[a, b, c]", chain.toString());
    }


    /**
     * Tests that get() returns without removing.
     */
    public void testGet()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        assertEquals("B", chain.get(1));
        assertEquals(2, chain.size());
    }


    /**
     * Tests that get() rejects bad indexes.
     */
    public void testGetBadIndex()
    {
        assertTrue(
            thrownBy(() -> chain.get(0)) instanceof IndexOutOfBoundsException);
        chain.addToEnd("A");
        assertTrue(
            thrownBy(() -> chain.get(-1)) instanceof IndexOutOfBoundsException);
    }


    /**
     * Tests removeAt() on first, middle, last, and only nodes.
     */
    public void testRemoveAt()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        chain.addToEnd("C");
        chain.addToEnd("D");
        assertEquals("B", chain.removeAt(1));
        assertEquals("D", chain.removeAt(2));
        chain.addToEnd("E");
        assertEquals("[A, C, E]", chain.toString());
        assertEquals("A", chain.removeAt(0));
        assertEquals("C", chain.removeAt(0));
        assertEquals("E", chain.removeAt(0));
        assertTrue(chain.isEmpty());
        chain.addToEnd("F");
        assertEquals("[F]", chain.toString());
    }


    /**
     * Tests that removeAt() rejects bad indexes without changes.
     */
    public void testRemoveAtBadIndex()
    {
        assertTrue(
            thrownBy(
                () -> chain.removeAt(0)) instanceof IndexOutOfBoundsException);
        chain.addToEnd("A");
        assertTrue(
            thrownBy(
                () -> chain.removeAt(1)) instanceof IndexOutOfBoundsException);
        assertEquals(1, chain.size());
    }


    /**
     * Tests that remove(T) removes the first match and updates the tail.
     */
    public void testRemoveValue()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        chain.addToEnd("A");
        assertTrue(chain.remove("A"));
        assertEquals("[B, A]", chain.toString());
        assertTrue(chain.remove("A"));
        chain.addToEnd("C");
        assertEquals("[B, C]", chain.toString());
    }


    /**
     * Tests that remove(T) returns false for null or absent values.
     */
    public void testRemoveValueBadInput()
    {
        chain.addToEnd("A");
        assertFalse(chain.remove(null));
        assertFalse(chain.remove("Z"));
        assertEquals(1, chain.size());
    }


    /**
     * Tests that contains() finds equal values.
     */
    public void testContains()
    {
        chain.addToEnd("A");
        assertTrue(chain.contains("A"));
    }


    /**
     * Tests that contains() is false for null or absent values.
     */
    public void testContainsBadInput()
    {
        chain.addToEnd("A");
        assertFalse(chain.contains(null));
        assertFalse(chain.contains("B"));
    }


    /**
     * Tests that count() counts duplicates.
     */
    public void testCount()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        chain.addToEnd("A");
        assertEquals(2, chain.count("A"));
    }


    /**
     * Tests that count() is zero for null or absent values.
     */
    public void testCountBadInput()
    {
        assertEquals(0, chain.count(null));
        assertEquals(0, chain.count("A"));
    }


    /**
     * Tests that size() tracks additions and removals.
     */
    public void testSize()
    {
        chain.add("A");
        chain.add("B");
        chain.remove();
        assertEquals(1, chain.size());
        chain.remove();
        assertEquals(0, chain.size());
    }


    /**
     * Tests isEmpty() before and after the final removal.
     */
    public void testIsEmpty()
    {
        chain.add("A");
        assertFalse(chain.isEmpty());
        chain.remove("A");
        assertTrue(chain.isEmpty());
    }


    /**
     * Tests that copy() keeps order but has independent structure.
     */
    public void testCopy()
    {
        chain.addToEnd("A");
        chain.addToEnd("B");
        LinkedChain<String> copy = chain.copy();
        assertEquals("[A, B]", copy.toString());
        copy.removeAt(0);
        copy.addToEnd("C");
        assertEquals("[A, B]", chain.toString());
        assertEquals("[B, C]", copy.toString());
    }


    /**
     * Tests that copying an empty chain gives an empty, usable chain.
     */
    public void testCopyEmpty()
    {
        LinkedChain<String> copy = chain.copy();
        assertTrue(copy.isEmpty());
        copy.addToEnd("A");
        assertTrue(chain.isEmpty());
    }


    // ~ Helpers ...............................................................
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
}
