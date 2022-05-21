package qna.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AnswersTest {
    @Test
    @DisplayName("답변을 삭제하면 삭제 상태(deleted)를 변경하고 답변 삭제 이력에 대한 정보 DeleteHistory를 반환한다.")
    void delete() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.newInstance();
        Answer answer1 = AnswerTest.of(1L, question, writer);
        Answer answer2 = AnswerTest.of(2L, question, writer);
        Answers answers = new Answers(Arrays.asList(answer1, answer2));

        // when
        List<DeleteHistory> deleteHistories = answers.delete(writer);

        // then
        assertAll(
                () -> assertThat(answer1.isDeleted()).isTrue(),
                () -> assertThat(answer2.isDeleted()).isTrue(),
                () -> assertThat(deleteHistories).hasSize(2),
                () -> assertThat(deleteHistories.get(0).getContentType()).isEqualTo(ContentType.ANSWER),
                () -> assertThat(deleteHistories.get(0).getContentId()).isEqualTo(answer1.getId()),
                () -> assertThat(deleteHistories.get(1).getContentType()).isEqualTo(ContentType.ANSWER),
                () -> assertThat(deleteHistories.get(1).getContentId()).isEqualTo(answer2.getId())
        );
    }

    @Test
    @DisplayName("답변자가 다른 경우 답변을 삭제할 수 없다.")
    void cannotDelete() {
        // given
        User writer = UserTest.newInstance();
        Question question = QuestionTest.newInstance();
        Answer answer1 = AnswerTest.of(question, writer);
        Answer answer2 = AnswerTest.of(question, writer);
        Answers answers = new Answers(Arrays.asList(answer1, answer2));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> answers.delete(UserTest.newInstance());

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(CannotDeleteException.class);
    }
}
