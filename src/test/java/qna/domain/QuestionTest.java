package qna.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class QuestionTest {
    public static final Question Q1 = new Question("title1", "contents1").writeBy(UserTest.JAVAJIGI);
    public static final Question Q2 = new Question("title2", "contents2").writeBy(UserTest.SANJIGI);

    private static long id = 1;

    public static Question newInstance() {
        return of(id++, UserTest.newInstance());
    }

    public static Question from(User writer) {
        return of(id++, writer);
    }

    private static Question of(long id, User writer) {
        return Question.builder()
                .title("question title " + id)
                .contents("question contents" + id)
                .writer(writer)
                .build();
    }

    @Test
    @DisplayName("질문을 삭제하면 질문과 답변을 삭제 상태(deleted)로 변경하고, 삭제 이력에 대한 정보 DeleteHistory를 반환한다.")
    void delete() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.from(writer);
        Answer answer1 = AnswerTest.of(1L, question, writer);
        Answer answer2 = AnswerTest.of(2L, question, writer);

        // when
        List<DeleteHistory> deleteHistories = question.delete(writer);
        System.out.println("deleteHistories = " + deleteHistories);

        // then
        assertAll(
                () -> assertThat(question.isDeleted()).isTrue(),
                () -> assertThat(answer1.isDeleted()).isTrue(),
                () -> assertThat(answer2.isDeleted()).isTrue(),
                () -> assertThat(deleteHistories).hasSize(3),
                () -> assertThat(deleteHistories.get(0).getContentType()).isEqualTo(ContentType.QUESTION),
                () -> assertThat(deleteHistories.get(0).getContentId()).isEqualTo(question.getId()),
                () -> assertThat(deleteHistories.get(1).getContentType()).isEqualTo(ContentType.ANSWER),
                () -> assertThat(deleteHistories.get(1).getContentId()).isEqualTo(answer1.getId()),
                () -> assertThat(deleteHistories.get(2).getContentType()).isEqualTo(ContentType.ANSWER),
                () -> assertThat(deleteHistories.get(2).getContentId()).isEqualTo(answer2.getId())
        );
    }

    @Test
    @DisplayName("질문을 삭제할 때 로그인 사용자와 질문한 사람이 같지 않으면 예외가 발생한다.")
    void thrownByDeleteOtherWriterQuestion() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.from(writer);
        User otherWriter = UserTest.newInstance();

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> question.delete(otherWriter);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("질문을 삭제할 때 로그인 사용자와 답변한 사람이 같지 않으면 예외가 발생한다.")
    void thrownByDeleteOtherWriterAnswer() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.from(writer);
        Answer answer1 = AnswerTest.of(question, writer);
        User otherWriter = UserTest.newInstance();
        Answer answer2 = AnswerTest.of(question, otherWriter);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> question.delete(writer);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotDeleteException.class);
    }
}
