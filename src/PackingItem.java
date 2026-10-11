/**
 * An immutable packing item identified by its normalized name and category. Two
 * items are equal when both fields match, so "Socks" in "CLOTHING" equals
 * "socks" in "clothing".
 * 
 * @author erikkwon
 * @version Oct 11, 2026
 */
public class PackingItem
{
    // ~ Fields ................................................................
    private final String name;
    private final String category;

    // ~ Constructors ..........................................................
    /**
     * Creates an item, trimming and lowercasing both fields.
     *
     * @param name
     *            the item name, at most 60 characters after trimming
     * @param category
     *            the category, at most 30 characters after trimming
     * @throws IllegalArgumentException
     *             if either field is null, blank, or too long
     */
    public PackingItem(String name, String category)
    {
        this.name =
            TextRules.normalized(name, "Item name", TextRules.MAX_ITEM_NAME);
        this.category =
            TextRules.normalized(category, "Category", TextRules.MAX_CATEGORY);
    }


    // ~Public Methods ........................................................
    /**
     * Gets the normalized item name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets the normalized category.
     *
     * @return the category
     */
    public String getCategory()
    {
        return category;
    }


    /**
     * Items are equal when their normalized names and categories match.
     *
     * @param other
     *            the object to compare against
     * @return true if other is an equal PackingItem
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || getClass() != other.getClass())
        {
            return false;
        }
        PackingItem that = (PackingItem)other;
        return name.equals(that.name) && category.equals(that.category);
    }


    /**
     * Returns a hash consistent with equals.
     *
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return 31 * name.hashCode() + category.hashCode();
    }


    /**
     * Formats the item as "socks [clothing]".
     *
     * @return the formatted item
     */
    @Override
    public String toString()
    {
        return name + " [" + category + "]";
    }
}
