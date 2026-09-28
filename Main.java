import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {

        public static void main(String[] args) throws IOException {
                Path datasetDirectory = args.length == 0 ? Path.of(".") : Path.of(args[0]);
                List<String> paperIds = readColumn(datasetDirectory.resolve("names.tsv"));
                List<String> labelIds = readColumn(datasetDirectory.resolve("labels.tsv"));
                List<String> labelNames = readColumn(datasetDirectory.resolve("names_labels.tsv"));

                if (paperIds.size() != labelIds.size()) {
                        throw new IllegalArgumentException("names.tsv and labels.tsv must have the same number of rows");
                }

                CitationGraph graph = new CitationGraph();
                Map<String, Integer> papersPerCategory = new TreeMap<>();

                for (int index = 0; index < paperIds.size(); index++) {
                        int labelIndex = Integer.parseInt(labelIds.get(index));
                        if (labelIndex < 0 || labelIndex >= labelNames.size()) {
                                throw new IllegalArgumentException("Invalid label index at row " + (index + 1));
                        }

                        String category = labelNames.get(labelIndex);
                        graph.addPaper(new Paper(paperIds.get(index), category));
                        papersPerCategory.merge(category, 1, Integer::sum);
                }

                int citationCount = 0;
                List<String> citationRows = Files.readAllLines(datasetDirectory.resolve("adjacency.tsv"));
                for (int rowIndex = 0; rowIndex < citationRows.size(); rowIndex++) {
                        String[] columns = citationRows.get(rowIndex).trim().split("\\t");
                        if (columns.length < 2) {
                                throw new IllegalArgumentException("Invalid citation row " + (rowIndex + 1));
                        }

                        int sourceIndex = Integer.parseInt(columns[0]);
                        int targetIndex = Integer.parseInt(columns[1]);
                        if (sourceIndex < 0 || sourceIndex >= paperIds.size()
                                        || targetIndex < 0 || targetIndex >= paperIds.size()) {
                                throw new IllegalArgumentException("Citation row has an unknown paper index: " + (rowIndex + 1));
                        }
                        if (columns.length < 3 || Boolean.parseBoolean(columns[2])) {
                                graph.addCitation(paperIds.get(sourceIndex), paperIds.get(targetIndex));
                                citationCount++;
                        }
                }

                System.out.printf("Loaded Cora: %d papers, %d citations%n", paperIds.size(), citationCount);
                System.out.println("Papers by category:");
                papersPerCategory.forEach((category, count) ->
                                System.out.printf("  %s: %d%n", category, count));

                HashMap<String, Integer> citationCounts =
                                new CitationAnalysis(graph).calculateCitationCount();
                System.out.println("Top 10 cited papers:");
                citationCounts.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                                                .thenComparing(Map.Entry.comparingByKey()))
                                .limit(10)
                                .forEach(entry -> System.out.printf("  %s: %d citations%n",
                                                entry.getKey(), entry.getValue()));
        }

        private static List<String> readColumn(Path path) throws IOException {
                return Files.readAllLines(path).stream()
                                .map(String::trim)
                                .filter(value -> !value.isEmpty())
                                .toList();
        }
}