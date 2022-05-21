package qna.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql("/truncate.sql")
@DataJpaTest
class AnswerRepositoryTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("답변을 등록한다.")
    void save() {
        // given
        Answer answer = AnswerTest.newInstance();

        // when
        answerRepository.save(answer);

        // then
        Answer findAnswer1 = answerRepository.findById(answer.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findAnswer1).isEqualTo(answer);
    }

    @Test
    @DisplayName("답변을 수정한다.")
    void update() {
        // given
        Answer answer = answerRepository.save(AnswerTest.newInstance());
        String updateContents = "answer contents 2";

        // when
        answer.setContents(updateContents);

        // then
        Answer findAnswer1 = answerRepository.findById(answer.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findAnswer1.getContents()).isEqualTo(updateContents);
    }

    @Test
    @DisplayName("답변을 삭제한다.")
    void delete() {
        // given
        Answer answer = answerRepository.save(AnswerTest.newInstance());

        // when
        answerRepository.delete(answer);

        // then
        Optional<Answer> deletedAnswer = answerRepository.findById(answer.getId());
        assertThat(deletedAnswer).isNotPresent();
    }

    @Test
    @DisplayName("전체 답변을 조회한다.")
    void findAll() {
        // given
        Answer answer1 = answerRepository.save(AnswerTest.newInstance());
        Answer answer2 = answerRepository.save(AnswerTest.newInstance());

        // when
        List<Answer> answers = answerRepository.findAll();

        // then
        assertThat(answers).containsExactly(answer1, answer2);
    }

    @Test
    @DisplayName("id로 답변을 조회한다.")
    void findById() {
        // given
        Answer answer = answerRepository.save(AnswerTest.newInstance());

        // when
        Answer findAnswer1 = answerRepository.findById(answer.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findAnswer1).isEqualTo(answer);
    }

    @Test
    @DisplayName("question_id로 삭제되지 않은 답변 컬렉션을 조회한다.")
    void findByQuestionIdAndDeletedFalse() {
        // given
        User user = UserTest.newInstance();
        Question question = QuestionTest.from(user);
        Answer answer1 = AnswerTest.of(question, user);
        Answer answer2 = AnswerTest.of(question, user);
        answerRepository.save(answer1);
        answerRepository.save(answer2);

        // when
        List<Answer> answers = answerRepository.findByQuestionIdAndDeletedFalse(question.getId());

        // then
        assertThat(answers).containsExactly(answer1, answer2);
    }

    @Test
    @DisplayName("id로 삭제되지 않은 답변을 조회한다.")
    void findByIdAndDeletedFalse() {
        // given
        Answer answer = answerRepository.save(AnswerTest.newInstance());

        // when
        Answer findAnswer = answerRepository.findByIdAndDeletedFalse(answer.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findAnswer).isEqualTo(answer);
    }

    @Test
    @DisplayName("연관 관계 매핑 후 객체 그래프 탐색 및 지연 로딩 테스트")
    void lazyLoading() {
        // given
        Answer answer = answerRepository.save(AnswerTest.newInstance());
        em.flush();
        em.clear();

        // when
        Answer findAnswer = answerRepository.findById(answer.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertAll(
                () -> assertThat(findAnswer.getQuestion().getId()).isNotNull(),
                () -> assertThat(findAnswer.getWriter().getId()).isNotNull()
        );
    }
}
