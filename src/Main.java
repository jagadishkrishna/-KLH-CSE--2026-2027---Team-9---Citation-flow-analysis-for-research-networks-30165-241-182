import data.DatasetLoader;
import dsa.CitationGraph;
import dsa.CustomMaxHeap;
import dsa.GraphAlgorithms;
import model.Paper;
import model.TraversalResult;
import server.SimpleHttpServer;
import service.CitationAnalyzer;
import service.PaperManager;
import service.RankingManager;
import test.TestRunner;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application Entry Point for CitationFlow: Citation Flow Analysis for Research Networks.
 * 
 * Features:
 * - Starts the embedded Zero-Dependency HTTP Server on port 8080
 * - Serves the modern Academic Research Web UI
 * - Provides CLI DSA demo mode (--cli) and Automated Test Mode (--test)
 */
public class Main {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   CITATIONFLOW: Citation Flow Analysis for Research Networks");
        System.out.println("   College DSA Academic Project - Directed Graph & Algorithms");
        System.out.println("==================================================================");

        // Initialize Core DSA Subsystems
        CitationGraph graph = new CitationGraph();
        PaperManager paperManager = new PaperManager(graph);
        CitationAnalyzer citationAnalyzer = new CitationAnalyzer(paperManager);
        RankingManager rankingManager = new RankingManager(paperManager);

        // Preload Realistic Academic Dataset
        System.out.println("[*] Loading academic research dataset (55 papers, 8 domains)...");
        DatasetLoader.loadSampleDataset(paperManager);
        System.out.printf("[+] Loaded %d papers and %d directed citation relationships.\n",
                paperManager.getPaperCount(), graph.getEdgeCount());

        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            TestRunner.main(args);
            return;
        }

        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            runCliMenu(paperManager, graph, rankingManager);
            return;
        }

        // Determine web directory
        String webDir = "web";
        if (!new File(webDir).exists()) {
            webDir = "." + File.separator + "web";
        }

        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            SimpleHttpServer server = new SimpleHttpServer(port, webDir, paperManager, citationAnalyzer, rankingManager);
            server.start();
            System.out.println("\n[!] Open your web browser at: http://localhost:" + port);
            System.out.println("[!] Press Ctrl+C in this terminal to stop the server.\n");
        } catch (Exception e) {
            System.err.println("[-] Failed to start HTTP server on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runCliMenu(PaperManager paperManager, CitationGraph graph, RankingManager rankingManager) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- CitationFlow DSA Interactive CLI Menu ---");
                System.out.println("1. List All Papers (HashMap lookup)");
                System.out.println("2. Run Breadth-First Search (BFS Traversal & Levels)");
                System.out.println("3. Run Depth-First Search (DFS Traversal & Cycle Detection)");
                System.out.println("4. Find Shortest Citation Path (BFS Path Reconstruction)");
                System.out.println("5. Top Cited Papers via Binary Max-Heap (Priority Queue)");
                System.out.println("6. Run Automated Test Suite");
                System.out.println("7. Exit CLI");
                System.out.print("Enter choice (1-7): ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.println("\n--- Research Papers ---");
                        for (Paper p : paperManager.getAllPapers()) {
                            System.out.printf("[%s] %-50s (%d) | In-Citations: %d | Out-Refs: %d\n",
                                    p.getId(), p.getTitle(), p.getYear(), p.getInCitationCount(), p.getOutCitationCount());
                        }
                        break;
                    case "2":
                        System.out.print("Enter Start Paper ID (e.g. P108 for Transformer): ");
                        String startBfs = scanner.nextLine().trim().toUpperCase();
                        TraversalResult bfsRes = GraphAlgorithms.breadthFirstSearch(graph, startBfs);
                        System.out.println("BFS Traversal Order: " + bfsRes.getVisitedOrder());
                        System.out.println("Explanation: " + bfsRes.getExplanation());
                        break;
                    case "3":
                        System.out.print("Enter Start Paper ID (e.g. P108): ");
                        String startDfs = scanner.nextLine().trim().toUpperCase();
                        TraversalResult dfsRes = GraphAlgorithms.depthFirstSearch(graph, startDfs);
                        System.out.println("DFS Traversal Order: " + dfsRes.getVisitedOrder());
                        System.out.println("Cycle Detected: " + dfsRes.isCycleDetected());
                        System.out.println("Explanation: " + dfsRes.getExplanation());
                        break;
                    case "4":
                        System.out.print("Enter Source Paper ID (e.g. P108): ");
                        String src = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Enter Target Paper ID (e.g. P106): ");
                        String tgt = scanner.nextLine().trim().toUpperCase();
                        TraversalResult pathRes = GraphAlgorithms.findShortestCitationPath(graph, src, tgt);
                        System.out.println("Shortest Path: " + pathRes.getPath());
                        System.out.println("Explanation: " + pathRes.getExplanation());
                        break;
                    case "5":
                        System.out.print("Enter K (number of top papers to extract, e.g. 5): ");
                        int k = Integer.parseInt(scanner.nextLine().trim());
                        CustomMaxHeap heap = rankingManager.getTopRankedHeap(k);
                        List<Paper> topK = heap.getTopK(k);
                        System.out.println("\n--- Top " + k + " Most Cited Papers (Binary Max-Heap) ---");
                        for (int i = 0; i < topK.size(); i++) {
                            Paper p = topK.get(i);
                            System.out.printf("%d. [%s] %s — %d citations\n", i + 1, p.getId(), p.getTitle(), p.getInCitationCount());
                        }
                        break;
                    case "6":
                        TestRunner.main(new String[]{});
                        break;
                    case "7":
                        System.out.println("Exiting CLI...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}
