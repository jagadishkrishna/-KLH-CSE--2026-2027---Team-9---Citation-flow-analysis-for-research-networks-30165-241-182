package dsa;

import model.Paper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom Binary Max-Heap (Priority Queue) Implementation for Academic Paper Ranking.
 * 
 * Orders research papers such that the paper with the HIGHEST citation count (inCitationCount)
 * is always at the root (index 0).
 * 
 * Heap Invariant:
 * For every node at index i:
 *   - Parent: (i - 1) / 2
 *   - Left Child: 2 * i + 1
 *   - Right Child: 2 * i + 2
 *   - Condition: heap[parent].inCitationCount >= heap[child].inCitationCount
 * 
 * Time Complexities:
 * - Insert: O(log n)
 * - Extract-Max: O(log n)
 * - Peek (Get Max): O(1)
 * - Build Heap (Bottom-up): O(n)
 * - Top-K Extraction: O(k log n)
 * 
 * Space Complexity: O(n)
 */
public class CustomMaxHeap {
    private final List<Paper> heap;
    private final List<String> operationLogs;

    public CustomMaxHeap() {
        this.heap = new ArrayList<>();
        this.operationLogs = new ArrayList<>();
    }

    public CustomMaxHeap(List<Paper> papers) {
        this.heap = new ArrayList<>();
        this.operationLogs = new ArrayList<>();
        buildHeap(papers);
    }

    /**
     * Builds a max-heap in O(n) time using bottom-up heapification.
     */
    public void buildHeap(List<Paper> papers) {
        heap.clear();
        operationLogs.clear();
        if (papers == null || papers.isEmpty()) return;

        heap.addAll(papers);
        operationLogs.add("Loaded " + papers.size() + " papers into raw heap array.");

        // Start from last non-leaf node: (n/2 - 1) down to 0
        int startIndex = (heap.size() / 2) - 1;
        for (int i = startIndex; i >= 0; i--) {
            heapifyDown(i);
        }
        operationLogs.add("Bottom-up O(n) Build-Heap completed successfully.");
    }

    /**
     * Inserts a paper into the Max-Heap.
     * Time Complexity: O(log n)
     */
    public void insert(Paper paper) {
        if (paper == null) return;
        heap.add(paper);
        int currentIndex = heap.size() - 1;
        operationLogs.add(String.format("Inserted [%s (%d cites)] at index %d -> Starting heapifyUp.",
                paper.getId(), paper.getInCitationCount(), currentIndex));
        heapifyUp(currentIndex);
    }

    /**
     * Returns the paper with the maximum citations without removing it.
     * Time Complexity: O(1)
     */
    public Paper peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    /**
     * Extracts and returns the most cited paper from the root.
     * Replaces root with last element and restores heap invariant via heapifyDown.
     * Time Complexity: O(log n)
     */
    public Paper extractMax() {
        if (heap.isEmpty()) return null;

        Paper maxPaper = heap.get(0);
        Paper lastPaper = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, lastPaper);
            operationLogs.add(String.format("ExtractMax: Removed root [%s]. Moved last element [%s] to root -> Starting heapifyDown.",
                    maxPaper.getId(), lastPaper.getId()));
            heapifyDown(0);
        } else {
            operationLogs.add(String.format("ExtractMax: Extracted last remaining element [%s]. Heap is now empty.", maxPaper.getId()));
        }

        return maxPaper;
    }

    /**
     * Restores Max-Heap property upwards from a given index.
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            Paper current = heap.get(index);
            Paper parent = heap.get(parentIndex);

            if (current.getInCitationCount() > parent.getInCitationCount()) {
                // Swap current and parent
                swap(index, parentIndex);
                operationLogs.add(String.format("  Swap (HeapifyUp): [%s (%d)] swapped with parent [%s (%d)] at index %d.",
                        current.getId(), current.getInCitationCount(), parent.getId(), parent.getInCitationCount(), parentIndex));
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Restores Max-Heap property downwards from a given index.
     */
    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int largest = index;

            if (leftChild < size && heap.get(leftChild).getInCitationCount() > heap.get(largest).getInCitationCount()) {
                largest = leftChild;
            }

            if (rightChild < size && heap.get(rightChild).getInCitationCount() > heap.get(largest).getInCitationCount()) {
                largest = rightChild;
            }

            if (largest != index) {
                Paper curr = heap.get(index);
                Paper maxChild = heap.get(largest);
                swap(index, largest);
                operationLogs.add(String.format("  Swap (HeapifyDown): [%s (%d)] swapped down with child [%s (%d)] at index %d.",
                        curr.getId(), curr.getInCitationCount(), maxChild.getId(), maxChild.getInCitationCount(), largest));
                index = largest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Paper temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    /**
     * Extracts top-K most cited papers using Max-Heap.
     * Time Complexity: O(k log n)
     */
    public List<Paper> getTopK(int k) {
        // Create a copy heap to avoid mutating the original
        CustomMaxHeap tempHeap = new CustomMaxHeap(new ArrayList<>(this.heap));
        List<Paper> topList = new ArrayList<>();
        int count = Math.min(k, tempHeap.size());
        for (int i = 0; i < count; i++) {
            Paper p = tempHeap.extractMax();
            if (p != null) {
                topList.add(p);
            }
        }
        return topList;
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public List<Paper> getHeapArray() {
        return Collections.unmodifiableList(heap);
    }

    public List<String> getOperationLogs() {
        return Collections.unmodifiableList(operationLogs);
    }

    /**
     * Returns JSON representation of the current Heap structure for visualization.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"size\":").append(heap.size()).append(",");
        sb.append("\"heapArray\":[");
        for (int i = 0; i < heap.size(); i++) {
            Paper p = heap.get(i);
            sb.append("{");
            sb.append("\"index\":").append(i).append(",");
            sb.append("\"id\":\"").append(p.getId()).append("\",");
            sb.append("\"title\":\"").append(p.getTitle().replace("\"", "\\\"")).append("\",");
            sb.append("\"citations\":").append(p.getInCitationCount()).append(",");
            sb.append("\"parentIndex\":").append(i > 0 ? (i - 1) / 2 : -1).append(",");
            sb.append("\"leftIndex\":").append(2 * i + 1 < heap.size() ? 2 * i + 1 : -1).append(",");
            sb.append("\"rightIndex\":").append(2 * i + 2 < heap.size() ? 2 * i + 2 : -1);
            sb.append("}");
            if (i < heap.size() - 1) sb.append(",");
        }
        sb.append("],\"logs\":[");
        for (int i = 0; i < operationLogs.size(); i++) {
            sb.append("\"").append(operationLogs.get(i).replace("\"", "\\\"")).append("\"");
            if (i < operationLogs.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}
