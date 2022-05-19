package qna.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AnswerRepositoryTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("답변을 등록한다.")
    void save() {
        // given
        Answer answer1 = newAnswer(1);

        // when
        answerRepository.save(answer1);

        // then
        Answer findAnswer1 = answerRepository.findById(answer1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findAnswer1).isEqualTo(answer1);
    }

    @Test
    @DisplayName("답변을 수정한다.")
    void update() {
        // given
        Answer answer1 = saveAnswer(1);
        String updateContents = "answer contents 2";

        // when
        answer1.setContents(updateContents);

        // then
        Answer findAnswer1 = answerRepository.findById(answer1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findAnswer1.getContents()).isEqualTo(updateContents);
    }

    @Test
    @DisplayName("답변을 삭제한다.")
    void delete() {
        // given
        Answer answer1 = saveAnswer(1);

        // when
        answerRepository.delete(answer1);

        // then
        Optional<Answer> deletedAnswer = answerRepository.findById(answer1.getId());
        assertThat(deletedAnswer).isNotPresent();
    }

    @Test
    @DisplayName("전체 답변을 조회한다.")
    void findAll() {
        // given
        Answer answer1 = saveAnswer(1);
        Answer answer2 = saveAnswer(2);

        // when
        List<Answer> answers = answerRepository.findAll();

        // then
        assertThat(answers).containsExactly(answer1, answer2);
    }

    @Test
    @DisplayName("id로 답변을 조회한다.")
    void findById() {
        // given
        Answer answer1 = saveAnswer(1);

        // when
        Answer findAnswer1 = answerRepository.findById(answer1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findAnswer1).isEqualTo(answer1);
    }

    @Test
    @DisplayName("question_id로 삭제되지 않은 답변 컬렉션을 조회한다.")
    void findByQuestionIdAndDeletedFalse() {
        // given
        Answer a1 = saveAnswer(1);
        Answer a2 = saveAnswer(2);
        answerRepository.save(a1);
        answerRepository.save(a2);

        // when
        List<Answer> answers = answerRepository.findByQuestionIdAndDeletedFalse(a1.getQuestionId());

        // then
        assertThat(answers).containsExactly(a1, a2);
    }

    @Test
    @DisplayName("id로 삭제되지 않은 답변을 조회한다.")
    void findByIdAndDeletedFalse() {
        // given
        Answer a1 = saveAnswer(1);
        answerRepository.save(a1);

        // when
        Answer answer = answerRepository.findByIdAndDeletedFalse(a1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(answer).isEqualTo(a1);
    }

    private Answer saveAnswer(int no) {
        return answerRepository.save(newAnswer(no));
    }

    private Answer newAnswer(int no) {
        return Answer.builder()
                .writerId(UserTest.JAVAJIGI.getId())
                .questionId(QuestionTest.Q1.getId())
                .contents("answer contents " + no)
                .build();
    }
}
