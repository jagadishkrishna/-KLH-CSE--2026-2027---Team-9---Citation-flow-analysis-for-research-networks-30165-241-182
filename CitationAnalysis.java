import java.util.*;

public class CitationAnalysis {

    private CitationGraph citationGraph;

    public CitationAnalysis(CitationGraph citationGraph) {

        this.citationGraph = citationGraph;

    }

    // Calculate In-Degree
    public HashMap<String, Integer> calculateCitationCount() {

        HashMap<String, ArrayList<String>> graph =
                citationGraph.getGraph();

        HashMap<String, Integer> indegree =
                new HashMap<>();

        // Initialize all papers with 0 citations
        for(String paper : graph.keySet()) {

            indegree.put(paper,0);

        }

        // Count incoming edges
        for(String paper : graph.keySet()) {

            for(String neighbour : graph.get(paper)) {

                indegree.put(neighbour,
                        indegree.get(neighbour)+1);

            }

        }

        return indegree;

    }

    public void displayCitationCount() {

        HashMap<String,Integer> indegree =
                calculateCitationCount();

        System.out.println("\n===== Citation Count =====");

        for(String paper : indegree.keySet()) {

            System.out.println(
                    paper +
                            " -> " +
                            indegree.get(paper));

        }

    }

}