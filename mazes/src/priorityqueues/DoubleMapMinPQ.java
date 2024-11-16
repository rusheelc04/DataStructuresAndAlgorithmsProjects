package priorityqueues;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

// Use this PQ implementation if you don't want to use your ArrayHeapMinPQ.
// This implementation is, in theory, slower than ArrayHeapMinPQ.
// Implements a priority queue with priorities provided externally, avoiding duplicates.
public class DoubleMapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // Maps each priority to a set of items with that priority.
    private final TreeMap<Double, Set<T>> priorityToItem = new TreeMap<>();
    // Maps each item to its priority, ensuring quick updates and checks.
    private final HashMap<T, Double> itemToPriority = new HashMap<>();

    // Constructor initializes an empty priority queue.
    public DoubleMapMinPQ() {}

    // Helper method to retrieve an item from a set.
    private static <K> K getItem(Set<K> s) {
        Iterator<K> i = s.iterator(); // Create an iterator for the set.
        return i.next(); // Return the first item found.
    }

    // Adds an item with a specific priority to the PQ.
    @Override
    public void add(T item, double priority) {
        if (item == null) { // Check if the item is null.
            throw new IllegalArgumentException("Item is null, but null items are not supported");
        }
        if (this.itemToPriority.containsKey(item)) { // Check if the item already exists in the PQ.
            throw new IllegalArgumentException("Already contains " + item);
        }
        this.priorityToItem.putIfAbsent(priority, new HashSet<>()); // Add priority if it's not already present.
        Set<T> itemsWithPriority = this.priorityToItem.get(priority); // Get the set of items with this priority.
        itemsWithPriority.add(item); // Add the item to the set.
        this.itemToPriority.put(item, priority); // Map the item to its priority.
    }

    // Checks if the PQ contains a specific item.
    @Override
    public boolean contains(T item) {
        return this.itemToPriority.containsKey(item); // Return true if the item is in the map.
    }

    // Returns the item with the lowest priority.
    @Override
    public T peekMin() {
        if (this.itemToPriority.isEmpty()) { // Check if the PQ is empty.
            throw new NoSuchElementException("PQ is empty.");
        }
        double lowestPriority = this.priorityToItem.firstKey(); // Get the lowest priority.
        Set<T> itemsWithLowestPriority = priorityToItem.get(lowestPriority); // Get the items with the lowest priority.
        return getItem(itemsWithLowestPriority); // Return one of these items.
    }

    // Removes and returns the item with the lowest priority.
    @Override
    public T removeMin() {
        if (this.itemToPriority.isEmpty()) { // Check if the PQ is empty.
            throw new NoSuchElementException("PQ is empty.");
        }
        double lowestPriority = this.priorityToItem.firstKey(); // Get the lowest priority.
        // Get the items with the lowest priority.
        Set<T> itemsWithLowestPriority = this.priorityToItem.get(lowestPriority);
        T item = getItem(itemsWithLowestPriority); // Get one of these items.
        itemsWithLowestPriority.remove(item); // Remove the item from its set.
        if (itemsWithLowestPriority.isEmpty()) { // If the set is now empty,
            this.priorityToItem.remove(lowestPriority); // remove the priority from the map.
        }
        this.itemToPriority.remove(item); // Remove the item from the item-to-priority map.
        return item; // Return the removed item.
    }

    // Changes the priority of a specific item.
    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) { // Check if the item is in the PQ.
            throw new IllegalArgumentException(item + " not in PQ.");
        }
        double oldPriority = this.itemToPriority.get(item); // Get the old priority of the item.
        // Get the set of items with the old priority.
        Set<T> itemsWithOldPriority = this.priorityToItem.get(oldPriority);
        itemsWithOldPriority.remove(item); // Remove the item from this set.
        if (itemsWithOldPriority.isEmpty()) { // If the set is now empty,
            this.priorityToItem.remove(oldPriority); // remove the old priority from the map.
        }
        this.itemToPriority.remove(item); // Remove the item from the item-to-priority map.
        add(item, priority); // Re-add the item with its new priority.
    }

    // Returns the number of items in the PQ.
    @Override
    public int size() {
        return this.itemToPriority.size(); // Return the size of the item-to-priority map.
    }

    // Checks if the PQ is empty.
    @Override
    public boolean isEmpty() {
        return size() == 0; // Return true if the size is 0.
    }
}
