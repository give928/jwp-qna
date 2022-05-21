package qna.domain;

public class QuestionTest {
    public static final Question Q1 = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
    public static final Question Q2 = new Question("title2", "contents2").writeBy(UserTest.SANJIGI);

    private static int id = 1;

    public static Question newInstance() {
        int no = id++;
        return Question.builder()
                .title("question title " + no)
                .contents("question contents" + no)
                .writer(UserTest.newInstance())
                .build();
    }

    public static Question from(User writer) {
        int no = id++;
        return Question.builder()
                .title("question title " + no)
                .contents("question contents" + no)
                .writer(writer)
                .build();
    }
}
