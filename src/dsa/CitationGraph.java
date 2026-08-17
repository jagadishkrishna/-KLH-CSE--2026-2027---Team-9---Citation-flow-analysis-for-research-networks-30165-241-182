package dsa;

import java.util.*;

/**
 * Directed Graph representing an Academic Research Citation Network.
 * 
 * Vertices (V): Research Papers identified by unique Paper IDs.
 * Directed Edges (E): A -> B represents "Paper A cites Paper B" (A references B).
 * 
 * Underlying Data Structure:
 * - Adjacency List using HashMap<String, LinkedHashSet<String>>:
 *     - outEdges (Forward Adjacency List): maps Paper ID -> Set of Papers cited by it (References).
 *     - inEdges (Reverse Adjacency List): maps Paper ID -> Set of Papers that cite it (Citations).
 * 
 * Complexity:
 * - Add Paper (Vertex): O(1)
 * - Add Citation (Edge): O(1) average
 * - Delete Paper (Vertex): O(V + E) (removes vertex and cleans all incident edges)
 * - Delete Citation (Edge): O(1) average
 * - Check Citation (Edge existence): O(1) average
 * - Out-Degree / In-Degree calculation: O(1)
 * - Space Complexity: O(V + E)
 */
public class CitationGraph {
    // Forward adjacency list: source -> destination papers (A cites B)
    private final Map<String, Set<String>> outEdges;
    
    // Reverse adjacency list: destination -> source papers (B cited by A)
    private final Map<String, Set<String>> inEdges;

    public CitationGraph() {
        this.outEdges = new HashMap<>();
        this.inEdges = new HashMap<>();
    }

    /**
     * Adds a paper vertex to the graph if it doesn't already exist.
     * Time Complexity: O(1)
     */
    public synchronized boolean addPaper(String paperId) {
        if (paperId == null || paperId.trim().isEmpty()) {
            throw new IllegalArgumentException("Paper ID cannot be null or empty.");
        }
        String id = paperId.trim().toUpperCase();
        if (outEdges.containsKey(id)) {
            return false; // Paper already exists
        }
        outEdges.put(id, new LinkedHashSet<>());
        inEdges.put(id, new LinkedHashSet<>());
        return true;
    }

    /**
     * Checks if a paper vertex exists in the graph.
     * Time Complexity: O(1)
     */
    public boolean containsPaper(String paperId) {
        if (paperId == null) return false;
        return outEdges.containsKey(paperId.trim().toUpperCase());
    }

    /**
     * Adds a directed citation edge from source to target (source cites target).
     * Validates:
     * 1. Both papers must exist.
     * 2. Self-citations (source == target) are prohibited.
     * 3. Duplicate citations are prevented.
     * 
     * Time Complexity: O(1) average
     */
    public synchronized boolean addCitation(String sourceId, String targetId) {
        if (sourceId == null || targetId == null) {
            throw new IllegalArgumentException("Source and Target Paper IDs cannot be null.");
        }
        String src = sourceId.trim().toUpperCase();
        String tgt = targetId.trim().toUpperCase();

        if (src.equals(tgt)) {
            throw new IllegalArgumentException("Self-citation is not permitted (Paper " + src + " cannot cite itself).");
        }
        if (!outEdges.containsKey(src)) {
            throw new NoSuchElementException("Source Paper ID '" + src + "' does not exist in the graph.");
        }
        if (!outEdges.containsKey(tgt)) {
            throw new NoSuchElementException("Target Paper ID '" + tgt + "' does not exist in the graph.");
        }

        // Check if edge already exists
        if (outEdges.get(src).contains(tgt)) {
            return false; // Duplicate citation
        }

        // Add directed edge in both forward and reverse adjacency lists
        outEdges.get(src).add(tgt);
        inEdges.get(tgt).add(src);
        return true;
    }

    /**
     * Removes a directed citation edge (source stops citing target).
     * Time Complexity: O(1) average
     */
    public synchronized boolean removeCitation(String sourceId, String targetId) {
        if (sourceId == null || targetId == null) return false;
        String src = sourceId.trim().toUpperCase();
        String tgt = targetId.trim().toUpperCase();

        if (!outEdges.containsKey(src) || !outEdges.containsKey(tgt)) {
            return false;
        }

        boolean removedOut = outEdges.get(src).remove(tgt);
        boolean removedIn = inEdges.get(tgt).remove(src);
        return removedOut || removedIn;
    }

    /**
     * Checks if a directed citation edge exists from source to target.
     * Time Complexity: O(1)
     */
    public boolean hasCitation(String sourceId, String targetId) {
        if (sourceId == null || targetId == null) return false;
        String src = sourceId.trim().toUpperCase();
        String tgt = targetId.trim().toUpperCase();

        Set<String> targets = outEdges.get(src);
        return targets != null && targets.contains(tgt);
    }

    /**
     * Deletes a paper vertex and removes all incident incoming and outgoing citation edges.
     * Time Complexity: O(V + E)
     */
    public synchronized boolean removePaper(String paperId) {
        if (paperId == null) return false;
        String id = paperId.trim().toUpperCase();

        if (!outEdges.containsKey(id)) {
            return false;
        }

        // 1. Remove all outgoing citations from 'id' to other papers
        Set<String> citedByThis = new HashSet<>(outEdges.get(id));
        for (String target : citedByThis) {
            Set<String> targetInSet = inEdges.get(target);
            if (targetInSet != null) {
                targetInSet.remove(id);
            }
        }

        // 2. Remove all incoming citations from other papers to 'id'
        Set<String> citingThis = new HashSet<>(inEdges.get(id));
        for (String source : citingThis) {
            Set<String> sourceOutSet = outEdges.get(source);
            if (sourceOutSet != null) {
                sourceOutSet.remove(id);
            }
        }

        // 3. Remove vertex from both maps
        outEdges.remove(id);
        inEdges.remove(id);
        return true;
    }

    /**
     * Returns the set of papers cited by the given paper (Outgoing neighbors / References).
     * Time Complexity: O(1) lookup
     */
    public Set<String> getOutNeighbors(String paperId) {
        if (paperId == null) return Collections.emptySet();
        Set<String> neighbors = outEdges.get(paperId.trim().toUpperCase());
        return (neighbors != null) ? Collections.unmodifiableSet(neighbors) : Collections.emptySet();
    }

    /**
     * Returns the set of papers citing the given paper (Incoming neighbors / Citations).
     * Time Complexity: O(1) lookup
     */
    public Set<String> getInNeighbors(String paperId) {
        if (paperId == null) return Collections.emptySet();
        Set<String> neighbors = inEdges.get(paperId.trim().toUpperCase());
        return (neighbors != null) ? Collections.unmodifiableSet(neighbors) : Collections.emptySet();
    }

    /**
     * Returns the in-degree (citation count) of a paper.
     * In-Degree = number of other papers that cite this paper.
     */
    public int getInDegree(String paperId) {
        if (paperId == null) return 0;
        Set<String> set = inEdges.get(paperId.trim().toUpperCase());
        return (set != null) ? set.size() : 0;
    }

    /**
     * Returns the out-degree (reference count) of a paper.
     * Out-Degree = number of papers cited by this paper.
     */
    public int getOutDegree(String paperId) {
        if (paperId == null) return 0;
        Set<String> set = outEdges.get(paperId.trim().toUpperCase());
        return (set != null) ? set.size() : 0;
    }

    /**
     * Returns all paper IDs (vertices) in the graph.
     */
    public Set<String> getAllPaperIds() {
        return Collections.unmodifiableSet(outEdges.keySet());
    }

    /**
     * Returns the total number of paper vertices in the graph.
     * Time Complexity: O(1)
     */
    public int getVertexCount() {
        return outEdges.size();
    }

    /**
     * Returns the total number of directed citation edges in the graph.
     * Time Complexity: O(V)
     */
    public int getEdgeCount() {
        int count = 0;
        for (Set<String> edges : outEdges.values()) {
            count += edges.size();
        }
        return count;
    }

    /**
     * Clears the graph completely.
     */
    public synchronized void clear() {
        outEdges.clear();
        inEdges.clear();
    }

    /**
     * Serializes the graph into a JSON structure formatted for Cytoscape.js visualization.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"nodeCount\":").append(getVertexCount()).append(",");
        sb.append("\"edgeCount\":").append(getEdgeCount()).append(",");
        sb.append("\"nodes\":[");
        int nodeIdx = 0;
        for (String id : outEdges.keySet()) {
            sb.append("{");
            sb.append("\"id\":\"").append(id).append("\",");
            sb.append("\"inDegree\":").append(getInDegree(id)).append(",");
            sb.append("\"outDegree\":").append(getOutDegree(id));
            sb.append("}");
            if (++nodeIdx < outEdges.size()) sb.append(",");
        }
        sb.append("],\"edges\":[");
        int edgeIdx = 0;
        int totalEdges = getEdgeCount();
        for (Map.Entry<String, Set<String>> entry : outEdges.entrySet()) {
            String source = entry.getKey();
            for (String target : entry.getValue()) {
                sb.append("{");
                sb.append("\"id\":\"e_").append(source).append("_").append(target).append("\",");
                sb.append("\"source\":\"").append(source).append("\",");
                sb.append("\"target\":\"").append(target).append("\"");
                sb.append("}");
                if (++edgeIdx < totalEdges) sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
