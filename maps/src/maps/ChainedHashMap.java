package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

// @see AbstractIterableMap
// @see Map

public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 2;

    // Warning:
    // You may not rename this field or change its type.
    // We will be inspecting it in our secret tests.
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    // added variables to log if users change defaults
    private double loadFactorThreshold;
    private int initialChainCapacity;

    // Constructs a new ChainedHashMap with default resizing load factor threshold,
    // default initial chain count, and default initial chain capacity.

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    // Constructs a new ChainedHashMap with the given parameters.
    //
    // @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
    //                                     exceeds this value, the hash table resizes. Must be > 0.
    // @param initialChainCount the initial number of chains for your hash table. Must be > 0.
    // @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
    //                              Must be > 0.
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        if (resizingLoadFactorThreshold <= 0 || initialChainCount <= 0 || chainInitialCapacity <= 0) {
            throw new IllegalArgumentException("Parameters must be greater than 0");
        }
        // logs if user changes values from default.
        this.loadFactorThreshold = resizingLoadFactorThreshold;
        this.initialChainCapacity = chainInitialCapacity;

        chains = createArrayOfChains(initialChainCount);
        for (int i = 0; i < chains.length; i++) {
            chains[i] = createChain(chainInitialCapacity);
        }
    }

    // This method will return a new, empty array of the given size that can contain
    // {@code AbstractIterableMap<K, V>} objects.
    //
    // Note that each element in the array will initially be null.
    //
    // Note: You do not need to modify this method.
    // @see ArrayMap createArrayOfEntries method for more background on why we need this method
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    // Returns a new chain.
    //
    // This method will be overridden by the grader so that your ChainedHashMap implementation
    //
    // is graded using our solution ArrayMaps.
    //
    // Note: You do not need to modify this method.
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        // gets the index from helper method
        int index = getChainIndex(key);
        // return the key that is at the index
        return chains[index].get(key);
    }

    // uses the java hashCode for a key and mods it by the length of the array
    private int getChainIndex(Object key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % chains.length;
    }

    @Override
    public V put(K key, V value) {
        // check to see if a resize is needed
        resizeCheck();
        // gets the index from the helper method
        int index = getChainIndex(key);
        // Obtain the chain at the index if there is any
        AbstractIterableMap<K, V> targetChain = chains[index];
        // Get the old value for the key from the target chain
        V oldValue = targetChain.get(key);
        // Put the new key and value into the target chain
        targetChain.put(key, value);
        // return the old value
        return oldValue;
    }

    // TODO: Some kind of issue with the timing. Maybe try and repostion this method call in the put method
    private void resizeCheck() {
        // Calculate the load factor
        double loadFactor = (double) size() / chains.length;
        // Check if loadFactor is greater than the threshold. If it is, resize.
        if (loadFactor > loadFactorThreshold) {
            // Double the number of chains
            int newChainCount = chains.length * 2;
            // Create new object with the doubled size
            AbstractIterableMap<K, V>[] newChains = createArrayOfChains(newChainCount);

            // Initialize the chains and give the correct capacity to them.
            for (int i = 0; i < newChainCount; i++) {
                newChains[i] = createChain(initialChainCapacity);
            }

            // Rehash all entries. For each chain in the chains object, for each entry in each chain,
            // get the key and the value. Find the new index to put into the new object and put the
            // key and value into the new object at that new index.
            for (AbstractIterableMap<K, V> chain : chains) {
                if (chain != null) {
                    for (Map.Entry<K, V> entry : chain) {
                        K key = entry.getKey();
                        V value = entry.getValue();
                        int newIndex = Math.abs(key.hashCode()) % newChainCount;
                        newChains[newIndex].put(key, value);
                    }
                }
            }

            // Replace old chains with new chains
            chains = newChains;
        }
    }

    // Get the index of the key. Remove and return that value associated with the key.
    @Override
    public V remove(Object key) {
        int index = getChainIndex(key);
        return chains[index].remove(key);
    }

    // Removes all key-value pairs in each chain.
    @Override
    public void clear() {
        for (AbstractIterableMap<K, V> chain : chains) {
            chain.clear();
        }
    }

    // Get the index from the key. Then returns if that chain contains key
    @Override
    public boolean containsKey(Object key) {
        int index = getChainIndex(key);
        return chains[index].containsKey(key);
    }

    // Add up the number of items in each chain
    @Override
    public int size() {
        int size = 0;
        for (AbstractIterableMap<K, V> chain : chains) {
            size += chain.size();
        }
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // removed this toString implementation
    // Doing so will give you a better string representation for assertion errors the debugger.
    // @Override
    // public String toString() {
    //     return super.toString();
    // }

    // See the assignment webpage for tips and restrictions on implementing this iterator.
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        // An array of chains. In each chain there are key value pairs
        private AbstractIterableMap<K, V>[] chains;
        // The index of current chain being accessed
        private int chainIndex;
        // Goes through the entries of the chain being accessed
        private Iterator<Map.Entry<K, V>> currentIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains; // Assign the chains from map to iterator.
            this.chainIndex = 0; // Start at chain0
            this.currentIterator = null; // No current chain selected yet because current chain0 might be null
            moveToNextChain(); // Moves to the first chain that has entries can be chain0
        }

        private void moveToNextChain() {
            // Loops until chain with entries is found or all chains null
            while (chainIndex < chains.length && (chains[chainIndex] == null
                   || !chains[chainIndex].iterator().hasNext())) {
                chainIndex++;
            }
            // If chain with entries found, current iterator is set to it
            if (chainIndex < chains.length) {
                currentIterator = chains[chainIndex].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            // If the current chain has more entries k-v pairs return true. Else find next chain with entries
            if (currentIterator != null && currentIterator.hasNext()) {
                return true;
            } else {
                chainIndex++;
                moveToNextChain();
                // Returns true if currentIterator exists and if the new current chain has entries
                return currentIterator != null && currentIterator.hasNext();
            }
        }

        // Returns the next entry and advances the iterator.
        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Gets the next entry in what even chain its in
            Map.Entry<K, V> nextEntry = currentIterator.next();
            // If the current chain has no more entries, move to the next chain.
            if (!currentIterator.hasNext()) {
                chainIndex++;
                moveToNextChain();
            }
            // Return the next entry.
            return nextEntry;
        }
    }
}
