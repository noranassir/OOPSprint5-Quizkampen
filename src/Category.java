public class Category {

    private int quizCategory;
    private String categoryName;

    public Category(int quizCategory, String categoryName) {
        this.quizCategory = quizCategory;
        this.categoryName = categoryName;
    }

    public int getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(int quizCategory) {
        this.quizCategory = quizCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}