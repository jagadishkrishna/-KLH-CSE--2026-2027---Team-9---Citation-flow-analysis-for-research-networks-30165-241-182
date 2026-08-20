package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dsa.CustomMaxHeap;
import dsa.GraphAlgorithms;
import dsa.MergeSort;
import dsa.QuickSort;
import data.DatasetLoader;
import model.Paper;
import model.TraversalResult;
import service.CitationAnalyzer;
import service.PaperManager;
import service.RankingManager;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST API Request Router and Controller for CitationFlow.
 */
public class ApiHandler implements HttpHandler {
    private final PaperManager paperManager;
    private final CitationAnalyzer citationAnalyzer;
    private final RankingManager rankingManager;

    public ApiHandler(PaperManager paperManager, CitationAnalyzer citationAnalyzer, RankingManager rankingManager) {
        this.paperManager = paperManager;
        this.citationAnalyzer = citationAnalyzer;
        this.rankingManager = rankingManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handle CORS Pre-flight
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();
        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

        try {
            if (path.equals("/api/statistics") && method.equals("GET")) {
                handleGetStatistics(exchange);
            } else if (path.equals("/api/graph") && method.equals("GET")) {
                handleGetGraph(exchange);
            } else if (path.equals("/api/papers") && method.equals("GET")) {
                handleGetPapers(exchange, queryParams);
            } else if (path.startsWith("/api/papers/") && method.equals("GET")) {
                String id = path.substring("/api/papers/".length());
                handleGetSinglePaper(exchange, id);
            } else if (path.equals("/api/papers") && method.equals("POST")) {
                handleCreatePaper(exchange);
            } else if (path.startsWith("/api/papers/") && method.equals("PUT")) {
                String id = path.substring("/api/papers/".length());
                handleUpdatePaper(exchange, id);
            } else if (path.startsWith("/api/papers/") && method.equals("DELETE")) {
                String id = path.substring("/api/papers/".length());
                handleDeletePaper(exchange, id);
            } else if (path.equals("/api/citations") && method.equals("POST")) {
                handleAddCitation(exchange);
            } else if (path.equals("/api/citations") && method.equals("DELETE")) {
                handleDeleteCitation(exchange);
            } else if (path.equals("/api/bfs") && method.equals("GET")) {
                handleBFS(exchange, queryParams);
            } else if (path.equals("/api/dfs") && method.equals("GET")) {
                handleDFS(exchange, queryParams);
            } else if (path.equals("/api/path") && method.equals("GET")) {
                handleShortestPath(exchange, queryParams);
            } else if (path.equals("/api/rankings") && method.equals("GET")) {
                handleGetRankings(exchange, queryParams);
            } else if (path.equals("/api/sort") && method.equals("GET")) {
                handleSortPapers(exchange, queryParams);
            } else if (path.equals("/api/dsa-guide") && method.equals("GET")) {
                handleGetDsaGuide(exchange);
            } else if (path.equals("/api/reset") && method.equals("POST")) {
                handleResetDataset(exchange);
            } else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Endpoint not found: " + path + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetStatistics(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, citationAnalyzer.toJson());
    }

    private void handleGetGraph(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, paperManager.getGraph().toJson());
    }

    private void handleGetPapers(HttpExchange exchange, Map<String, String> params) throws IOException {
        String query = params.getOrDefault("query", "");
        String topic = params.getOrDefault("topic", "");
        String sortBy = params.getOrDefault("sort", "citations");
        String order = params.getOrDefault("order", "desc");
        String algo = params.getOrDefault("algo", "mergesort");

        List<Paper> filtered = paperManager.search(query, topic);

        // Sort results using custom MergeSort or QuickSort
        Comparator<Paper> comparator;
        if ("year".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingInt(Paper::getYear);
        } else if ("title".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Paper::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("id".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Paper::getId);
        } else {
            comparator = Comparator.comparingInt(Paper::getInCitationCount);
        }

        if (!"asc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        long comparisons;
        long durationNanos;
        String algorithmName;

        if ("quicksort".equalsIgnoreCase(algo)) {
            QuickSort.SortResult res = QuickSort.sort(filtered, comparator);
            filtered = res.getSortedList();
            comparisons = res.getComparisons();
            durationNanos = res.getExecutionTimeNanos();
            algorithmName = "Quick Sort";
        } else {
            MergeSort.SortResult res = MergeSort.sort(filtered, comparator);
            filtered = res.getSortedList();
            comparisons = res.getComparisons();
            durationNanos = res.getExecutionTimeNanos();
            algorithmName = "Merge Sort";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"count\":").append(filtered.size()).append(",");
        sb.append("\"algorithm\":\"").append(algorithmName).append("\",");
        sb.append("\"comparisons\":").append(comparisons).append(",");
        sb.append("\"executionTimeNanos\":").append(durationNanos).append(",");
        sb.append("\"papers\":[");
        for (int i = 0; i < filtered.size(); i++) {
            sb.append(filtered.get(i).toJson());
            if (i < filtered.size() - 1) sb.append(",");
        }
        sb.append("]}");

        sendJsonResponse(exchange, 200, sb.toString());
    }

    private void handleGetSinglePaper(HttpExchange exchange, String id) throws IOException {
        Paper paper = paperManager.getPaperById(id);
        if (paper == null) {
            sendJsonResponse(exchange, 404, "{\"error\":\"Paper not found: " + id + "\"}");
            return;
        }

        Set<String> outNeighbors = paperManager.getGraph().getOutNeighbors(id);
        Set<String> inNeighbors = paperManager.getGraph().getInNeighbors(id);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"paper\":").append(paper.toJson()).append(",");
        sb.append("\"references\":[");
        int idx = 0;
        for (String refId : outNeighbors) {
            Paper p = paperManager.getPaperById(refId);
            sb.append(p != null ? p.toJson() : "{\"id\":\"" + refId + "\"}");
            if (++idx < outNeighbors.size()) sb.append(",");
        }
        sb.append("],\"citedBy\":[");
        idx = 0;
        for (String citeId : inNeighbors) {
            Paper p = paperManager.getPaperById(citeId);
            sb.append(p != null ? p.toJson() : "{\"id\":\"" + citeId + "\"}");
            if (++idx < inNeighbors.size()) sb.append(",");
        }
        sb.append("]}");

        sendJsonResponse(exchange, 200, sb.toString());
    }

    private void handleCreatePaper(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Map<String, String> json = parseSimpleJson(body);

        String id = json.get("id");
        String title = json.get("title");
        String authors = json.get("authors");
        int year = parseInt(json.get("year"), 2024);
        String topic = json.get("topic");
        String abstractText = json.get("abstractText");
        String doi = json.get("doi");

        if (id == null || id.trim().isEmpty() || title == null || title.trim().isEmpty()) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Paper ID and Title are required.\"}");
            return;
        }

        Paper paper = new Paper(id, title, authors, year, topic, abstractText, doi);
        boolean added = paperManager.addPaper(paper);

        if (!added) {
            sendJsonResponse(exchange, 409, "{\"error\":\"Paper with ID '" + id + "' already exists.\"}");
            return;
        }

        sendJsonResponse(exchange, 201, "{\"message\":\"Paper created successfully\",\"paper\":" + paper.toJson() + "}");
    }

    private void handleUpdatePaper(HttpExchange exchange, String id) throws IOException {
        String body = readRequestBody(exchange);
        Map<String, String> json = parseSimpleJson(body);

        String title = json.get("title");
        String authors = json.get("authors");
        int year = parseInt(json.get("year"), -1);
        String topic = json.get("topic");
        String abstractText = json.get("abstractText");
        String doi = json.get("doi");

        boolean updated = paperManager.updatePaper(id, title, authors, year, topic, abstractText, doi);
        if (!updated) {
            sendJsonResponse(exchange, 404, "{\"error\":\"Paper with ID '" + id + "' not found.\"}");
            return;
        }

        Paper paper = paperManager.getPaperById(id);
        sendJsonResponse(exchange, 200, "{\"message\":\"Paper updated successfully\",\"paper\":" + paper.toJson() + "}");
    }

    private void handleDeletePaper(HttpExchange exchange, String id) throws IOException {
        boolean deleted = paperManager.deletePaper(id);
        if (!deleted) {
            sendJsonResponse(exchange, 404, "{\"error\":\"Paper with ID '" + id + "' not found.\"}");
            return;
        }
        sendJsonResponse(exchange, 200, "{\"message\":\"Paper deleted and incident graph edges cleaned up.\"}");
    }

    private void handleAddCitation(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Map<String, String> json = parseSimpleJson(body);

        String source = json.get("source");
        String target = json.get("target");

        if (source == null || target == null || source.trim().isEmpty() || target.trim().isEmpty()) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Source and Target Paper IDs are required.\"}");
            return;
        }

        try {
            boolean added = paperManager.addCitation(source, target);
            if (!added) {
                sendJsonResponse(exchange, 409, "{\"error\":\"Citation edge already exists from " + source + " to " + target + ".\"}");
                return;
            }
            sendJsonResponse(exchange, 201, "{\"message\":\"Citation edge added: " + source + " -> " + target + "\"}");
        } catch (IllegalArgumentException e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (NoSuchElementException e) {
            sendJsonResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteCitation(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Map<String, String> json = parseSimpleJson(body);

        String source = json.get("source");
        String target = json.get("target");

        boolean removed = paperManager.removeCitation(source, target);
        if (!removed) {
            sendJsonResponse(exchange, 404, "{\"error\":\"Citation edge does not exist between " + source + " and " + target + ".\"}");
            return;
        }
        sendJsonResponse(exchange, 200, "{\"message\":\"Citation edge removed successfully.\"}");
    }

    private void handleBFS(HttpExchange exchange, Map<String, String> params) throws IOException {
        String start = params.getOrDefault("start", "P101");
        TraversalResult res = GraphAlgorithms.breadthFirstSearch(paperManager.getGraph(), start);
        sendJsonResponse(exchange, 200, res.toJson());
    }

    private void handleDFS(HttpExchange exchange, Map<String, String> params) throws IOException {
        String start = params.getOrDefault("start", "P101");
        TraversalResult res = GraphAlgorithms.depthFirstSearch(paperManager.getGraph(), start);
        sendJsonResponse(exchange, 200, res.toJson());
    }

    private void handleShortestPath(HttpExchange exchange, Map<String, String> params) throws IOException {
        String source = params.getOrDefault("source", "P108");
        String target = params.getOrDefault("target", "P106");
        TraversalResult res = GraphAlgorithms.findShortestCitationPath(paperManager.getGraph(), source, target);
        sendJsonResponse(exchange, 200, res.toJson());
    }

    private void handleGetRankings(HttpExchange exchange, Map<String, String> params) throws IOException {
        int limit = parseInt(params.get("limit"), 10);
        CustomMaxHeap heap = rankingManager.getTopRankedHeap(limit);
        List<Paper> topPapers = heap.getTopK(limit);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"limit\":").append(limit).append(",");
        sb.append("\"heapStructure\":").append(heap.toJson()).append(",");
        sb.append("\"topPapers\":[");
        for (int i = 0; i < topPapers.size(); i++) {
            sb.append(topPapers.get(i).toJson());
            if (i < topPapers.size() - 1) sb.append(",");
        }
        sb.append("]}");

        sendJsonResponse(exchange, 200, sb.toString());
    }

    private void handleSortPapers(HttpExchange exchange, Map<String, String> params) throws IOException {
        String sortBy = params.getOrDefault("by", "citations");
        String order = params.getOrDefault("order", "desc");
        String algo = params.getOrDefault("algo", "mergesort");

        Object result = rankingManager.sortPapers(sortBy, order, algo);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (result instanceof MergeSort.SortResult) {
            MergeSort.SortResult ms = (MergeSort.SortResult) result;
            sb.append("\"algorithm\":\"").append(ms.getAlgorithm()).append("\",");
            sb.append("\"timeComplexity\":\"").append(ms.getTimeComplexity()).append("\",");
            sb.append("\"spaceComplexity\":\"").append(ms.getSpaceComplexity()).append("\",");
            sb.append("\"comparisons\":").append(ms.getComparisons()).append(",");
            sb.append("\"executionTimeNanos\":").append(ms.getExecutionTimeNanos()).append(",");
            sb.append("\"sortedPapers\":[");
            for (int i = 0; i < ms.getSortedList().size(); i++) {
                sb.append(ms.getSortedList().get(i).toJson());
                if (i < ms.getSortedList().size() - 1) sb.append(",");
            }
            sb.append("]");
        } else if (result instanceof QuickSort.SortResult) {
            QuickSort.SortResult qs = (QuickSort.SortResult) result;
            sb.append("\"algorithm\":\"").append(qs.getAlgorithm()).append("\",");
            sb.append("\"timeComplexity\":\"").append(qs.getTimeComplexity()).append("\",");
            sb.append("\"spaceComplexity\":\"").append(qs.getSpaceComplexity()).append("\",");
            sb.append("\"comparisons\":").append(qs.getComparisons()).append(",");
            sb.append("\"swaps\":").append(qs.getSwaps()).append(",");
            sb.append("\"executionTimeNanos\":").append(qs.getExecutionTimeNanos()).append(",");
            sb.append("\"sortedPapers\":[");
            for (int i = 0; i < qs.getSortedList().size(); i++) {
                sb.append(qs.getSortedList().get(i).toJson());
                if (i < qs.getSortedList().size() - 1) sb.append(",");
            }
            sb.append("]");
        }
        sb.append("}");

        sendJsonResponse(exchange, 200, sb.toString());
    }

    private void handleGetDsaGuide(HttpExchange exchange) throws IOException {
        String json = "{" +
                "\"structures\":[" +
                "{\"name\":\"Directed Graph\",\"purpose\":\"Model asymmetric paper citation relationships (Paper A cites Paper B)\",\"implementation\":\"Adjacency List via HashMap<String, Set<String>>\",\"time\":\"Add: O(1), Edge: O(1)\",\"space\":\"O(V + E)\"}," +
                "{\"name\":\"HashMap\",\"purpose\":\"O(1) average ID-based Paper lookup and visited vertex tracking\",\"implementation\":\"Java standard HashMap\",\"time\":\"Average O(1), Worst O(n)\",\"space\":\"O(V)\"}," +
                "{\"name\":\"Queue (FIFO)\",\"purpose\":\"Breadth-First Search (BFS) level-order citation exploration\",\"implementation\":\"ArrayDeque\",\"time\":\"Enqueue/Dequeue: O(1)\",\"space\":\"O(V)\"}," +
                "{\"name\":\"Stack / Recursion\",\"purpose\":\"Depth-First Search (DFS) citation chain & cycle detection\",\"implementation\":\"Call Stack / Explicit Stack\",\"time\":\"Push/Pop: O(1)\",\"space\":\"O(V)\"}," +
                "{\"name\":\"Binary Max-Heap\",\"purpose\":\"Priority Queue for extracting Top-K most cited research papers\",\"implementation\":\"Custom ArrayList-based Binary Max-Heap\",\"time\":\"Build: O(n), Extract: O(log n), Top-K: O(k log n)\",\"space\":\"O(n)\"}," +
                "{\"name\":\"Merge Sort\",\"purpose\":\"Stable divide-and-conquer sorting for research papers\",\"implementation\":\"Custom recursive merge sort\",\"time\":\"O(n log n) all cases\",\"space\":\"O(n)\"}," +
                "{\"name\":\"Quick Sort\",\"purpose\":\"In-place divide-and-conquer partition sorting for research papers\",\"implementation\":\"Custom recursive Lomuto partitioning\",\"time\":\"O(n log n) avg, O(n^2) worst\",\"space\":\"O(log n)\"}" +
                "]," +
                "\"vivaScenarios\":[" +
                "{\"problem\":\"Shortest Citation Path\",\"dataStructure\":\"Directed Graph + Queue + Predecessor Map\",\"algorithm\":\"Breadth-First Search (BFS)\",\"output\":\"Minimum-hop reference sequence\",\"complexity\":\"O(V + E) time, O(V) space\"}," +
                "{\"problem\":\"Citation Level Discovery\",\"dataStructure\":\"Directed Graph + Queue + Level Map\",\"algorithm\":\"Level-Order BFS Traversal\",\"output\":\"Papers grouped by direct/indirect citation distance\",\"complexity\":\"O(V + E) time, O(V) space\"}," +
                "{\"problem\":\"Research Influence Chain\",\"dataStructure\":\"Directed Graph + Call Stack\",\"algorithm\":\"Depth-First Search (DFS)\",\"output\":\"Deep exploration path of research lineage\",\"complexity\":\"O(V + E) time, O(V) space\"}," +
                "{\"problem\":\"Citation Cycle / Circular Reference\",\"dataStructure\":\"Directed Graph + Recursion State Map (3-Color)\",\"algorithm\":\"DFS with Back-Edge Detection\",\"output\":\"Boolean cycle flag and cycle node path\",\"complexity\":\"O(V + E) time, O(V) space\"}," +
                "{\"problem\":\"Top-K Influential Papers\",\"dataStructure\":\"Binary Max-Heap (Priority Queue)\",\"algorithm\":\"Heapify Down / Extract Max\",\"output\":\"Ranked list of highest cited papers\",\"complexity\":\"O(n + k log n) time, O(n) space\"}," +
                "{\"problem\":\"Paper Ranking & Filtering\",\"dataStructure\":\"Array / Dynamic List\",\"algorithm\":\"Custom Merge Sort / Quick Sort\",\"output\":\"Sorted list by citations, year, or title\",\"complexity\":\"O(n log n) time\"}" +
                "]" +
                "}";

        sendJsonResponse(exchange, 200, json);
    }

    private void handleResetDataset(HttpExchange exchange) throws IOException {
        DatasetLoader.loadSampleDataset(paperManager);
        sendJsonResponse(exchange, 200, "{\"message\":\"Sample dataset reloaded with 55 papers and 100+ citation edges.\"}");
    }

    // Helper methods
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String val = (kv.length > 1) ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(key, val);
        }
        return map;
    }

    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);

        String[] pairs = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String k = kv[0].trim().replace("\"", "");
                String v = kv[1].trim();
                if (v.startsWith("\"") && v.endsWith("\"")) {
                    v = v.substring(1, v.length() - 1);
                }
                map.put(k, v);
            }
        }
        return map;
    }

    private int parseInt(String val, int defaultVal) {
        if (val == null) return defaultVal;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
