package dsa;

import model.TraversalResult;
import model.TraversalResult.Step;

import java.util.*;

/**
 * Manual implementations of Core Graph Algorithms for Academic Citation Networks:
 * 
 * 1. Breadth-First Search (BFS) - Citation Level Explorer (Queue-based, O(V + E))
 * 2. Depth-First Search (DFS) - Research Influence Tracer (Stack-based, O(V + E))
 * 3. Shortest Citation Path Finder - Minimum-hop path reconstruction (BFS Parent Pointers, O(V + E))
 * 4. Cycle Detection in Directed Graph - 3-color algorithm (DFS Recursion Stack, O(V + E))
 * 5. Reachability Analysis - Subgraph influence bounds
 * 
 * Every algorithm records fine-grained execution steps (data structure states)
 * to provide full transparency during DSA project vivas and UI animations.
 */
public class GraphAlgorithms {

    /**
     * Executes manual Breadth-First Search (BFS) starting from a source paper.
     * 
     * DSA Concepts:
     * - Queue (FIFO) for level-order exploration
     * - Visited Set (Hash table) to prevent re-processing nodes
     * - Citation Levels (Distance tracking from start node)
     * 
     * Time Complexity: O(V + E) where V = papers, E = citation edges
     * Space Complexity: O(V) for Queue and Visited Set
     */
    public static TraversalResult breadthFirstSearch(CitationGraph graph, String startNodeId) {
        long startTime = System.nanoTime();
        if (graph == null || startNodeId == null || !graph.containsPaper(startNodeId)) {
            TraversalResult empty = new TraversalResult("BFS", startNodeId);
            empty.setExplanation("Start paper does not exist in the citation graph.");
            return empty;
        }

        String start = startNodeId.trim().toUpperCase();
        TraversalResult result = new TraversalResult("BFS", start);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");

        // Explicit Queue data structure for FIFO processing
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new LinkedHashSet<>();
        Map<String, Integer> levels = new LinkedHashMap<>();
        int stepCounter = 1;

        // Step 1: Enqueue start node and mark visited
        queue.add(start);
        visited.add(start);
        levels.put(start, 0);

        result.addStep(new Step(
                stepCounter++,
                start,
                "ENQUEUE",
                null,
                new ArrayList<>(queue),
                new ArrayList<>(visited),
                "Initialized BFS: Enqueued root paper " + start + " at Level 0."
        ));

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.getVisitedOrder().add(current);
            int currentLevel = levels.get(current);

            result.addStep(new Step(
                    stepCounter++,
                    current,
                    "DEQUEUE",
                    null,
                    new ArrayList<>(queue),
                    new ArrayList<>(visited),
                    "Dequeued " + current + " (Level " + currentLevel + ") for exploration."
            ));

            // Explore outgoing citations (Paper A -> Paper B: A cites B)
            Set<String> neighbors = graph.getOutNeighbors(current);
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    levels.put(neighbor, currentLevel + 1);
                    queue.add(neighbor);

                    result.addStep(new Step(
                            stepCounter++,
                            current,
                            "ENQUEUE",
                            neighbor,
                            new ArrayList<>(queue),
                            new ArrayList<>(visited),
                            "Found unvisited cited paper " + neighbor + " -> Added to queue at Level " + (currentLevel + 1) + "."
                    ));
                } else {
                    result.addStep(new Step(
                            stepCounter++,
                            current,
                            "SKIP_ALREADY_VISITED",
                            neighbor,
                            new ArrayList<>(queue),
                            new ArrayList<>(visited),
                            "Cited paper " + neighbor + " is already visited -> Skipped."
                    ));
                }
            }
        }

        long duration = System.nanoTime() - startTime;
        result.setExecutionTimeNanos(duration);
        result.setExplanation(String.format(
                "BFS completed in %d steps. Discovered %d reachable papers across citation levels from root %s.",
                stepCounter - 1, visited.size(), start
        ));

        return result;
    }

    /**
     * Executes manual Depth-First Search (DFS) starting from a source paper.
     * 
     * DSA Concepts:
     * - Call Stack / Recursion for deep influence path exploration
     * - Visited Set to avoid reprocessing
     * - Active Recursion Stack to detect citation cycles
     * 
     * Time Complexity: O(V + E)
     * Space Complexity: O(V) for call stack frames and visited tracking
     */
    public static TraversalResult depthFirstSearch(CitationGraph graph, String startNodeId) {
        long startTime = System.nanoTime();
        if (graph == null || startNodeId == null || !graph.containsPaper(startNodeId)) {
            TraversalResult empty = new TraversalResult("DFS", startNodeId);
            empty.setExplanation("Start paper does not exist in the citation graph.");
            return empty;
        }

        String start = startNodeId.trim().toUpperCase();
        TraversalResult result = new TraversalResult("DFS", start);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");

        Set<String> visited = new LinkedHashSet<>();
        Set<String> recursionStack = new LinkedHashSet<>();
        List<String> currentStackList = new ArrayList<>();
        int[] stepCounter = new int[]{1};

        dfsRecursive(graph, start, visited, recursionStack, currentStackList, result, stepCounter);

        long duration = System.nanoTime() - startTime;
        result.setExecutionTimeNanos(duration);
        result.setExplanation(String.format(
                "DFS completed in %d steps. Traced %d influence chain papers from source %s.",
                stepCounter[0] - 1, visited.size(), start
        ));

        return result;
    }

    private static void dfsRecursive(CitationGraph graph, String current, Set<String> visited,
                                     Set<String> recursionStack, List<String> currentStackList,
                                     TraversalResult result, int[] stepCounter) {
        visited.add(current);
        recursionStack.add(current);
        currentStackList.add(current);
        result.getVisitedOrder().add(current);

        result.addStep(new Step(
                stepCounter[0]++,
                current,
                "PUSH_STACK",
                null,
                new ArrayList<>(currentStackList),
                new ArrayList<>(visited),
                "Pushed " + current + " onto DFS Call Stack. Exploring deep citation chain."
        ));

        Set<String> neighbors = graph.getOutNeighbors(current);
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                result.addStep(new Step(
                        stepCounter[0]++,
                        current,
                        "EXPLORE_NEIGHBOR",
                        neighbor,
                        new ArrayList<>(currentStackList),
                        new ArrayList<>(visited),
                        "Stepping deeper into citation: " + current + " -> " + neighbor
                ));
                dfsRecursive(graph, neighbor, visited, recursionStack, currentStackList, result, stepCounter);
            } else if (recursionStack.contains(neighbor)) {
                // Cycle detected in directed graph!
                result.setCycleDetected(true);
                List<String> cycle = new ArrayList<>();
                int idx = currentStackList.indexOf(neighbor);
                if (idx != -1) {
                    for (int i = idx; i < currentStackList.size(); i++) {
                        cycle.add(currentStackList.get(i));
                    }
                    cycle.add(neighbor); // Close cycle
                }
                result.setCyclePath(cycle);

                result.addStep(new Step(
                        stepCounter[0]++,
                        current,
                        "CYCLE_FOUND",
                        neighbor,
                        new ArrayList<>(currentStackList),
                        new ArrayList<>(visited),
                        "Citation Cycle Detected! Back edge found from " + current + " to ancestor " + neighbor + "."
                ));
            } else {
                result.addStep(new Step(
                        stepCounter[0]++,
                        current,
                        "SKIP_ALREADY_VISITED",
                        neighbor,
                        new ArrayList<>(currentStackList),
                        new ArrayList<>(visited),
                        "Paper " + neighbor + " already visited in another branch -> Skipped."
                ));
            }
        }

        // Backtrack
        recursionStack.remove(current);
        currentStackList.remove(currentStackList.size() - 1);

        result.addStep(new Step(
                stepCounter[0]++,
                current,
                "POP_STACK",
                null,
                new ArrayList<>(currentStackList),
                new ArrayList<>(visited),
                "Backtracked: Popped " + current + " from DFS Call Stack."
        ));
    }

    /**
     * Finds the Shortest Citation Path (minimum number of citations / edges) between Source and Target.
     * 
     * Algorithm Choice Justification (DSA Viva):
     * "In an unweighted directed graph, Breadth-First Search (BFS) is guaranteed to find the shortest
     * path in O(V + E) time because it visits vertices in order of increasing distance from the source."
     * 
     * Path Reconstruction:
     * Uses a Predecessor/Parent Map (HashMap<String, String>) to backtrack from target to source.
     */
    public static TraversalResult findShortestCitationPath(CitationGraph graph, String sourceId, String targetId) {
        long startTime = System.nanoTime();
        TraversalResult result = new TraversalResult("Shortest Path (BFS)", sourceId);
        result.setTargetNode(targetId);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");

        if (graph == null || sourceId == null || targetId == null) {
            result.setExplanation("Source and Target cannot be null.");
            return result;
        }

        String src = sourceId.trim().toUpperCase();
        String tgt = targetId.trim().toUpperCase();

        if (!graph.containsPaper(src)) {
            result.setExplanation("Source paper " + src + " does not exist in graph.");
            return result;
        }
        if (!graph.containsPaper(tgt)) {
            result.setExplanation("Target paper " + tgt + " does not exist in graph.");
            return result;
        }

        if (src.equals(tgt)) {
            result.getPath().add(src);
            result.setExplanation("Source and Target are the same paper (" + src + "). Distance = 0.");
            return result;
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        int stepCounter = 1;

        queue.add(src);
        visited.add(src);

        result.addStep(new Step(
                stepCounter++,
                src,
                "ENQUEUE",
                null,
                new ArrayList<>(queue),
                new ArrayList<>(visited),
                "Initialized Path Search: Enqueued source paper " + src
        ));

        boolean found = false;

        while (!queue.isEmpty() && !found) {
            String current = queue.poll();
            result.getVisitedOrder().add(current);

            result.addStep(new Step(
                    stepCounter++,
                    current,
                    "DEQUEUE",
                    null,
                    new ArrayList<>(queue),
                    new ArrayList<>(visited),
                    "Inspecting paper " + current + " for citation path towards " + tgt
            ));

            for (String neighbor : graph.getOutNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);

                    result.addStep(new Step(
                            stepCounter++,
                            current,
                            "ENQUEUE",
                            neighbor,
                            new ArrayList<>(queue),
                            new ArrayList<>(visited),
                            "Linked parent pointer: " + current + " -> " + neighbor
                    ));

                    if (neighbor.equals(tgt)) {
                        found = true;
                        result.addStep(new Step(
                                stepCounter++,
                                neighbor,
                                "TARGET_REACHED",
                                null,
                                new ArrayList<>(queue),
                                new ArrayList<>(visited),
                                "Target paper " + tgt + " reached successfully!"
                        ));
                        break;
                    }
                }
            }
        }

        if (found) {
            // Reconstruct path from target to source
            LinkedList<String> reconstructedPath = new LinkedList<>();
            String curr = tgt;
            while (curr != null) {
                reconstructedPath.addFirst(curr);
                curr = parentMap.get(curr);
            }
            result.setPath(reconstructedPath);
            result.setExplanation(String.format(
                    "Shortest path found: %s with %d citation hops.",
                    String.join(" -> ", reconstructedPath), reconstructedPath.size() - 1
            ));
        } else {
            result.setExplanation("No citation path exists from " + src + " to " + tgt + " in the directed graph.");
        }

        long duration = System.nanoTime() - startTime;
        result.setExecutionTimeNanos(duration);
        return result;
    }

    /**
     * Detects if any citation cycles exist across the entire directed graph.
     * Uses 3-Coloring DFS:
     * 0: WHITE (Unvisited)
     * 1: GRAY (Currently in recursion stack)
     * 2: BLACK (Completely visited)
     */
    public static boolean hasAnyCycle(CitationGraph graph) {
        if (graph == null || graph.getVertexCount() == 0) return false;

        Map<String, Integer> color = new HashMap<>();
        for (String id : graph.getAllPaperIds()) {
            color.put(id, 0); // WHITE
        }

        for (String id : graph.getAllPaperIds()) {
            if (color.get(id) == 0) {
                if (checkCycleDFS(graph, id, color)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkCycleDFS(CitationGraph graph, String u, Map<String, Integer> color) {
        color.put(u, 1); // GRAY (Active in call stack)

        for (String v : graph.getOutNeighbors(u)) {
            if (color.get(v) == 1) {
                return true; // Found back-edge to an ancestor!
            }
            if (color.get(v) == 0 && checkCycleDFS(graph, v, color)) {
                return true;
            }
        }

        color.put(u, 2); // BLACK (Finished)
        return false;
    }

    /**
     * Computes citation levels for all papers reachable from startNode.
     * Returns a Map of PaperID -> Distance (levels).
     */
    public static Map<String, Integer> getCitationLevels(CitationGraph graph, String startNodeId) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        if (graph == null || startNodeId == null || !graph.containsPaper(startNodeId)) {
            return levels;
        }

        Queue<String> queue = new ArrayDeque<>();
        String start = startNodeId.trim().toUpperCase();
        queue.add(start);
        levels.put(start, 0);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            int currLevel = levels.get(curr);

            for (String nbr : graph.getOutNeighbors(curr)) {
                if (!levels.containsKey(nbr)) {
                    levels.put(nbr, currLevel + 1);
                    queue.add(nbr);
                }
            }
        }
        return levels;
    }
}
