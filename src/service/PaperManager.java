package service;

import dsa.CitationGraph;
import model.Paper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages research papers and maintains synchronized state with the CitationGraph.
 * 
 * Core DSA Mechanisms:
 * - HashMap<String, Paper> (paperLookup): Provides average O(1) time complexity for Paper ID lookups.
 * - Inverted/Substring Search: Linear search across Title, Author, and Topic filters with O(N * M) string matching.
 * - Graph synchronization: Adding/removing papers coordinates directly with CitationGraph vertices and edges.
 */
public class PaperManager {
    private final Map<String, Paper> paperLookup; // HashMap for O(1) retrieval
    private final CitationGraph graph;

    public PaperManager(CitationGraph graph) {
        this.paperLookup = new LinkedHashMap<>();
        this.graph = graph;
    }

    public CitationGraph getGraph() {
        return graph;
    }

    /**
     * Adds a research paper to the database and graph.
     * Prevents duplicate Paper IDs.
     * Time Complexity: O(1) average
     */
    public synchronized boolean addPaper(Paper paper) {
        if (paper == null || paper.getId() == null) {
            throw new IllegalArgumentException("Paper and Paper ID cannot be null.");
        }
        String id = paper.getId().trim().toUpperCase();
        if (paperLookup.containsKey(id)) {
            return false; // Duplicate paper ID
        }

        paperLookup.put(id, paper);
        graph.addPaper(id);
        syncCitationCounts(id);
        return true;
    }

    /**
     * Updates existing paper metadata.
     */
    public synchronized boolean updatePaper(String id, String title, String authors, int year, String topic, String abstractText, String doi) {
        if (id == null) return false;
        Paper paper = paperLookup.get(id.trim().toUpperCase());
        if (paper == null) return false;

        if (title != null && !title.trim().isEmpty()) paper.setTitle(title.trim());
        if (authors != null && !authors.trim().isEmpty()) paper.setAuthors(authors.trim());
        if (year > 1900 && year <= 2030) paper.setYear(year);
        if (topic != null && !topic.trim().isEmpty()) paper.setTopic(topic.trim());
        if (abstractText != null) paper.setAbstractText(abstractText.trim());
        if (doi != null && !doi.trim().isEmpty()) paper.setDoi(doi.trim());

        return true;
    }

    /**
     * Deletes a paper and cleans up all related citation edges.
     * Time Complexity: O(V + E)
     */
    public synchronized boolean deletePaper(String paperId) {
        if (paperId == null) return false;
        String id = paperId.trim().toUpperCase();
        if (!paperLookup.containsKey(id)) {
            return false;
        }

        // Get neighbors before removal to update their in/out counts
        Set<String> outNeighbors = new HashSet<>(graph.getOutNeighbors(id));
        Set<String> inNeighbors = new HashSet<>(graph.getInNeighbors(id));

        // Remove from graph and lookup
        graph.removePaper(id);
        paperLookup.remove(id);

        // Resync neighbor citation counts
        for (String target : outNeighbors) {
            syncCitationCounts(target);
        }
        for (String source : inNeighbors) {
            syncCitationCounts(source);
        }

        return true;
    }

    /**
     * Adds a citation (source cites target) and updates paper citation counts.
     */
    public synchronized boolean addCitation(String sourceId, String targetId) {
        boolean added = graph.addCitation(sourceId, targetId);
        if (added) {
            syncCitationCounts(sourceId);
            syncCitationCounts(targetId);
        }
        return added;
    }

    /**
     * Removes a citation and updates paper citation counts.
     */
    public synchronized boolean removeCitation(String sourceId, String targetId) {
        boolean removed = graph.removeCitation(sourceId, targetId);
        if (removed) {
            syncCitationCounts(sourceId);
            syncCitationCounts(targetId);
        }
        return removed;
    }

    /**
     * Synchronizes the inCitationCount and outCitationCount of a paper directly from graph degrees.
     */
    public void syncCitationCounts(String paperId) {
        if (paperId == null) return;
        String id = paperId.trim().toUpperCase();
        Paper paper = paperLookup.get(id);
        if (paper != null) {
            paper.setInCitationCount(graph.getInDegree(id));
            paper.setOutCitationCount(graph.getOutDegree(id));
        }
    }

    public void syncAllCitationCounts() {
        for (String id : paperLookup.keySet()) {
            syncCitationCounts(id);
        }
    }

    /**
     * O(1) retrieval by Paper ID using HashMap.
     */
    public Paper getPaperById(String paperId) {
        if (paperId == null) return null;
        return paperLookup.get(paperId.trim().toUpperCase());
    }

    /**
     * Returns all papers as an ArrayList.
     */
    public List<Paper> getAllPapers() {
        return new ArrayList<>(paperLookup.values());
    }

    public int getPaperCount() {
        return paperLookup.size();
    }

    /**
     * Searches papers across multiple attributes (ID, Title, Author, Topic) with case-insensitive substring matching.
     */
    public List<Paper> search(String query, String topicFilter) {
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String t = (topicFilter != null && !topicFilter.equalsIgnoreCase("ALL")) ? topicFilter.trim().toLowerCase() : "";

        return paperLookup.values().stream().filter(p -> {
            boolean matchesTopic = t.isEmpty() || p.getTopic().toLowerCase().contains(t);
            if (!matchesTopic) return false;

            if (q.isEmpty()) return true;

            return p.getId().toLowerCase().contains(q) ||
                   p.getTitle().toLowerCase().contains(q) ||
                   p.getAuthors().toLowerCase().contains(q) ||
                   p.getTopic().toLowerCase().contains(q) ||
                   String.valueOf(p.getYear()).contains(q);
        }).collect(Collectors.toList());
    }

    /**
     * Returns all unique topics present in the database.
     */
    public Set<String> getAllTopics() {
        Set<String> topics = new TreeSet<>();
        for (Paper p : paperLookup.values()) {
            topics.add(p.getTopic());
        }
        return topics;
    }

    /**
     * Clears all papers and graph.
     */
    public synchronized void clear() {
        paperLookup.clear();
        graph.clear();
    }
}
