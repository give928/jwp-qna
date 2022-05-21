package qna.repository;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import qna.domain.Question;
import qna.domain.QuestionTest;
import qna.domain.UserTest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql("/truncate.sql")
@DataJpaTest
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("질문을 등록한다.")
    void save() {
        // given
        Question question = QuestionTest.newInstance();
        System.out.println("question = " + question);

        // when
        questionRepository.save(question);

        // then
        Question findQuestion1 = questionRepository.findById(question.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findQuestion1).isEqualTo(question);
    }

    @Test
    @DisplayName("100글자가 넘는 제목을 등록하면 예외가 발생한다.")
    void thrownByTooLongValue() {
        // given
        Question question = Question.builder()
                .title("가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차" +
                               "가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차" +
                               "?")
                .contents("question contents")
                .build()
                .writeBy(UserTest.newInstance());

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> questionRepository.save(question);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("질문을 수정한다.")
    void update() {
        // given
        Question question = questionRepository.save(QuestionTest.newInstance());
        String updateTitle = "question title 1";
        String updateContents = "question contents 2";

        // when
        question.update(updateTitle, updateContents);

        // then
        Question findQuestion1 = questionRepository.findById(question.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findQuestion1.getTitle()).isEqualTo(updateTitle);
        assertThat(findQuestion1.getContents()).isEqualTo(updateContents);
    }

    @Test
    @DisplayName("질문을 삭제한다.")
    void delete() {
        // given
        Question question = questionRepository.save(QuestionTest.newInstance());

        // when
        questionRepository.delete(question);

        // then
        Optional<Question> deletedQuestion = questionRepository.findById(question.getId());
        assertThat(deletedQuestion).isNotPresent();
    }

    @Test
    @DisplayName("전체 질문을 조회한다.")
    void findAll() {
        // given
        Question question1 = questionRepository.save(QuestionTest.newInstance());
        Question question2 = questionRepository.save(QuestionTest.newInstance());

        // when
        List<Question> questions = questionRepository.findAll();

        // then
        assertThat(questions).containsExactly(question1, question2);
    }

    @Test
    @DisplayName("id로 질문을 조회한다.")
    void findById() {
        // given
        Question question = questionRepository.save(QuestionTest.newInstance());

        // when
        Question findQuestion1 = questionRepository.findById(question.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findQuestion1).isEqualTo(question);
    }

    @Test
    @DisplayName("삭제되지 않은 질문 컬렉션을 조회한다.")
    void findByDeletedFalse() {
        // given
        Question q1 = questionRepository.save(QuestionTest.newInstance());
        Question q2 = questionRepository.save(QuestionTest.newInstance());

        // when
        List<Question> questions = questionRepository.findByDeletedFalse();

        // then
        assertThat(questions).containsExactly(q1, q2);
    }

    @Test
    @DisplayName("id로 삭제되지 않은 질문을 조회한다.")
    void findByIdAndDeletedFalse() {
        // given
        Question q1 = questionRepository.save(QuestionTest.newInstance());

        // when
        Question findQuestion = questionRepository.findByIdAndDeletedFalse(q1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findQuestion).isEqualTo(q1);
    }

    @Test
    @DisplayName("연관 관계 매핑 후 객체 그래프 탐색 및 지연 로딩 테스트")
    void lazyLoading() {
        // given
        Question question = questionRepository.save(QuestionTest.newInstance());
        em.flush();
        em.clear();

        // when
        Question findQuestion = questionRepository.findById(question.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertAll(
                () -> assertThat(findQuestion.getWriter().getId()).isNotNull()
        );
    }
}
