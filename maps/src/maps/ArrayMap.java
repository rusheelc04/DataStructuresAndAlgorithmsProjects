package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

// @see AbstractIterableMap
// @see Map

public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10; // Changed the default starting value to 10.
    SimpleEntry<K, V>[] entries;

    // Constructs a new ArrayMap with default initial capacity.
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    // Constructs a new ArrayMap with the given initial capacity (i.e., the initial
    // size of the internal array).
    //
    // @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
    public ArrayMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = DEFAULT_INITIAL_CAPACITY; // resort to the default capacity
        }
        this.entries = this.createArrayOfEntries(initialCapacity);
    }

    // This method will return a new, empty array of the given size that can contain
    // {@code Entry<K, V>} objects.
    //
    // Note that each element in the array will initially be null.
    //
    // Note: You do not need to modify this method.
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        // It turns out that creating arrays of generic objects in Java is complicated due to something
        // known as "type erasure."

        // We've given you this helper method to help simplify this part of your assignment. Use this
        // helper method as appropriate when implementing the rest of this class.

        // You are not required to understand how this method works, what type erasure is, or how
        // arrays and generics interact.
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    // Commenting this method out since default method works
    // @Override
    // public V get(Object key) {
    //     for (SimpleEntry<K, V> entry : entries) {
    //         if (entry != null && entry.getKey().equals(key)) {
    //             return entry.getValue();
    //         }
    //     }
    //     return null;
    // }

    @Override
    public V put(K key, V value) {
        // If key is found, replace the keys value with the new value and return the old value.
        for (SimpleEntry<K, V> entry : entries) {
            if (entry != null && entry.getKey().equals(key)) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
        }

        // If key is not found, find null entry and put the key value pair in.
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] == null) {
                entries[i] = new SimpleEntry<>(key, value);
                return null;
            }
        }

        // If no null spots are remaining we resize and try again.
        expandArray();
        return put(key, value);
    }

    private void expandArray() {
        // Create a new array double the size of the entries
        SimpleEntry<K, V>[] newArray = createArrayOfEntries(entries.length * 2);
        // put all entries in the new array
        for (int i = 0; i < entries.length; i++) {
            newArray[i] = entries[i];
        }
        // entries set to the new array
        entries = newArray;
    }

    @Override
    public V remove(Object key) {
        // loops through entries, if an entry isn't null and given key equals the key at i, that
        // entry is replaced with the last entry, and the last entry is set to null. The value
        // of the removed entry is returned
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null && Objects.equals(entries[i].getKey(), key)) {
                V value = entries[i].getValue();
                entries[i] = entries[entries.length - 1];
                entries[entries.length - 1] = null;
                return value;
            }
        }
        // if the target key isn't found in entries, null is returned.
        return null;
    }

    // Commenting this method out since default method works
    // @Override
    // public void clear() {
    //     for (int i = 0; i < entries.length; i++) {
    //         entries[i] = null;
    //     }
    // }

    // Commenting this method out since default method works
    // @Override
    // public boolean containsKey(Object key) {
    //     for (SimpleEntry<K, V> entry : entries) {
    //         if (entry != null && entry.getKey().equals(key)) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    @Override
    public int size() {
        int size = 0;
        for (SimpleEntry<K, V> entry : entries) {
            if (entry != null) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    // Commented out the toString method
    // Doing so will give you a better string representation for assertion errors the debugger.
    // @Override
    // public String toString() {
    //     return super.toString();
    // }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index = 0;
        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
        }

        // while the index is less than the entries length and entries at index is null, increment
        // index by one. If an entry is found next, index will be less than entries length and
        // return true. If an entry isn't found, index will equal entries length and return false.
        @Override
        public boolean hasNext() {
            while (index < entries.length && entries[index] == null) {
                index++;
            }
            return index < entries.length;
        }

        // if the hasNext() is false, a NoSuchElementException is thrown. If hasNext() is true
        // the entry at the next index is returned.
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return entries[index++];
        }
    }

}