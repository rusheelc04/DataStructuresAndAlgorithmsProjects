package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


// Computes shortest paths using Dijkstra's algorithm.
// @see SPTShortestPathFinder for more documentation.
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        // If you have confidence in your heap implementation, you can disable the line above
        // and enable the one below.

        // return new ArrayHeapMinPQ<>();

        // Otherwise, do not change this method.
        // We override this during grading to test your code using our correct implementation so that
        // you don't lose extra points if your implementation is buggy.
    }


    // Constructs a shortest paths tree (SPT) from the start to the end vertex using Dijkstra's algorithm.
    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        // Initialize a map to hold each vertex's closest incoming edge on the shortest path.
        HashMap<V, E> edgeTo = new HashMap<>();
        // Return immediately if start and end are the same, as no path needs to be constructed.
        if (start.equals(end)) {
            return edgeTo;
        }
        // Track visited vertices to avoid revisiting.
        HashSet<V> known = new HashSet<>();
        // Record shortest distance from start to each vertex; all start as infinity.
        HashMap<V, Double> distTo = new HashMap<>();
        // Priority queue to efficiently find the next closest vertex to process.
        ExtrinsicMinPQ<V> closestHeap = createMinPQ();

        // Set start vertex's distance to 0 (start of path) and mark it as having no incoming edge.
        edgeTo.put(start, null);
        distTo.put(start, 0.0);
        // Add start vertex to priority queue with a priority of 0.
        closestHeap.add(start, 0.0);
        // Initialize distances to infinity for all vertices directly reachable from start.
        for (E edge : graph.outgoingEdgesFrom(start)) {
            if (!distTo.containsKey(edge.to())) {
                distTo.put(edge.to(), Double.POSITIVE_INFINITY);
                closestHeap.add(edge.to(), Double.POSITIVE_INFINITY);
            }
        }

        // Process vertices until there are none left to check.
        while (!closestHeap.isEmpty()) {
            // Remove and process the closest unvisited vertex.
            V closest = closestHeap.removeMin();
            // Mark this vertex as visited.
            known.add(closest);
            // If this vertex is the destination, stop; the shortest path is found.
            if (closest.equals(end)) {
                return edgeTo;
            }
            // Relax all edges from the current vertex to update distances to neighbors.
            for (E edge : graph.outgoingEdgesFrom(closest)) {
                V to = edge.to();
                // Skip if vertex is already visited.
                if (!known.contains(to)) {
                    // Calculate new distance through current vertex to neighbor.
                    double oldDist = distTo.get(to);
                    double newDist = edge.weight() + distTo.get(closest);
                    // If new distance is shorter, update it and set current edge as incoming.
                    if (newDist < oldDist) {
                        distTo.put(to, newDist);
                        edgeTo.put(to, edge);
                        // Update priority queue with new distance.
                        closestHeap.changePriority(to, newDist);
                    }
                    // Ensure all neighbors are in the priority queue with correct distances.
                    for (E e : graph.outgoingEdgesFrom(to)) {
                        if (!distTo.containsKey(e.to())) {
                            distTo.put(e.to(), Double.POSITIVE_INFINITY);
                            closestHeap.add(e.to(), Double.POSITIVE_INFINITY);
                        }
                    }
                }
            }
        }
        // Start vertex doesn't precede itself in path; remove it from the map.
        edgeTo.remove(start);
        // Return map of incoming edges representing the shortest paths tree.
        return edgeTo;
    }

    // Constructs the shortest path from start to end using the shortest paths tree (SPT) generated by Dijkstra's
    // algorithm.
    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        // If the shortest paths tree is null, no path exists.
        if (spt == null) {
            return new ShortestPath.Failure<>();
        }
        // If start and end are the same, the path is just the start vertex.
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        // Initialize a list to store the edges in the path from start to end.
        LinkedList<E> result = new LinkedList<>();
        // Start from the end vertex and work backwards.
        V curr = end;
        while (!curr.equals(start)) { // Keep tracing back until reaching the start vertex.
            // If a vertex in the path doesn't have an incoming edge, the path is invalid.
            if (spt.get(curr) == null) {
                return new ShortestPath.Failure<>();
            }
            // Add the incoming edge to the front of the list, constructing the path in reverse.
            result.add(0, spt.get(curr));
            // Move to the vertex at the start of the incoming edge.
            curr = spt.get(curr).from();
        }
        // Return the successfully constructed path.
        return new ShortestPath.Success<>(result);
    }
}