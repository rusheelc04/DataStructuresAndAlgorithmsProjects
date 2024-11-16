package graphs.minspantrees;

import disjointsets.DisjointSets;
import disjointsets.UnionBySizeCompressingDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

// Computes minimum spanning trees using Kruskal's algorithm.
// @see MinimumSpanningTreeFinder for more documentation.
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        // return new QuickFindDisjointSets<>();

        // Disable the line above and enable the one below after you've finished implementing
        // your `UnionBySizeCompressingDisjointSets`.

        return new UnionBySizeCompressingDisjointSets<>();

        // Otherwise, do not change this method.
        // We override this during grading to test your code using our correct implementation so that
        // you don't lose extra points if your implementation is buggy.
    }

    // Find the MST of given graph using Kruskal's. Sorts edges of graph by weight, then iteratively adds shortest
    // edge to the MST that doesn't form a cycle, until all vertices are connected or no more edges can be added.
    // returns MST as a collection of edges if the graph is connected, or returns a failure if the graph is not
    // connected.
    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {
        // check if graph is empty
        if (graph.allEdges().isEmpty() && graph.allVertices().isEmpty()) {
            return new MinimumSpanningTree.Success<>(); //  return success
        }

        // Create a list of all edges in the graph and sort them by weight in ascending order.
        List<E> edges = new ArrayList<>(graph.allEdges());
        edges.sort(Comparator.comparingDouble(E::weight));

        // Create disjoint sets to keep track of connected components.
        DisjointSets<V> disjointSets = createDisjointSets();
        // A set to store the edges of the final MST.
        HashSet<E> finalMST = new HashSet<>();

        // create set for each vertex in graph
        for (V vertex : graph.allVertices()) {
            disjointSets.makeSet(vertex);
        }

        // iterate over sorted edges, adding them to the MST if they connect separate trees.
        for (E curr : edges) {
            // Find the sets of the vertices at the ends of the current edge.
            int fromMST = disjointSets.findSet(curr.from());
            int toMST = disjointSets.findSet(curr.to());
            // if ends are in different sets add the edge to the MST and merge sets.
            if (fromMST != toMST) {
                finalMST.add(curr);
                disjointSets.union(curr.from(), curr.to());
            }
        }
        // If the number of edges in the MST equals the number of vertices minus one, return success.
        if (finalMST.size() == graph.allVertices().size() - 1) {
            return new MinimumSpanningTree.Success<>(finalMST);
        }
        // If not all vertices are connected, return failure.
        return new MinimumSpanningTree.Failure<>();
    }
}
