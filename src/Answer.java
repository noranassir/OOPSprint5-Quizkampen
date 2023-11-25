public class Answer {

    private int questionNumber;
    private int answerNumber;
    private String quizAnswer;
    private int quizCategory;
    private Boolean correctAnswer;

    public Answer(int quizCategory, int questionNumber, int answerNumber, String quizAnswer, Boolean correctAnswer) {
        this.questionNumber = questionNumber;
        this.answerNumber = answerNumber;
        this.quizAnswer = quizAnswer;
        this.quizCategory = quizCategory;
        this.correctAnswer = correctAnswer;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public String getQuizAnswer() {
        return quizAnswer;
    }

    public void setQuizAnswer(String quizAnswer) {
        this.quizAnswer = quizAnswer;
    }

    public int getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(int quizCategory) {
        this.quizCategory = quizCategory;
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String toString(){
        return quizCategory + " " + questionNumber + " " + answerNumber + " " + quizAnswer;
    }
}