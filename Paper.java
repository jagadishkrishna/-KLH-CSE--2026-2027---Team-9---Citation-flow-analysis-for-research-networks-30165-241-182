public class Paper {

    private String paperId;
    private String title;
    private String author;
    private int year;
    private String category;

    // Constructor
    public Paper(String paperId, String title, String author, int year) {
        this.paperId = paperId;
        this.title = title;
        this.author = author;
        this.year = year;
        this.category = null;
    }

    public Paper(String paperId, String category) {
        this.paperId = paperId;
        this.title = null;
        this.author = null;
        this.year = -1;
        this.category = category;
    }

    // Getters
    public String getPaperId() {
        return paperId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        if (category != null) {
            return "Paper ID : " + paperId +
                    "\nCategory : " + category;
        }

        return "Paper ID : " + paperId +
                "\nTitle    : " + title +
                "\nAuthor   : " + author +
                "\nYear     : " + year;
    }
}