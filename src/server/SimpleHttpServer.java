package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import service.CitationAnalyzer;
import service.PaperManager;
import service.RankingManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * Built-in Embedded Zero-Dependency HTTP Server for CitationFlow.
 * Serves the modern Academic Web Frontend and the JSON REST API.
 */
public class SimpleHttpServer {
    private final int port;
    private final String webRoot;
    private final PaperManager paperManager;
    private final CitationAnalyzer citationAnalyzer;
    private final RankingManager rankingManager;
    private HttpServer server;

    public SimpleHttpServer(int port, String webRoot, PaperManager paperManager,
                            CitationAnalyzer citationAnalyzer, RankingManager rankingManager) {
        this.port = port;
        this.webRoot = webRoot;
        this.paperManager = paperManager;
        this.citationAnalyzer = citationAnalyzer;
        this.rankingManager = rankingManager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoint handler
        ApiHandler apiHandler = new ApiHandler(paperManager, citationAnalyzer, rankingManager);
        server.createContext("/api", apiHandler);

        // Static Web Asset handler
        server.createContext("/", new StaticFileHandler(webRoot));

        // Use multi-threaded executor for concurrent requests
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("==================================================================");
        System.out.println(" CitationFlow Server running at: http://localhost:" + port);
        System.out.println(" Web Dashboard:  http://localhost:" + port + "/index.html");
        System.out.println(" REST API Base:  http://localhost:" + port + "/api/statistics");
        System.out.println("==================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("CitationFlow server stopped.");
        }
    }

    /**
     * Static file handler serving HTML, CSS, JavaScript, JSON, and images.
     */
    private static class StaticFileHandler implements HttpHandler {
        private final String rootDir;

        public StaticFileHandler(String rootDir) {
            this.rootDir = rootDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr.equals("/") || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }

            // Sanitize path to prevent directory traversal
            pathStr = pathStr.replace("..", "").replace("//", "/");
            Path filePath = Paths.get(rootDir, pathStr);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                // Fallback to index.html for SPA routes or return 404
                Path indexPath = Paths.get(rootDir, "index.html");
                if (Files.exists(indexPath)) {
                    filePath = indexPath;
                } else {
                    String notFound = "<h1>404 Not Found</h1><p>The file " + pathStr + " does not exist.</p>";
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(404, notFound.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(notFound.getBytes());
                    }
                    return;
                }
            }

            String contentType = determineContentType(filePath.toString());
            byte[] bytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String determineContentType(String path) {
            String lower = path.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }
}
