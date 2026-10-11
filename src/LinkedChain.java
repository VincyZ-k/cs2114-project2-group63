/**
 * // -------------------------------------------------------------------------
 * An ordered, singly linked collection that accepts duplicates and rejects null
 * elements. Element matching uses equals.
 * 
 * @author erikkwon
 * @version Oct 11, 2026
 */

public class LinkedChain<T>
{
    // ~ Fields ................................................................
    private Node<T> firstNode;
    private Node<T> lastNode;
    private int numberOfEntries;

    // ~ Constructors ..........................................................
    /**
     * Creates an empty chain.
     */
    public LinkedChain()
    {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }


    // ~Public Methods ........................................................
    /**
     * Adds an element to the front of this chain in O(1) time.
     *
     * @param value
     *            the element to add
     * @return true after the element is added
     * @throws IllegalArgumentException
     *             if value is null
     */
    public boolean add(T value)
    {
        checkValue(value);
        firstNode = new Node<T>(value, firstNode);

        if (numberOfEntries == 0)
        {
            lastNode = firstNode;
        }

        numberOfEntries++;
        return true;
    }


    /**
     * Adds an element to the end of this chain in O(1) time.
     *
     * @param value
     *            the element to add
     * @return true after the element is added
     * @throws IllegalArgumentException
     *             if value is null
     */
    public boolean addToEnd(T value)
    {
        checkValue(value);
        Node<T> node = new Node<T>(value, null);

        if (isEmpty())
        {
            firstNode = node;
        }
        else
        {
            lastNode.next = node;
        }

        lastNode = node;
        numberOfEntries++;
        return true;
    }


    /**
     * Removes the first element in O(1) time.
     *
     * @return the removed element, or null if this chain is empty
     */
    public T remove()
    {
        if (isEmpty())
        {
            return null;
        }

        T value = firstNode.data;
        firstNode = firstNode.next;
        numberOfEntries--;

        if (isEmpty())
        {
            lastNode = null;
        }

        return value;
    }


    /**
     * Inserts an element at the specified zero-based position. Existing
     * elements at and after the position shift right. Takes O(n) time in the
     * worst case.
     *
     * @param index
     *            the insertion position, from zero through size()
     * @param value
     *            the element to insert
     * @throws IllegalArgumentException
     *             if value is null
     * @throws IndexOutOfBoundsException
     *             if index is negative or exceeds size()
     */
    public void insert(int index, T value)
    {
        checkValue(value);

        if (index < 0 || index > numberOfEntries)
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        if (index == 0)
        {
            add(value);
        }
        else if (index == numberOfEntries)
        {
            addToEnd(value);
        }
        else
        {
            Node<T> previous = nodeAt(index - 1);
            previous.next = new Node<T>(value, previous.next);
            numberOfEntries++;
        }
    }


    /**
     * Removes all elements from this chain in O(1) time.
     */
    public void clear()
    {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }


    /**
     * Copies element references into the supplied array in chain order. Array
     * positions beyond the chain's size remain unchanged. Takes O(n) time.
     *
     * @param values
     *            the destination array
     * @return the supplied array containing the copied references
     * @throws IllegalArgumentException
     *             if values is null or too small
     */
    public T[] toArray(T[] values)
    {
        if (values == null || values.length < numberOfEntries)
        {
            throw new IllegalArgumentException("Array is null or too small.");
        }

        Node<T> current = firstNode;
        int index = 0;

        while (current != null)
        {
            values[index++] = current.data;
            current = current.next;
        }

        return values;
    }


    /**
     * Returns the elements in chain order, formatted as [a, b, c].
     *
     * @return a string representation of this chain
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("[");
        Node<T> current = firstNode;

        while (current != null)
        {
            result.append(current.data);
            current = current.next;

            if (current != null)
            {
                result.append(", ");
            }
        }

        return result.append("]").toString();
    }


    /**
     * Retrieves an element without removing it. Takes O(n) time in the worst
     * case.
     *
     * @param index
     *            the element's zero-based position
     * @return the element at index
     * @throws IndexOutOfBoundsException
     *             if index is outside this chain
     */
    public T get(int index)
    {
        checkIndex(index);
        return nodeAt(index).data;
    }


    /**
     * Removes the element at the specified zero-based position. Takes O(n) time
     * in the worst case.
     *
     * @param index
     *            the position of the element to remove
     * @return the removed element
     * @throws IndexOutOfBoundsException
     *             if index is outside this chain
     */
    public T removeAt(int index)
    {
        checkIndex(index);

        if (index == 0)
        {
            return remove();
        }

        Node<T> previous = nodeAt(index - 1);
        Node<T> removed = previous.next;
        previous.next = removed.next;

        if (removed == lastNode)
        {
            lastNode = previous;
        }

        numberOfEntries--;
        return removed.data;
    }


    /**
     * Removes the first element equal to value. Takes O(n) time in the worst
     * case.
     *
     * @param value
     *            the element to match
     * @return true if an element was removed; false if value is null or absent
     */
    public boolean remove(T value)
    {
        if (value == null)
        {
            return false;
        }

        Node<T> previous = null;
        Node<T> current = firstNode;

        while (current != null)
        {
            if (value.equals(current.data))
            {
                if (previous == null)
                {
                    remove();
                }
                else
                {
                    previous.next = current.next;

                    if (current == lastNode)
                    {
                        lastNode = previous;
                    }

                    numberOfEntries--;
                }

                return true;
            }

            previous = current;
            current = current.next;
        }

        return false;
    }


    /**
     * Checks whether this chain contains an element equal to value. Takes O(n)
     * time in the worst case.
     *
     * @param value
     *            the element to find
     * @return true if a match exists; false if value is null or absent
     */
    public boolean contains(T value)
    {
        if (value == null)
        {
            return false;
        }

        Node<T> current = firstNode;

        while (current != null)
        {
            if (value.equals(current.data))
            {
                return true;
            }

            current = current.next;
        }

        return false;
    }


    /**
     * Counts elements equal to value in O(n) time.
     *
     * @param value
     *            the element to match
     * @return the number of matches, or zero if value is null or absent
     */
    public int count(T value)
    {
        if (value == null)
        {
            return 0;
        }

        int matches = 0;
        Node<T> current = firstNode;

        while (current != null)
        {
            if (value.equals(current.data))
            {
                matches++;
            }

            current = current.next;
        }

        return matches;
    }


    /**
     * Returns the number of elements in O(1) time.
     *
     * @return the number of elements in this chain
     */
    public int size()
    {
        return numberOfEntries;
    }


    /**
     * Checks whether this chain is empty in O(1) time.
     *
     * @return true if this chain contains no elements
     */
    public boolean isEmpty()
    {
        return numberOfEntries == 0;
    }


    /**
     * Creates a structural copy in O(n) time and space. The copy has
     * independent nodes in the same order but shares element references with
     * this chain.
     *
     * @return a new chain containing the same element references
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
     * Validates an element before insertion.
     *
     * @param value
     *            the element to validate
     * @throws IllegalArgumentException
     *             if value is null
     */
    private void checkValue(T value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Value cannot be null.");
        }
    }


    /**
     * Validates an index referring to an existing element.
     *
     * @param index
     *            the zero-based index to validate
     * @throws IndexOutOfBoundsException
     *             if index is outside this chain
     */
    private void checkIndex(int index)
    {
        if (index < 0 || index >= numberOfEntries)
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }


    /**
     * Finds a node at an already validated index.
     *
     * @param index
     *            a valid zero-based index
     * @return the node at index
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
     * Stores one element and a link to the next node.
     *
     * @param <E>
     *            the type of element stored in this node
     */
    private static class Node<E>
    {
        private E data;
        private Node<E> next;

        /**
         * Creates a node with an element and a next link.
         *
         * @param data
         *            the element to store
         * @param next
         *            the following node, or null for the final node
         */
        private Node(E data, Node<E> next)
        {
            this.data = data;
            this.next = next;
        }
    }
}
