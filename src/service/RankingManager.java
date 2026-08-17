package service;

import dsa.CustomMaxHeap;
import dsa.MergeSort;
import dsa.QuickSort;
import model.Paper;

import java.util.Comparator;
import java.util.List;

/**
 * Orchestrates paper ranking algorithms using Max-Heap, Merge Sort, and Quick Sort.
 * Provides explicit step metrics and complexity traces for viva demonstrations.
 */
public class RankingManager {
    private final PaperManager paperManager;

    public RankingManager(PaperManager paperManager) {
        this.paperManager = paperManager;
    }

    /**
     * Extracts Top-K most cited papers using Custom Binary Max-Heap.
     * Complexity: O(n) build-heap + O(k log n) extract-max = O(n + k log n)
     */
    public CustomMaxHeap getTopRankedHeap(int k) {
        List<Paper> papers = paperManager.getAllPapers();
        CustomMaxHeap heap = new CustomMaxHeap(papers);
        return heap;
    }

    /**
     * Sorts papers by chosen attribute and order using either Merge Sort or Quick Sort.
     */
    public Object sortPapers(String sortBy, String order, String algorithm) {
        List<Paper> papers = paperManager.getAllPapers();
        boolean ascending = "asc".equalsIgnoreCase(order);

        Comparator<Paper> comparator;
        if ("year".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingInt(Paper::getYear);
        } else if ("title".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Paper::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("id".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Paper::getId);
        } else {
            // Default: citations
            comparator = Comparator.comparingInt(Paper::getInCitationCount);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        if ("quicksort".equalsIgnoreCase(algorithm)) {
            return QuickSort.sort(papers, comparator);
        } else {
            return MergeSort.sort(papers, comparator);
        }
    }
}
