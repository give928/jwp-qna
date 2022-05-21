package qna.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql("/truncate.sql")
@DataJpaTest
class DeleteHistoryRepositoryTest {
    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("삭제이력를 등록력한다.")
    void save() {
        // given
        DeleteHistory deleteHistory = newInstance();

        // when
        deleteHistoryRepository.save(deleteHistory);

        // then
        DeleteHistory findDeleteHistory1 = deleteHistoryRepository.findById(deleteHistory.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findDeleteHistory1).isEqualTo(deleteHistory);
    }

    @Test
    @DisplayName("삭제이력를 삭제한다.")
    void delete() {
        // given
        DeleteHistory deleteHistory = deleteHistoryRepository.save(newInstance());

        // when
        deleteHistoryRepository.delete(deleteHistory);

        // then
        Optional<DeleteHistory> deletedDeleteHistory = deleteHistoryRepository.findById(deleteHistory.getId());
        assertThat(deletedDeleteHistory).isNotPresent();
    }

    @Test
    @DisplayName("전체 삭제이력를 조회한다.")
    void findAll() {
        // given
        DeleteHistory deleteHistory1 = deleteHistoryRepository.save(newInstance());
        DeleteHistory deleteHistory2 = deleteHistoryRepository.save(newInstance());

        // when
        List<DeleteHistory> deleteHistories = deleteHistoryRepository.findAll();

        // then
        assertThat(deleteHistories).containsExactly(deleteHistory1, deleteHistory2);
    }

    @Test
    @DisplayName("id로 삭제이력를 조회한다.")
    void findById() {
        // given
        DeleteHistory deleteHistory = deleteHistoryRepository.save(newInstance());

        // when
        DeleteHistory findDeleteHistory1 = deleteHistoryRepository.findById(deleteHistory.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findDeleteHistory1).isEqualTo(deleteHistory);
    }

    @Test
    @DisplayName("연관 관계 매핑 후 객체 그래프 탐색 및 지연 로딩 테스트")
    void lazyLoading() {
        // given
        DeleteHistory deleteHistory = deleteHistoryRepository.save(newInstance());
        em.flush();
        em.clear();

        // when
        DeleteHistory findDeleteHistory = deleteHistoryRepository.findById(deleteHistory.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertAll(
                () -> assertThat(findDeleteHistory.getDeletedBy().getId()).isNotNull()
        );
    }

    private static DeleteHistory newInstance() {
        Question question = QuestionTest.newInstance();
        return DeleteHistory.builder()
                .contentId(question.getId())
                .contentType(ContentType.QUESTION)
                .deletedBy(question.getWriter())
                .createDate(LocalDateTime.now())
                .build();
    }
}
