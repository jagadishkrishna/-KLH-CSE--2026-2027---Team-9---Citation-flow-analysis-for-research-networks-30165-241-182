package service;

import dsa.CitationGraph;
import model.Paper;

import java.util.*;

/**
 * Computes deep structural and academic citation metrics for the research network.
 * 
 * Calculated Metrics:
 * - Total Papers (V) and Total Citations (E)
 * - Average citations per paper (E / V)
 * - Graph Density: E / (V * (V - 1))
 * - Most Cited Paper (Max In-Degree)
 * - Most Referencing Paper (Max Out-Degree)
 * - Oldest and Newest publication years
 * - h-index calculation for the overall network / topics
 * - Isolated papers (0 in-degree and 0 out-degree)
 * - Field / Topic citation distribution
 * - Yearly publication volume and citation growth
 */
public class CitationAnalyzer {
    private final PaperManager paperManager;
    private final CitationGraph graph;

    public CitationAnalyzer(PaperManager paperManager) {
        this.paperManager = paperManager;
        this.graph = paperManager.getGraph();
    }

    public Map<String, Object> getNetworkStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        List<Paper> papers = paperManager.getAllPapers();
        int totalPapers = papers.size();
        int totalCitations = graph.getEdgeCount();

        stats.put("totalPapers", totalPapers);
        stats.put("totalCitations", totalCitations);

        double avgCitations = totalPapers > 0 ? (double) totalCitations / totalPapers : 0.0;
        stats.put("avgCitations", Math.round(avgCitations * 100.0) / 100.0);

        // Density for directed graph: E / (V * (V - 1))
        double density = (totalPapers > 1) ? (double) totalCitations / (totalPapers * (totalPapers - 1)) : 0.0;
        stats.put("graphDensity", Math.round(density * 10000.0) / 10000.0);

        // Find Most Cited and Most Referencing
        Paper mostCited = null;
        Paper mostReferencing = null;
        Paper oldestPaper = null;
        Paper newestPaper = null;
        int isolatedCount = 0;

        for (Paper p : papers) {
            if (mostCited == null || p.getInCitationCount() > mostCited.getInCitationCount()) {
                mostCited = p;
            }
            if (mostReferencing == null || p.getOutCitationCount() > mostReferencing.getOutCitationCount()) {
                mostReferencing = p;
            }
            if (oldestPaper == null || p.getYear() < oldestPaper.getYear()) {
                oldestPaper = p;
            }
            if (newestPaper == null || p.getYear() > newestPaper.getYear()) {
                newestPaper = p;
            }
            if (p.getInCitationCount() == 0 && p.getOutCitationCount() == 0) {
                isolatedCount++;
            }
        }

        stats.put("mostCitedPaper", mostCited != null ? mostCited : "None");
        stats.put("mostReferencingPaper", mostReferencing != null ? mostReferencing : "None");
        stats.put("oldestPaper", oldestPaper != null ? oldestPaper : "None");
        stats.put("newestPaper", newestPaper != null ? newestPaper : "None");
        stats.put("isolatedPapersCount", isolatedCount);

        // Calculate Network h-index
        // An h-index of h means there are at least h papers with at least h citations each.
        int hIndex = computeHIndex(papers);
        stats.put("networkHIndex", hIndex);

        // Distribution by Topic
        Map<String, Integer> topicCounts = new TreeMap<>();
        Map<String, Integer> topicCitations = new TreeMap<>();
        for (Paper p : papers) {
            topicCounts.put(p.getTopic(), topicCounts.getOrDefault(p.getTopic(), 0) + 1);
            topicCitations.put(p.getTopic(), topicCitations.getOrDefault(p.getTopic(), 0) + p.getInCitationCount());
        }
        stats.put("topicPaperCounts", topicCounts);
        stats.put("topicCitationCounts", topicCitations);
        stats.put("uniqueTopicsCount", topicCounts.size());

        // Distribution by Year
        Map<Integer, Integer> yearCounts = new TreeMap<>();
        for (Paper p : papers) {
            yearCounts.put(p.getYear(), yearCounts.getOrDefault(p.getYear(), 0) + 1);
        }
        stats.put("yearPaperCounts", yearCounts);

        return stats;
    }

    /**
     * Computes the h-index in O(n log n) using citation counts.
     */
    private int computeHIndex(List<Paper> papers) {
        if (papers == null || papers.isEmpty()) return 0;
        int[] citations = papers.stream().mapToInt(Paper::getInCitationCount).toArray();
        Arrays.sort(citations); // Ascending
        int n = citations.length;
        int h = 0;
        for (int i = 0; i < n; i++) {
            int citationCount = citations[i];
            int papersWithAtLeastThisMany = n - i;
            if (citationCount >= papersWithAtLeastThisMany) {
                h = Math.max(h, papersWithAtLeastThisMany);
            }
        }
        return h;
    }

    /**
     * Serializes network statistics into JSON.
     */
    public String toJson() {
        Map<String, Object> stats = getNetworkStatistics();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"totalPapers\":").append(stats.get("totalPapers")).append(",");
        sb.append("\"totalCitations\":").append(stats.get("totalCitations")).append(",");
        sb.append("\"avgCitations\":").append(stats.get("avgCitations")).append(",");
        sb.append("\"graphDensity\":").append(stats.get("graphDensity")).append(",");
        sb.append("\"networkHIndex\":").append(stats.get("networkHIndex")).append(",");
        sb.append("\"isolatedPapersCount\":").append(stats.get("isolatedPapersCount")).append(",");
        sb.append("\"uniqueTopicsCount\":").append(stats.get("uniqueTopicsCount")).append(",");

        Paper mostCited = (Paper) stats.get("mostCitedPaper");
        sb.append("\"mostCitedPaper\":").append(mostCited != null ? mostCited.toJson() : "null").append(",");

        Paper mostRef = (Paper) stats.get("mostReferencingPaper");
        sb.append("\"mostReferencingPaper\":").append(mostRef != null ? mostRef.toJson() : "null").append(",");

        Paper oldest = (Paper) stats.get("oldestPaper");
        sb.append("\"oldestPaper\":").append(oldest != null ? oldest.toJson() : "null").append(",");

        Paper newest = (Paper) stats.get("newestPaper");
        sb.append("\"newestPaper\":").append(newest != null ? newest.toJson() : "null").append(",");

        // Topics
        @SuppressWarnings("unchecked")
        Map<String, Integer> topicCounts = (Map<String, Integer>) stats.get("topicPaperCounts");
        sb.append("\"topicPaperCounts\":{");
        int idx = 0;
        for (Map.Entry<String, Integer> e : topicCounts.entrySet()) {
            sb.append("\"").append(e.getKey().replace("\"", "\\\"")).append("\":").append(e.getValue());
            if (++idx < topicCounts.size()) sb.append(",");
        }
        sb.append("},");

        @SuppressWarnings("unchecked")
        Map<String, Integer> topicCitations = (Map<String, Integer>) stats.get("topicCitationCounts");
        sb.append("\"topicCitationCounts\":{");
        idx = 0;
        for (Map.Entry<String, Integer> e : topicCitations.entrySet()) {
            sb.append("\"").append(e.getKey().replace("\"", "\\\"")).append("\":").append(e.getValue());
            if (++idx < topicCitations.size()) sb.append(",");
        }
        sb.append("},");

        // Years
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> yearCounts = (Map<Integer, Integer>) stats.get("yearPaperCounts");
        sb.append("\"yearPaperCounts\":{");
        idx = 0;
        for (Map.Entry<Integer, Integer> e : yearCounts.entrySet()) {
            sb.append("\"").append(e.getKey()).append("\":").append(e.getValue());
            if (++idx < yearCounts.size()) sb.append(",");
        }
        sb.append("}");

        sb.append("}");
        return sb.toString();
    }
}
