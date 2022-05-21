package qna.domain;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    private static int id = 1;

    public static Answer newInstance() {
        int no = id++;
        return Answer.builder()
                .question(QuestionTest.newInstance())
                .contents("answer contents " + no)
                .writer(UserTest.newInstance())
                .build();
    }

    public static Answer of(Question question, User writer) {
        int no = id++;
        return Answer.builder()
                .question(question)
                .contents("answer contents " + no)
                .writer(writer)
                .build();
    }
}
