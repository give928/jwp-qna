package qna.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    private static long id = 1;

    public static Answer newInstance() {
        return of(id++, QuestionTest.newInstance(), UserTest.newInstance());
    }

    public static Answer of(Question question, User writer) {
        return of(id++, question, writer);
    }

    public static Answer of(long id, Question question, User writer) {
        return Answer.builder()
                .question(question)
                .contents("answer contents " + id)
                .writer(writer)
                .build();
    }

    @Test
    @DisplayName("답변을 삭제하면 삭제 상태(deleted)를 변경하고 답변 삭제 이력에 대한 정보 DeleteHistory를 반환한다.")
    void delete() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.newInstance();
        Answer answer = of(1L, question, writer);

        // when
        DeleteHistory deleteHistory = answer.delete(writer);

        // then
        assertAll(
                () -> assertThat(answer.isDeleted()).isTrue(),
                () -> assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.ANSWER),
                () -> assertThat(deleteHistory.getContentId()).isEqualTo(answer.getId()),
                () -> assertThat(deleteHistory.getDeletedBy()).isEqualTo(answer.getWriter())
        );
    }

    @Test
    @DisplayName("답변자가 다른 경우 답변을 삭제할 수 없다.")
    void cannotDelete() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.newInstance();
        Answer answer = of(question, writer);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> answer.delete(UserTest.newInstance());

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotDeleteException.class);
    }
}
