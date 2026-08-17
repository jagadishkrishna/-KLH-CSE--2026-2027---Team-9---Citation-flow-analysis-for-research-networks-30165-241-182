package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the complete result and step-by-step trace of a Graph Traversal (BFS / DFS / Shortest Path).
 * Designed to provide transparent algorithmic execution logs for DSA Viva demonstrations.
 */
public class TraversalResult {
    private String algorithm;            // "BFS", "DFS", "Shortest Path"
    private String startNode;
    private String targetNode;
    private List<String> visitedOrder;    // Order of nodes visited
    private List<String> path;            // Reconstructed path if applicable
    private List<Step> steps;             // Step-by-step trace for UI playback
    private boolean cycleDetected;        // Flag for DFS cycle detection
    private List<String> cyclePath;       // Cycle path if detected
    private String timeComplexity;        // Theoretical Time Complexity
    private String spaceComplexity;       // Theoretical Space Complexity
    private long executionTimeNanos;      // Measured execution time in nanoseconds
    private String explanation;           // Human-readable academic explanation

    public static class Step {
        private int stepNumber;
        private String currentNode;
        private String action;            // "VISIT", "EXPLORE_NEIGHBOR", "ENQUEUE", "DEQUEUE", "PUSH_STACK", "POP_STACK", "BACKTRACK", "CYCLE_FOUND"
        private String neighborNode;
        private List<String> dataStructureState; // Current state of Queue or Stack
        private List<String> visitedSet;
        private String message;

        public Step(int stepNumber, String currentNode, String action, String neighborNode,
                    List<String> dataStructureState, List<String> visitedSet, String message) {
            this.stepNumber = stepNumber;
            this.currentNode = currentNode;
            this.action = action;
            this.neighborNode = neighborNode;
            this.dataStructureState = (dataStructureState != null) ? new ArrayList<>(dataStructureState) : new ArrayList<>();
            this.visitedSet = (visitedSet != null) ? new ArrayList<>(visitedSet) : new ArrayList<>();
            this.message = message;
        }

        public int getStepNumber() { return stepNumber; }
        public String getCurrentNode() { return currentNode; }
        public String getAction() { return action; }
        public String getNeighborNode() { return neighborNode; }
        public List<String> getDataStructureState() { return dataStructureState; }
        public List<String> getVisitedSet() { return visitedSet; }
        public String getMessage() { return message; }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"stepNumber\":").append(stepNumber).append(",");
            sb.append("\"currentNode\":\"").append(currentNode != null ? currentNode : "").append("\",");
            sb.append("\"action\":\"").append(action != null ? action : "").append("\",");
            sb.append("\"neighborNode\":\"").append(neighborNode != null ? neighborNode : "").append("\",");
            sb.append("\"dataStructureState\":").append(toJsonStringArray(dataStructureState)).append(",");
            sb.append("\"visitedSet\":").append(toJsonStringArray(visitedSet)).append(",");
            sb.append("\"message\":\"").append(escapeJson(message)).append("\"");
            sb.append("}");
            return sb.toString();
        }
    }

    public TraversalResult(String algorithm, String startNode) {
        this.algorithm = algorithm;
        this.startNode = startNode;
        this.visitedOrder = new ArrayList<>();
        this.path = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.cycleDetected = false;
        this.cyclePath = new ArrayList<>();
    }

    // Getters and Setters
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getStartNode() { return startNode; }
    public void setStartNode(String startNode) { this.startNode = startNode; }

    public String getTargetNode() { return targetNode; }
    public void setTargetNode(String targetNode) { this.targetNode = targetNode; }

    public List<String> getVisitedOrder() { return visitedOrder; }
    public void setVisitedOrder(List<String> visitedOrder) { this.visitedOrder = visitedOrder; }

    public List<String> getPath() { return path; }
    public void setPath(List<String> path) { this.path = path; }

    public List<Step> getSteps() { return steps; }
    public void addStep(Step step) { this.steps.add(step); }

    public boolean isCycleDetected() { return cycleDetected; }
    public void setCycleDetected(boolean cycleDetected) { this.cycleDetected = cycleDetected; }

    public List<String> getCyclePath() { return cyclePath; }
    public void setCyclePath(List<String> cyclePath) { this.cyclePath = cyclePath; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public long getExecutionTimeNanos() { return executionTimeNanos; }
    public void setExecutionTimeNanos(long executionTimeNanos) { this.executionTimeNanos = executionTimeNanos; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"algorithm\":\"").append(algorithm).append("\",");
        sb.append("\"startNode\":\"").append(startNode != null ? startNode : "").append("\",");
        sb.append("\"targetNode\":\"").append(targetNode != null ? targetNode : "").append("\",");
        sb.append("\"visitedOrder\":").append(toJsonStringArray(visitedOrder)).append(",");
        sb.append("\"path\":").append(toJsonStringArray(path)).append(",");
        sb.append("\"cycleDetected\":").append(cycleDetected).append(",");
        sb.append("\"cyclePath\":").append(toJsonStringArray(cyclePath)).append(",");
        sb.append("\"timeComplexity\":\"").append(escapeJson(timeComplexity)).append("\",");
        sb.append("\"spaceComplexity\":\"").append(escapeJson(spaceComplexity)).append("\",");
        sb.append("\"executionTimeNanos\":").append(executionTimeNanos).append(",");
        sb.append("\"explanation\":\"").append(escapeJson(explanation)).append("\",");
        sb.append("\"steps\":[");
        for (int i = 0; i < steps.size(); i++) {
            sb.append(steps.get(i).toJson());
            if (i < steps.size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String toJsonStringArray(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
