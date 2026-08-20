package dsa;

import model.Paper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Custom Merge Sort Implementation for Research Papers.
 * 
 * Paradigm: Divide and Conquer
 * 1. Divide: Split the paper array into two halves at mid = (low + high) / 2.
 * 2. Conquer: Recursively sort left and right halves.
 * 3. Combine: Merge the two sorted sub-arrays into a single sorted array.
 * 
 * Stability: Stable (preserves original order of papers with identical keys).
 * Time Complexity:
 * - Best Case: O(n log n)
 * - Average Case: O(n log n)
 * - Worst Case: O(n log n)
 * 
 * Space Complexity: O(n) auxiliary space for merging.
 */
public class MergeSort {

    public static class SortResult {
        private final List<Paper> sortedList;
        private final long comparisons;
        private final long executionTimeNanos;
        private final String algorithm = "Merge Sort";
        private final String timeComplexity = "O(n log n)";
        private final String spaceComplexity = "O(n)";

        public SortResult(List<Paper> sortedList, long comparisons, long executionTimeNanos) {
            this.sortedList = sortedList;
            this.comparisons = comparisons;
            this.executionTimeNanos = executionTimeNanos;
        }

        public List<Paper> getSortedList() { return sortedList; }
        public long getComparisons() { return comparisons; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getAlgorithm() { return algorithm; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
    }

    /**
     * Sorts a list of papers using custom Merge Sort.
     */
    public static SortResult sort(List<Paper> papers, Comparator<Paper> comparator) {
        long startTime = System.nanoTime();
        if (papers == null || papers.isEmpty()) {
            return new SortResult(new ArrayList<>(), 0, 0);
        }

        List<Paper> list = new ArrayList<>(papers);
        long[] comparisonCounter = new long[]{0};

        mergeSortInternal(list, 0, list.size() - 1, comparator, comparisonCounter);

        long duration = System.nanoTime() - startTime;
        return new SortResult(list, comparisonCounter[0], duration);
    }

    private static void mergeSortInternal(List<Paper> list, int low, int high,
                                          Comparator<Paper> comparator, long[] comparisons) {
        if (low < high) {
            int mid = low + (high - low) / 2;
            // Divide & Conquer
            mergeSortInternal(list, low, mid, comparator, comparisons);
            mergeSortInternal(list, mid + 1, high, comparator, comparisons);
            // Combine
            merge(list, low, mid, high, comparator, comparisons);
        }
    }

    private static void merge(List<Paper> list, int low, int mid, int high,
                              Comparator<Paper> comparator, long[] comparisons) {
        int n1 = mid - low + 1;
        int n2 = high - mid;

        List<Paper> leftList = new ArrayList<>(n1);
        List<Paper> rightList = new ArrayList<>(n2);

        for (int i = 0; i < n1; i++) leftList.add(list.get(low + i));
        for (int j = 0; j < n2; j++) rightList.add(list.get(mid + 1 + j));

        int i = 0, j = 0, k = low;
        while (i < n1 && j < n2) {
            comparisons[0]++;
            if (comparator.compare(leftList.get(i), rightList.get(j)) <= 0) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }

        while (i < n1) {
            list.set(k++, leftList.get(i++));
        }

        while (j < n2) {
            list.set(k++, rightList.get(j++));
        }
    }
}
