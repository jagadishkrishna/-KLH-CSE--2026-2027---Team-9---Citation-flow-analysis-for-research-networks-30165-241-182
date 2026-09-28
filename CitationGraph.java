
import java.util.*;

public class CitationGraph {

    // Stores all papers
    private HashMap<String, Paper> papers;

    // Adjacency List
    // Key = Paper
    // Value = Papers it cites
    private HashMap<String, ArrayList<String>> graph;

    public CitationGraph() {

        papers = new HashMap<>();
        graph = new HashMap<>();

    }

    // Add Paper
    public void addPaper(Paper paper) {

        papers.put(paper.getPaperId(), paper);

        graph.putIfAbsent(paper.getPaperId(), new ArrayList<>());

    }

    // Add Citation
    public void addCitation(String sourcePaper, String destinationPaper) {

        if(graph.containsKey(sourcePaper) &&
                graph.containsKey(destinationPaper))
        {

            graph.get(sourcePaper).add(destinationPaper);

        }

    }

    // Display Graph
    public void displayGraph() {

        System.out.println("\n===== Citation Network =====");

        for(String paper : graph.keySet()) {

            System.out.print(paper + " -> ");

            for(String neighbour : graph.get(paper)) {

                System.out.print(neighbour + " ");

            }

            System.out.println();

        }

    }

    // Return the graph
    public HashMap<String, ArrayList<String>> getGraph() {
        return graph;
    }

    // Return all papers
    public HashMap<String, Paper> getPapers() {
        return papers;
    }

    // Search paper by ID
    public void searchPaper(String paperId) {

        if (papers.containsKey(paperId)) {

            System.out.println("\n===== Paper Details =====");

            System.out.println(papers.get(paperId));

        } else {

            System.out.println("Paper Not Found!");

        }

    }

}