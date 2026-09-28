import java.util.*;

public class GraphAlgorithms {

    private CitationGraph citationGraph;

    public GraphAlgorithms(CitationGraph citationGraph) {
        this.citationGraph = citationGraph;
    }

    // =========================
    // Breadth First Search
    // =========================
    public void BFS(String startPaper) {

        HashMap<String, ArrayList<String>> graph = citationGraph.getGraph();

        if (!graph.containsKey(startPaper)) {
            System.out.println("Paper not found!");
            return;
        }

        Queue<String> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();

        queue.add(startPaper);
        visited.add(startPaper);

        System.out.println("\nBFS Traversal:");

        while (!queue.isEmpty()) {

            String current = queue.poll();

            System.out.println(current);

            for (String neighbour : graph.get(current)) {

                if (!visited.contains(neighbour)) {

                    visited.add(neighbour);

                    queue.add(neighbour);

                }

            }

        }

    }

    // =========================
    // Depth First Search
    // =========================
    public void DFS(String startPaper) {

        HashMap<String, ArrayList<String>> graph = citationGraph.getGraph();

        HashSet<String> visited = new HashSet<>();

        System.out.println("\nDFS Traversal:");

        dfsHelper(startPaper, visited, graph);

    }

    private void dfsHelper(String current,
                           HashSet<String> visited,
                           HashMap<String, ArrayList<String>> graph) {

        if (visited.contains(current))
            return;

        visited.add(current);

        System.out.println(current);

        for (String neighbour : graph.get(current)) {

            dfsHelper(neighbour, visited, graph);

        }

    }

}