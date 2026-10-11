
/**
 * A generic chain of linked nodes. Use it as the storage inside your own
 * classes, and extend or modify it as your project needs. This class owns
 * storage only: nodes, the head and tail references, and the count. It contains
 * no application logic. A class that needs a collection should hold a
 * LinkedChain field and call its methods. add() places the new entry at the
 * FRONT of the chain, so entries added with add() come back from toArray() in
 * reverse order of insertion. addToEnd() preserves insertion order. PackCheck
 * changes to the provided class: a lastNode reference so that addToEnd() runs
 * in O(1), and the new methods insert(int, T), removeAt(int), and copy(). Every
 * existing public signature and behavior is unchanged. Invariant: an empty
 * chain has null firstNode and lastNode and zero numberOfEntries. Otherwise
 * exactly numberOfEntries nodes are reachable from firstNode, lastNode
 * references the last of them, and lastNode.next is null. Time complexity,
 * where n is the number of entries: add O(1) addToEnd O(1) insert O(n) remove()
 * O(1) remove(T) O(n) removeAt O(n) clear O(1) get O(n) contains O(n) count
 * O(n) size O(1) isEmpty O(1) toArray O(n) copy O(n)
 *
 * @author CS 2114 Staff
 * @version October 2026
 * @param <T>
 *            the type of entries stored in the chain
 */
public class LinkedChain<T>
{
    private Node<T> firstNode;   // reference to the first node, or null
    private Node<T> lastNode;    // reference to the last node, or null
    private int numberOfEntries; // number of nodes in the chain

    /**
     * Creates an empty chain.
     */
    public LinkedChain()
    {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }


    /**
     * Adds a new entry to the front of this chain.
     *
     * @param newEntry
     *            the object to be added; must not be null
     * @return true
     * @throws IllegalArgumentException
     *             if newEntry is null
     */
    public boolean add(T newEntry)
    {
        requireNonNull(newEntry);
        firstNode = new Node<T>(newEntry, firstNode);
        if (lastNode == null)
        {
            lastNode = firstNode;
        }
        numberOfEntries++;
        return true;
    }


    /**
     * Adds a new entry to the end of this chain.
     *
     * @param newEntry
     *            the object to be added; must not be null
     * @return true
     * @throws IllegalArgumentException
     *             if newEntry is null
     */
    public boolean addToEnd(T newEntry)
    {
        requireNonNull(newEntry);
        Node<T> newNode = new Node<T>(newEntry);
        if (firstNode == null)
        {
            firstNode = newNode;
        }
        else
        {
            lastNode.next = newNode;
        }
        lastNode = newNode;
        numberOfEntries++;
        return true;
    }


    /**
     * Inserts a new entry at the given zero-based position. Index 0 inserts at
     * the front; index size() appends.
     *
     * @param index
     *            the position, from 0 through size()
     * @param newEntry
     *            the object to be added; must not be null
     * @throws IllegalArgumentException
     *             if newEntry is null
     * @throws IndexOutOfBoundsException
     *             if index is negative or greater than size()
     */
    public void insert(int index, T newEntry)
    {
        requireNonNull(newEntry);
        if (index < 0 || index > numberOfEntries)
        {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for insertion into size "
                    + numberOfEntries);
        }
        if (index == 0)
        {
            add(newEntry);
        }
        else if (index == numberOfEntries)
        {
            addToEnd(newEntry);
        }
        else
        {
            Node<T> before = nodeAt(index - 1);
            before.next = new Node<T>(newEntry, before.next);
            numberOfEntries++;
        }
    }


    /**
     * Removes and returns the first entry in this chain, if possible.
     *
     * @return the removed entry, or null if the chain is empty
     */
    public T remove()
    {
        if (firstNode == null)
        {
            return null;
        }
        T result = firstNode.data;
        firstNode = firstNode.next;
        if (firstNode == null)
        {
            lastNode = null;
        }
        numberOfEntries--;
        return result;
    }


    /**
     * Removes the first occurrence of a given entry from this chain.
     *
     * @param anEntry
     *            the entry to be removed
     * @return true if the removal was successful, or false if not
     */
    public boolean remove(T anEntry)
    {
        if (anEntry == null || firstNode == null)
        {
            return false;
        }
        if (anEntry.equals(firstNode.data))
        {
            remove();
            return true;
        }
        Node<T> previous = firstNode;
        Node<T> current = firstNode.next;
        while (current != null)
        {
            if (anEntry.equals(current.data))
            {
                unlinkAfter(previous);
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }


    /**
     * Removes and returns the entry at the given zero-based position.
     *
     * @param index
     *            the position, from 0 through size() - 1
     * @return the removed entry
     * @throws IndexOutOfBoundsException
     *             if index is negative or greater than or equal to size()
     */
    public T removeAt(int index)
    {
        checkIndex(index);
        if (index == 0)
        {
            return remove();
        }
        Node<T> before = nodeAt(index - 1);
        T result = before.next.data;
        unlinkAfter(before);
        return result;
    }


    /**
     * Removes all entries from this chain.
     */
    public void clear()
    {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }


    /**
     * Gets the entry at the given position without removing it.
     *
     * @param index
     *            the zero-based position
     * @return the entry at that position
     * @throws IndexOutOfBoundsException
     *             if index is negative or index is greater than or equal to
     *             size()
     */
    public T get(int index)
    {
        checkIndex(index);
        return nodeAt(index).data;
    }


    /**
     * Tests whether this chain contains a given entry.
     *
     * @param anEntry
     *            the entry to locate
     * @return true if the chain contains anEntry, or false if not
     */
    public boolean contains(T anEntry)
    {
        return count(anEntry) > 0;
    }


    /**
     * Counts the number of times a given entry appears in this chain.
     *
     * @param anEntry
     *            the entry to be counted
     * @return the number of times anEntry appears in the chain
     */
    public int count(T anEntry)
    {
        if (anEntry == null)
        {
            return 0;
        }
        int frequency = 0;
        Node<T> current = firstNode;
        while (current != null)
        {
            if (anEntry.equals(current.data))
            {
                frequency++;
            }
            current = current.next;
        }
        return frequency;
    }


    /**
     * Gets the number of entries currently in this chain.
     *
     * @return the integer number of entries in the chain
     */
    public int size()
    {
        return numberOfEntries;
    }


    /**
     * Sees whether this chain is empty.
     *
     * @return true if the chain has no entries, or false if not
     */
    public boolean isEmpty()
    {
        return numberOfEntries == 0;
    }


    /**
     * Copies every entry in this chain into the given array, first entry first.
     *
     * @param values
     *            an array of the correct type to fill; it must have length at
     *            least size()
     * @return the array containing all entries in the chain
     * @throws IllegalArgumentException
     *             if values is null or too small
     */
    public T[] toArray(T[] values)
    {
        if (values == null)
        {
            throw new IllegalArgumentException("Array must not be null");
        }
        if (values.length < numberOfEntries)
        {
            throw new IllegalArgumentException(
                "Array of length " + values.length + " is too small for "
                    + numberOfEntries + " entries");
        }
        int index = 0;
        Node<T> current = firstNode;
        while (current != null)
        {
            values[index] = current.data;
            index++;
            current = current.next;
        }
        return values;
    }


    /**
     * Returns a new chain with new nodes holding the same entries in the same
     * order. The entries themselves are shared, not cloned, so changing the
     * copy's structure never affects this chain.
     *
     * @return a structural copy of this chain
     */
    public LinkedChain<T> copy()
    {
        LinkedChain<T> result = new LinkedChain<T>();
        Node<T> current = firstNode;
        while (current != null)
        {
            result.addToEnd(current.data);
            current = current.next;
        }
        return result;
    }


    /**
     * Returns a readable listing of the chain, first entry first, e.g. "[a, b,
     * c]".
     *
     * @return a string representation of this chain
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = firstNode;
        while (current != null)
        {
            sb.append(current.data);
            if (current.next != null)
            {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Rejects null entries.
     *
     * @param entry
     *            the entry to check
     * @throws IllegalArgumentException
     *             if entry is null
     */
    private void requireNonNull(T entry)
    {
        if (entry == null)
        {
            throw new IllegalArgumentException("Cannot add null to chain");
        }
    }


    /**
     * Rejects indexes that do not name an existing entry.
     *
     * @param index
     *            the zero-based position to check
     * @throws IndexOutOfBoundsException
     *             if index is negative or greater than or equal to size()
     */
    private void checkIndex(int index)
    {
        if (index < 0 || index >= numberOfEntries)
        {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for size "
                    + numberOfEntries);
        }
    }


    /**
     * Walks to the node at a position already known to be valid.
     *
     * @param index
     *            the zero-based position
     * @return the node at that position
     */
    private Node<T> nodeAt(int index)
    {
        Node<T> current = firstNode;
        for (int i = 0; i < index; i++)
        {
            current = current.next;
        }
        return current;
    }


    /**
     * Unlinks the node after the given one, updating lastNode if the removed
     * node was the tail.
     *
     * @param previous
     *            a node whose next node exists
     */
    private void unlinkAfter(Node<T> previous)
    {
        Node<T> removed = previous.next;
        previous.next = removed.next;
        if (removed == lastNode)
        {
            lastNode = previous;
        }
        numberOfEntries--;
    }

    /**
     * A node in the chain. Holds one entry and a link to the next node.
     *
     * @param <E>
     *            the type of data held in the node
     */
    private static class Node<E>
    {
        private E data;        // entry stored in this node
        private Node<E> next;  // link to the next node, or null

        /**
         * Creates a node with the given data and no next node.
         *
         * @param dataPortion
         *            the entry to store
         */
        private Node(E dataPortion)
        {
            this(dataPortion, null);
        }


        /**
         * Creates a node with the given data and next link.
         *
         * @param dataPortion
         *            the entry to store
         * @param nextNode
         *            the node that follows this one
         */
        private Node(E dataPortion, Node<E> nextNode)
        {
            data = dataPortion;
            next = nextNode;
        }
    }
}
