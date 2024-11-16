package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// A quick-union-by-size data structure with path compression.
// @see DisjointSets for more documentation.
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // stores parent index or size of each set. negative means size of the set, positive means parent index
    List<Integer> pointers;
    // stores the mapping from elems to their index in the pointers list
    private HashMap<T, Integer> locations;

    // constructor initializes the above items
    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        locations = new HashMap<>();
    }

    // Makes new set with a single item.
    @Override
    public void makeSet(T item) {
        if (locations.get(item) != null) { // Check if the item already exists in any set
            throw new IllegalArgumentException();
        }
        pointers.add(-1); // Add -1 to pointers, means a new set with a size of 1
        // store the newly added item's index (current size of pointers minus 1) in the map.
        locations.put(item, pointers.size() - 1);
    }

    // Find the root of given item. path compression for future searches. traverses up the tree from the given item to
    // find the root and path compressing setting each visited node to the root.
    @Override
    public int findSet(T item) {
        if (locations.get(item) == null) {
            throw new IllegalArgumentException();
        }
        HashSet<Integer> visited = new HashSet<>(); // stores all visited indexes for path compression
        int index = locations.get(item); // get index of the item's set
        int pointer = pointers.get(index); // get parent pointer or set size for the current index
        while (pointer >= 0) { // loop until root is found, pointer <= 0 is root
            visited.add(index); // add current index to visited for later path compression.
            index = pointer; // move to the parent index
            pointer = pointers.get(index); // update pointer to the next parent index.
        }
        for (int i : visited) { // compress the path for all visited indexes
            pointers.set(i, index); // set their parent to root
        }
        // return the root index of set
        return index;
    }


    // Merges sets that contain given items if in different sets, unionizes by size. If the items are in same set, it
    // returns false.
    @Override
    public boolean union(T item1, T item2) {
        // find root index for both items sets
        int index1 = findSet(item1);
        int index2 = findSet(item2);
        if (index1 == index2) { // If both roots are the same, they are both in the same index
            return false; // return false
        }
        // Add sizes of both sets to find total size. sizes are negative so addition subtracts their absolute values
        int totalWeight = pointers.get(index1) + pointers.get(index2);
        // Figure which set is smaller and merge smaller into larger one
        if (pointers.get(index1) <= pointers.get(index2)) {
            // If first set smaller or equal, merge into second, updating first roots pointer to total weight
            // and making the second set's root point to the first.
            pointers.set(index1, totalWeight);
            pointers.set(index2, index1);
        } else {
            // If the second set smaller, merge into first, updating second roots pointer to total weight
            // and making the first set's root point to the second.
            pointers.set(index2, totalWeight);
            pointers.set(index1, index2);
        }
        // return true
        return true;
    }
}
