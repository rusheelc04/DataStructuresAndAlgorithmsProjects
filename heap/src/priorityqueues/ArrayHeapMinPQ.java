package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

// @see ExtrinsicMinPQ

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // Start index of the heap within the array
    static final int START_INDEX = 0;
    // Stores the heaps elements
    List<PriorityNode<T>> items;
    // Maps each item T to its index in the items ArrayList
    private final HashMap<T, Integer> indexMap;

    // Initialization of the above things
    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        indexMap = new HashMap<>();
    }

    // A helper method for swapping the items at two indices of the array heap.
    private void swap(int a, int b) {
        // Update the map and the indices
        indexMap.put(items.get(a).getItem(), b);
        indexMap.put(items.get(b).getItem(), a);
        // Actually swap the items in the items list
        items.set(b, items.set(a, items.get(b)));
    }

    // Adds an item with the given priority to the priority queue. If the item already exists, throws an
    // IllegalArgumentException. After adding, it ensures the heap property is maintained via percolateUp.
    @Override
    public void add(T item, double priority) {
        if (this.contains(item)) {
            throw new IllegalArgumentException();
        }
        items.add(new PriorityNode<>(item, priority)); // Adds the item and its priority to the end of the items list.
        indexMap.put(item, size() - 1); // Maps the item and index in the items list into the HashMap
        percolateUp(size() - 1); // Calls percolateUp to put it in correct place.
    }

    // Percolates item upwards
    private void percolateUp(int i) {
        while (i != 0) { // Continues until the root of the heap is reached. The root is at index 0.
            int parent = (i - 1) / 2; // Calculates the parent's index of the current node.
            if (items.get(i).getPriority() >= items.get(parent).getPriority()) {
                break; // If the current node's priority is higher or equal to its parent, it's in the right place.
            } else {
                this.swap(i, parent); // If it isn't it swaps the current node with its parent.
                i = parent; // Update the index to the parent's index for the next percolation loop
            }
        }
    }

    @Override
    public boolean contains(T item) {
        // Get the index of the item from the indexMap. If the item is present, its index won't be null
        return indexMap.get(item) != null;
    }

    @Override
    public T peekMin() {
        if (this.size() == 0) {
            throw new NoSuchElementException();
        }
        return items.get(0).getItem(); // Return the item at index 0/root, which has the minimum priority.
    }


    // Removes and returns the item with the min priority which is at the root.
    // Replaces root with item at the very end, percolates down
    @Override
    public T removeMin() {
        if (this.size() == 0) {
            throw new NoSuchElementException();
        }
        T min = items.get(0).getItem(); // Gets min priority item at index 0, root of the heap
        swap(0, this.size() - 1); // Swaps the root item with the last item in the heap to prepare for removal.
        items.remove(this.size() - 1); // Removes the last item from the list, was the root (the swap above)
        indexMap.remove(min); // Removes the min item's entry from HashMap
        percolateDown(0); // Percolates down starting from root
        return min; // Returns the item with the minimum priority that was removed
    }

    // Percolation down. Called after removing the min element or changing an element's priority
    private void percolateDown(int index) {
        int left = 2 * index + 1; // Calculates the index of the left child
        int right = 2 * index + 2; // Calculates the index of the right child
        if (left >= this.size()) {
            return; // If left child index is greater or = size, the current node is a leaf, no need to percolate down.
        }
        int next;
        // Determines the child with the smaller priority to potentially swap with the current node.
        if (right >= this.size() || items.get(left).getPriority() <= items.get(right).getPriority()) {
            next = left; // If the right child DOE or the left child has a smaller or equal priority, choose left.
        } else {
            next = right; // Choose the right child because it has a smaller priority.
        }
        // If the current node's priority is greater than the chosen child's priority, swap.
        if (items.get(index).getPriority() > items.get(next).getPriority()) {
            swap(index, next); // Swaps the current node with the chosen child.
            percolateDown(next); // Recursively percolates down
        }
    }

    // Change the priority of an item in the queue. Then percolates down and up to make sure everything is in order
    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int index = indexMap.get(item); // Gets the index of the item via HashMap.
        items.set(index, new PriorityNode<>(item, priority)); // Creates a new Node with given values at its index
        percolateDown(index); // Percolates down if priority higher than before
        percolateUp(index); // Percolates up if priority lower than before
    }

    @Override
    public int size() {
        return items.size(); // Returns the num elemts in the items list
    }
}
