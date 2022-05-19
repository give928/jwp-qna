package qna.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문을 등록한다.")
    void save() {
        // given
        Question question1 = newQuestion(1);

        // when
        questionRepository.save(question1);

        // then
        Question findQuestion1 = questionRepository.findById(question1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findQuestion1).isEqualTo(question1);
    }

    @Test
    @DisplayName("100글자가 넘는 제목을 등록하면 예외가 발생한다.")
    void thrownByTooLongValue() {
        // given
        Question question1 = Question.builder()
                .title("가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차" +
                               "가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차" +
                               "?")
                .contents("contents")
                .build()
                .writeBy(UserTest.JAVAJIGI);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> questionRepository.save(question1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("질문을 수정한다.")
    void update() {
        // given
        Question question1 = saveQuestion(1);
        String updateContents = "contents2";

        // when
        question1.setContents(updateContents);

        // then
        Question findQuestion1 = questionRepository.findById(question1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findQuestion1.getContents()).isEqualTo(updateContents);
    }

    @Test
    @DisplayName("질문을 삭제한다.")
    void delete() {
        // given
        Question question1 = saveQuestion(1);

        // when
        questionRepository.delete(question1);

        // then
        Optional<Question> deletedQuestion = questionRepository.findById(question1.getId());
        assertThat(deletedQuestion).isNotPresent();
    }

    @Test
    @DisplayName("전체 질문을 조회한다.")
    void findAll() {
        // given
        Question question1 = saveQuestion(1);
        Question question2 = saveQuestion(2);

        // when
        List<Question> questions = questionRepository.findAll();

        // then
        assertThat(questions).containsExactly(question1, question2);
    }

    @Test
    @DisplayName("id로 질문을 조회한다.")
    void findById() {
        // given
        Question question1 = saveQuestion(1);

        // when
        Question findQuestion1 = questionRepository.findById(question1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findQuestion1).isEqualTo(question1);
    }

    @Test
    @DisplayName("삭제되지 않은 질문 컬렉션을 조회한다.")
    void findByDeletedFalse() {
        // given
        Question q1 = saveQuestion(1);
        Question q2 = saveQuestion(2);

        // when
        List<Question> questions = questionRepository.findByDeletedFalse();

        // then
        assertThat(questions).containsExactly(q1, q2);
    }

    @Test
    @DisplayName("id로 삭제되지 않은 질문을 조회한다.")
    void findByIdAndDeletedFalse() {
        // given
        Question q1 = saveQuestion(1);

        // when
        Question findQuestion = questionRepository.findByIdAndDeletedFalse(q1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findQuestion).isEqualTo(q1);
    }

    private Question saveQuestion(int no) {
        return questionRepository.save(newQuestion(no));
    }

    private Question newQuestion(int no) {
        return Question.builder()
                .title("title" + no)
                .contents("contents" + no)
                .build()
                .writeBy(UserTest.JAVAJIGI);
    }
}
