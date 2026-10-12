/**
 * Calculates which units are missing from, or extra in, a packed list.
 * Duplicates are quantities: requiring three socks and packing two leaves one
 * missing sock. Neither input chain is modified. Algorithm: copy one list into
 * an "unmatched" chain, walk the other list in order, and remove one matching
 * occurrence from unmatched for each item. Items with no match are the result.
 *
 * @author erikkwon
 * @version October 11, 2026
 */
public final class PackingComparator
{
    /**
     * Not instantiable; the comparator has no state.
     */
    private PackingComparator()
    {
        // static methods only
    }

    /**
     * Returns the required units that were not packed, in required-list order.
     *
     * @param required
     *            the required items
     * @param packed
     *            the packed items
     * @return a new chain of missing units
     * @throws IllegalArgumentException
     *             if either chain is null
     */
    public static LinkedChain<PackingItem> missing(
        LinkedChain<PackingItem> required,
        LinkedChain<PackingItem> packed)
    {
        requireChains(required, packed);
        return unmatched(required, packed);
    }

    /**
     * Returns the packed units that were not required, in packed-list order.
     *
     * @param required
     *            the required items
     * @param packed
     *            the packed items
     * @return a new chain of extra units
     * @throws IllegalArgumentException
     *             if either chain is null
     */
    public static LinkedChain<PackingItem> extra(
        LinkedChain<PackingItem> required,
        LinkedChain<PackingItem> packed)
    {
        requireChains(required, packed);
        return unmatched(packed, required);
    }

    /**
     * Returns the units of wanted that have no partner in available.
     *
     * @param wanted
     *            the list whose order the result follows
     * @param available
     *            the list that cancels out matching units
     * @return a new chain of unmatched units from wanted
     */
    private static LinkedChain<PackingItem> unmatched(
        LinkedChain<PackingItem> wanted,
        LinkedChain<PackingItem> available)
    {
        LinkedChain<PackingItem> pool = available.copy();
        LinkedChain<PackingItem> result = new LinkedChain<PackingItem>();

        for (int i = 0; i < wanted.size(); i++)
        {
            PackingItem item = wanted.get(i);
            if (!pool.remove(item))
            {
                result.addToEnd(item);
            }
        }
        return result;
    }

    /**
     * Rejects null input chains.
     *
     * @param required
     *            the required chain
     * @param packed
     *            the packed chain
     * @throws IllegalArgumentException
     *             if either is null
     */
    private static void requireChains(
        LinkedChain<PackingItem> required,
        LinkedChain<PackingItem> packed)
    {
        if (required == null || packed == null)
        {
            throw new IllegalArgumentException("Both lists are required.");
        }
    }
}
