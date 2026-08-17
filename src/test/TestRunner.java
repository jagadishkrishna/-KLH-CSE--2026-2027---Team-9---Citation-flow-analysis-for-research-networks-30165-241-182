package test;

import data.DatasetLoader;
import dsa.CitationGraph;
import dsa.CustomMaxHeap;
import dsa.GraphAlgorithms;
import dsa.MergeSort;
import dsa.QuickSort;
import model.Paper;
import model.TraversalResult;
import service.CitationAnalyzer;
import service.PaperManager;

import java.util.*;

/**
 * Automated Test Suite for CitationFlow DSA Academic Project.
 * Validates all core algorithms, data structures, and edge cases.
 */
public class TestRunner {
    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   RUNNING CITATIONFLOW DSA COMPREHENSIVE TEST SUITE");
        System.out.println("==================================================================");

        testPaperModel();
        testGraphOperations();
        testCitationValidation();
        testPaperDeletionCascade();
        testBreadthFirstSearch();
        testDepthFirstSearch();
        testCycleDetection();
        testShortestCitationPath();
        testCustomMaxHeap();
        testMergeSort();
        testQuickSort();
        testPaperManagerSearch();
        testCitationAnalyzerMetrics();
        testEdgeCases();

        System.out.println("==================================================================");
        System.out.printf("   TEST SUMMARY: %d / %d TESTS PASSED (%.1f%%)\n",
                passedTests, totalTests, (double) passedTests / totalTests * 100.0);
        System.out.println("==================================================================");

        if (passedTests != totalTests) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void testPaperModel() {
        System.out.println("\n--- Testing Paper Model ---");
        Paper p = new Paper("T100", "Test Title", "Author A", 2021, "AI", "Abstract", "doi:123");
        assertTrue("Paper ID capitalized", "T100".equals(p.getId()));
        assertTrue("Title match", "Test Title".equals(p.getTitle()));
        assertTrue("Initial in-citation count is 0", p.getInCitationCount() == 0);
        p.incrementInCitations();
        assertTrue("Increment in-citation count to 1", p.getInCitationCount() == 1);
        p.decrementInCitations();
        assertTrue("Decrement in-citation count to 0", p.getInCitationCount() == 0);
        p.decrementInCitations();
        assertTrue("In-citation count cannot drop below 0", p.getInCitationCount() == 0);
    }

    private static void testGraphOperations() {
        System.out.println("\n--- Testing Directed Graph Operations ---");
        CitationGraph graph = new CitationGraph();
        assertTrue("Add Paper A", graph.addPaper("A"));
        assertTrue("Add Paper B", graph.addPaper("B"));
        assertTrue("Add Paper C", graph.addPaper("C"));
        assertTrue("Prevent duplicate Paper A", !graph.addPaper("A"));
        assertTrue("Graph vertex count is 3", graph.getVertexCount() == 3);

        assertTrue("Add citation A -> B", graph.addCitation("A", "B"));
        assertTrue("Add citation A -> C", graph.addCitation("A", "C"));
        assertTrue("Add citation B -> C", graph.addCitation("B", "C"));
        assertTrue("Graph edge count is 3", graph.getEdgeCount() == 3);

        assertTrue("Check citation exists A -> B", graph.hasCitation("A", "B"));
        assertTrue("Check citation does not exist B -> A", !graph.hasCitation("B", "A"));
        assertTrue("A out-degree is 2", graph.getOutDegree("A") == 2);
        assertTrue("C in-degree is 2", graph.getInDegree("C") == 2);
    }

    private static void testCitationValidation() {
        System.out.println("\n--- Testing Citation Validations ---");
        CitationGraph graph = new CitationGraph();
        graph.addPaper("A");
        graph.addPaper("B");

        // Prevent self-citation
        boolean selfCiteBlocked = false;
        try {
            graph.addCitation("A", "A");
        } catch (IllegalArgumentException e) {
            selfCiteBlocked = true;
        }
        assertTrue("Prevent self-citation A -> A", selfCiteBlocked);

        // Prevent citation to nonexistent paper
        boolean nonexistentBlocked = false;
        try {
            graph.addCitation("A", "Z");
        } catch (NoSuchElementException e) {
            nonexistentBlocked = true;
        }
        assertTrue("Prevent citation to nonexistent target", nonexistentBlocked);

        // Prevent duplicate citation
        graph.addCitation("A", "B");
        assertTrue("Prevent duplicate citation edge A -> B", !graph.addCitation("A", "B"));
    }

    private static void testPaperDeletionCascade() {
        System.out.println("\n--- Testing Paper Deletion with Cascade Edge Cleanup ---");
        CitationGraph graph = new CitationGraph();
        graph.addPaper("A");
        graph.addPaper("B");
        graph.addPaper("C");
        graph.addCitation("A", "B");
        graph.addCitation("B", "C");
        graph.addCitation("C", "A");

        assertTrue("Delete Paper B", graph.removePaper("B"));
        assertTrue("Vertex count reduced to 2", graph.getVertexCount() == 2);
        assertTrue("Out edges from A to B removed", !graph.hasCitation("A", "B"));
        assertTrue("In edges to C from B removed", !graph.hasCitation("B", "C"));
        assertTrue("C -> A citation still intact", graph.hasCitation("C", "A"));
        assertTrue("Total edge count is 1", graph.getEdgeCount() == 1);
    }

    private static void testBreadthFirstSearch() {
        System.out.println("\n--- Testing Breadth-First Search (BFS) ---");
        CitationGraph graph = new CitationGraph();
        graph.addPaper("P1");
        graph.addPaper("P2");
        graph.addPaper("P3");
        graph.addPaper("P4");
        graph.addPaper("P5");

        graph.addCitation("P1", "P2");
        graph.addCitation("P1", "P3");
        graph.addCitation("P2", "P4");
        graph.addCitation("P3", "P5");

        TraversalResult result = GraphAlgorithms.breadthFirstSearch(graph, "P1");
        List<String> order = result.getVisitedOrder();

        assertTrue("BFS visits P1 first", order.get(0).equals("P1"));
        assertTrue("BFS visits all 5 reachable papers", order.size() == 5);
        assertTrue("BFS level 1 contains P2 and P3", (order.get(1).equals("P2") && order.get(2).equals("P3")) ||
                                                      (order.get(1).equals("P3") && order.get(2).equals("P2")));

        Map<String, Integer> levels = GraphAlgorithms.getCitationLevels(graph, "P1");
        assertTrue("Level of P1 is 0", levels.get("P1") == 0);
        assertTrue("Level of P2 is 1", levels.get("P2") == 1);
        assertTrue("Level of P4 is 2", levels.get("P4") == 2);
    }

    private static void testDepthFirstSearch() {
        System.out.println("\n--- Testing Depth-First Search (DFS) ---");
        CitationGraph graph = new CitationGraph();
        graph.addPaper("P1");
        graph.addPaper("P2");
        graph.addPaper("P3");
        graph.addCitation("P1", "P2");
        graph.addCitation("P2", "P3");

        TraversalResult result = GraphAlgorithms.depthFirstSearch(graph, "P1");
        List<String> order = result.getVisitedOrder();

        assertTrue("DFS visits P1 -> P2 -> P3 in sequence",
                order.size() == 3 && order.get(0).equals("P1") && order.get(1).equals("P2") && order.get(2).equals("P3"));
        assertTrue("No cycle in DAG", !result.isCycleDetected());
    }

    private static void testCycleDetection() {
        System.out.println("\n--- Testing Directed Graph Cycle Detection ---");
        CitationGraph acyclicGraph = new CitationGraph();
        acyclicGraph.addPaper("A");
        acyclicGraph.addPaper("B");
        acyclicGraph.addCitation("A", "B");
        assertTrue("Acyclic graph has no cycle", !GraphAlgorithms.hasAnyCycle(acyclicGraph));

        CitationGraph cyclicGraph = new CitationGraph();
        cyclicGraph.addPaper("A");
        cyclicGraph.addPaper("B");
        cyclicGraph.addPaper("C");
        cyclicGraph.addCitation("A", "B");
        cyclicGraph.addCitation("B", "C");
        cyclicGraph.addCitation("C", "A"); // Forms A -> B -> C -> A cycle

        assertTrue("Cyclic graph detected by 3-color DFS", GraphAlgorithms.hasAnyCycle(cyclicGraph));
    }

    private static void testShortestCitationPath() {
        System.out.println("\n--- Testing Shortest Citation Path (BFS) ---");
        CitationGraph graph = new CitationGraph();
        graph.addPaper("A");
        graph.addPaper("B");
        graph.addPaper("C");
        graph.addPaper("D");
        graph.addPaper("E");

        // Long path: A -> B -> C -> D
        graph.addCitation("A", "B");
        graph.addCitation("B", "C");
        graph.addCitation("C", "D");
        // Short path: A -> E -> D
        graph.addCitation("A", "E");
        graph.addCitation("E", "D");

        TraversalResult result = GraphAlgorithms.findShortestCitationPath(graph, "A", "D");
        List<String> path = result.getPath();

        assertTrue("Shortest path has 3 nodes (2 hops)", path.size() == 3);
        assertTrue("Shortest path is [A, E, D]", path.get(0).equals("A") && path.get(1).equals("E") && path.get(2).equals("D"));
    }

    private static void testCustomMaxHeap() {
        System.out.println("\n--- Testing Custom Binary Max-Heap ---");
        List<Paper> papers = new ArrayList<>();
        Paper p1 = new Paper("P1", "Paper 1", "Author", 2020, "AI", "", "");
        p1.setInCitationCount(10);
        Paper p2 = new Paper("P2", "Paper 2", "Author", 2020, "AI", "", "");
        p2.setInCitationCount(50);
        Paper p3 = new Paper("P3", "Paper 3", "Author", 2020, "AI", "", "");
        p3.setInCitationCount(30);
        Paper p4 = new Paper("P4", "Paper 4", "Author", 2020, "AI", "", "");
        p4.setInCitationCount(90);

        papers.add(p1);
        papers.add(p2);
        papers.add(p3);
        papers.add(p4);

        CustomMaxHeap heap = new CustomMaxHeap(papers);
        assertTrue("Peek returns highest citation paper (P4 with 90)", heap.peek().getId().equals("P4"));

        Paper max1 = heap.extractMax();
        assertTrue("First ExtractMax is P4 (90)", max1.getId().equals("P4") && max1.getInCitationCount() == 90);
        Paper max2 = heap.extractMax();
        assertTrue("Second ExtractMax is P2 (50)", max2.getId().equals("P2") && max2.getInCitationCount() == 50);
        Paper max3 = heap.extractMax();
        assertTrue("Third ExtractMax is P3 (30)", max3.getId().equals("P3") && max3.getInCitationCount() == 30);
        Paper max4 = heap.extractMax();
        assertTrue("Fourth ExtractMax is P1 (10)", max4.getId().equals("P1") && max4.getInCitationCount() == 10);
        assertTrue("Heap is now empty", heap.isEmpty());
    }

    private static void testMergeSort() {
        System.out.println("\n--- Testing Custom Merge Sort ---");
        List<Paper> papers = new ArrayList<>();
        Paper p1 = new Paper("A", "Alpha", "Auth", 2010, "AI", "", ""); p1.setInCitationCount(15);
        Paper p2 = new Paper("B", "Beta", "Auth", 2020, "AI", "", ""); p2.setInCitationCount(5);
        Paper p3 = new Paper("C", "Gamma", "Auth", 2015, "AI", "", ""); p3.setInCitationCount(25);
        papers.add(p1);
        papers.add(p2);
        papers.add(p3);

        MergeSort.SortResult result = MergeSort.sort(papers, Comparator.comparingInt(Paper::getInCitationCount).reversed());
        List<Paper> sorted = result.getSortedList();

        assertTrue("MergeSort citation desc first is C (25)", sorted.get(0).getId().equals("C"));
        assertTrue("MergeSort citation desc second is A (15)", sorted.get(1).getId().equals("A"));
        assertTrue("MergeSort citation desc third is B (5)", sorted.get(2).getId().equals("B"));
    }

    private static void testQuickSort() {
        System.out.println("\n--- Testing Custom Quick Sort ---");
        List<Paper> papers = new ArrayList<>();
        Paper p1 = new Paper("A", "Zeta", "Auth", 2010, "AI", "", ""); p1.setInCitationCount(15);
        Paper p2 = new Paper("B", "Alpha", "Auth", 2020, "AI", "", ""); p2.setInCitationCount(5);
        Paper p3 = new Paper("C", "Beta", "Auth", 2015, "AI", "", ""); p3.setInCitationCount(25);
        papers.add(p1);
        papers.add(p2);
        papers.add(p3);

        QuickSort.SortResult result = QuickSort.sort(papers, Comparator.comparing(Paper::getTitle));
        List<Paper> sorted = result.getSortedList();

        assertTrue("QuickSort title asc first is Alpha (B)", sorted.get(0).getId().equals("B"));
        assertTrue("QuickSort title asc second is Beta (C)", sorted.get(1).getId().equals("C"));
        assertTrue("QuickSort title asc third is Zeta (A)", sorted.get(2).getId().equals("A"));
    }

    private static void testPaperManagerSearch() {
        System.out.println("\n--- Testing PaperManager Multi-Search & HashMap ---");
        CitationGraph graph = new CitationGraph();
        PaperManager manager = new PaperManager(graph);
        DatasetLoader.loadSampleDataset(manager);

        assertTrue("Dataset loaded 55 papers", manager.getPaperCount() == 55);
        Paper resNet = manager.getPaperById("P101");
        assertTrue("O(1) HashMap lookup for P101 (ResNet)", resNet != null && resNet.getTitle().contains("Residual"));

        List<Paper> searchResults = manager.search("Transformer", "ALL");
        assertTrue("Search 'Transformer' returns multiple matches", searchResults.size() >= 5);

        List<Paper> topicResults = manager.search("", "Blockchain");
        assertTrue("Topic filter 'Blockchain' returns papers", topicResults.size() >= 6);
    }

    private static void testCitationAnalyzerMetrics() {
        System.out.println("\n--- Testing Citation Analyzer Graph Metrics ---");
        CitationGraph graph = new CitationGraph();
        PaperManager manager = new PaperManager(graph);
        DatasetLoader.loadSampleDataset(manager);
        CitationAnalyzer analyzer = new CitationAnalyzer(manager);

        Map<String, Object> stats = analyzer.getNetworkStatistics();
        int totalPapers = (int) stats.get("totalPapers");
        int totalCitations = (int) stats.get("totalCitations");
        double avgCitations = (double) stats.get("avgCitations");
        int hIndex = (int) stats.get("networkHIndex");

        assertTrue("Total papers is 55", totalPapers == 55);
        assertTrue("Total citations > 50", totalCitations > 50);
        assertTrue("Average citations calculated correctly", avgCitations > 0);
        assertTrue("H-index is calculated and > 0", hIndex > 0);
    }

    private static void testEdgeCases() {
        System.out.println("\n--- Testing Graph & Algorithm Edge Cases ---");
        CitationGraph emptyGraph = new CitationGraph();
        TraversalResult bfsEmpty = GraphAlgorithms.breadthFirstSearch(emptyGraph, "NONE");
        assertTrue("BFS on empty graph handles gracefully", bfsEmpty.getVisitedOrder().isEmpty());

        TraversalResult pathEmpty = GraphAlgorithms.findShortestCitationPath(emptyGraph, "A", "B");
        assertTrue("Shortest path on empty graph handles gracefully", pathEmpty.getPath().isEmpty());

        CustomMaxHeap emptyHeap = new CustomMaxHeap();
        assertTrue("ExtractMax on empty heap returns null", emptyHeap.extractMax() == null);
        assertTrue("Peek on empty heap returns null", emptyHeap.peek() == null);
    }
}
