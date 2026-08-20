package dsa;

import model.Paper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Custom Quick Sort Implementation for Research Papers.
 * 
 * Paradigm: Divide and Conquer with In-place Partitioning (Lomuto / Hoare style).
 * 1. Pivot Selection: Chooses high element as pivot.
 * 2. Partition: Rearranges elements so that elements <= pivot are on left, > pivot on right.
 * 3. Recursion: Recursively sorts partitions.
 * 
 * In-place: Yes.
 * Time Complexity:
 * - Best Case: O(n log n)
 * - Average Case: O(n log n)
 * - Worst Case: O(n^2) (if already sorted and bad pivot choice)
 * 
 * Space Complexity: O(log n) auxiliary stack space for recursion.
 */
public class QuickSort {

    public static class SortResult {
        private final List<Paper> sortedList;
        private final long comparisons;
        private final long swaps;
        private final long executionTimeNanos;
        private final String algorithm = "Quick Sort";
        private final String timeComplexity = "O(n log n) avg, O(n^2) worst";
        private final String spaceComplexity = "O(log n)";

        public SortResult(List<Paper> sortedList, long comparisons, long swaps, long executionTimeNanos) {
            this.sortedList = sortedList;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.executionTimeNanos = executionTimeNanos;
        }

        public List<Paper> getSortedList() { return sortedList; }
        public long getComparisons() { return comparisons; }
        public long getSwaps() { return swaps; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getAlgorithm() { return algorithm; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
    }

    public static SortResult sort(List<Paper> papers, Comparator<Paper> comparator) {
        long startTime = System.nanoTime();
        if (papers == null || papers.isEmpty()) {
            return new SortResult(new ArrayList<>(), 0, 0, 0);
        }

        List<Paper> list = new ArrayList<>(papers);
        long[] stats = new long[]{0, 0}; // [0]: comparisons, [1]: swaps

        quickSortInternal(list, 0, list.size() - 1, comparator, stats);

        long duration = System.nanoTime() - startTime;
        return new SortResult(list, stats[0], stats[1], duration);
    }

    private static void quickSortInternal(List<Paper> list, int low, int high,
                                          Comparator<Paper> comparator, long[] stats) {
        if (low < high) {
            int pivotIdx = partition(list, low, high, comparator, stats);
            quickSortInternal(list, low, pivotIdx - 1, comparator, stats);
            quickSortInternal(list, pivotIdx + 1, high, comparator, stats);
        }
    }

    private static int partition(List<Paper> list, int low, int high,
                                 Comparator<Paper> comparator, long[] stats) {
        Paper pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            stats[0]++; // comparison
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
                stats[1]++; // swap
            }
        }
        swap(list, i + 1, high);
        stats[1]++;
        return i + 1;
    }

    private static void swap(List<Paper> list, int i, int j) {
        Paper temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
