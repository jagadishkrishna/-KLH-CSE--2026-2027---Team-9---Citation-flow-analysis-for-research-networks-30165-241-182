package model;

import java.util.Objects;

/**
 * Represents a Research Paper vertex in the citation network graph.
 * Encapsulates paper metadata along with incoming and outgoing citation counters.
 */
public class Paper {
    private String id;               // e.g. "P101"
    private String title;            // e.g. "Attention Is All You Need"
    private String authors;          // e.g. "Vaswani et al."
    private int year;                // e.g. 2017
    private String topic;            // e.g. "Transformer & NLP"
    private String abstractText;     // Brief summary of paper contributions
    private String doi;              // e.g. "10.48550/arXiv.1706.03762"
    private int inCitationCount;     // Number of papers citing this paper (In-Degree)
    private int outCitationCount;    // Number of references made by this paper (Out-Degree)

    public Paper(String id, String title, String authors, int year, String topic, String abstractText, String doi) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Paper ID cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.id = id.trim().toUpperCase();
        this.title = title.trim();
        this.authors = (authors != null && !authors.trim().isEmpty()) ? authors.trim() : "Unknown Author";
        this.year = (year > 1900 && year <= 2030) ? year : 2020;
        this.topic = (topic != null && !topic.trim().isEmpty()) ? topic.trim() : "General Computer Science";
        this.abstractText = (abstractText != null) ? abstractText.trim() : "";
        this.doi = (doi != null && !doi.trim().isEmpty()) ? doi.trim() : "doi:10.1000/" + this.id.toLowerCase();
        this.inCitationCount = 0;
        this.outCitationCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getInCitationCount() {
        return inCitationCount;
    }

    public void setInCitationCount(int inCitationCount) {
        this.inCitationCount = Math.max(0, inCitationCount);
    }

    public int getOutCitationCount() {
        return outCitationCount;
    }

    public void setOutCitationCount(int outCitationCount) {
        this.outCitationCount = Math.max(0, outCitationCount);
    }

    public void incrementInCitations() {
        this.inCitationCount++;
    }

    public void decrementInCitations() {
        if (this.inCitationCount > 0) {
            this.inCitationCount--;
        }
    }

    public void incrementOutCitations() {
        this.outCitationCount++;
    }

    public void decrementOutCitations() {
        if (this.outCitationCount > 0) {
            this.outCitationCount--;
        }
    }

    /**
     * Converts the Paper object to a JSON string representation without external libraries.
     */
    public String toJson() {
        return "{" +
                "\"id\":\"" + escapeJson(id) + "\"," +
                "\"title\":\"" + escapeJson(title) + "\"," +
                "\"authors\":\"" + escapeJson(authors) + "\"," +
                "\"year\":" + year + "," +
                "\"topic\":\"" + escapeJson(topic) + "\"," +
                "\"abstractText\":\"" + escapeJson(abstractText) + "\"," +
                "\"doi\":\"" + escapeJson(doi) + "\"," +
                "\"inCitationCount\":" + inCitationCount + "," +
                "\"outCitationCount\":" + outCitationCount +
                "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paper paper = (Paper) o;
        return Objects.equals(id, paper.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d) | In-Citations: %d | Out-Citations: %d",
                id, title, year, inCitationCount, outCitationCount);
    }
}
