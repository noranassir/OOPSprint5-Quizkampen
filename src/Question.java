public class Question {

    private int questionNumber;
    private String quizQuestion;
    private int quizCategory;
    public Question(int quizCategory, int questionNumber, String quizQuestion){
        this.questionNumber = questionNumber;
        this.quizQuestion = quizQuestion;
        this.quizCategory = quizCategory;
    }
    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionType(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(String quizQuestion) {
        this.quizQuestion = quizQuestion;
    }

    public int getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(int quizCategory) {
        this.quizCategory = quizCategory;
    }

    public String toString(){
        return quizCategory + " " + questionNumber + " " + quizQuestion;
    }
}
