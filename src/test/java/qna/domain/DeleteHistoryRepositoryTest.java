package qna.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeleteHistoryRepositoryTest {
    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Test
    @DisplayName("삭제이력를 등록력한다.")
    void save() {
        // given
        DeleteHistory deleteHistory1 = newDeleteHistory(1);

        // when
        deleteHistoryRepository.save(deleteHistory1);

        // then
        DeleteHistory findDeleteHistory1 = deleteHistoryRepository.findById(deleteHistory1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findDeleteHistory1).isEqualTo(deleteHistory1);
    }

    @Test
    @DisplayName("삭제이력를 삭제한다.")
    void delete() {
        // given
        DeleteHistory deleteHistory1 = saveDeleteHistory(1);

        // when
        deleteHistoryRepository.delete(deleteHistory1);

        // then
        Optional<DeleteHistory> deletedDeleteHistory = deleteHistoryRepository.findById(deleteHistory1.getId());
        assertThat(deletedDeleteHistory).isNotPresent();
    }

    @Test
    @DisplayName("전체 삭제이력를 조회한다.")
    void findAll() {
        // given
        DeleteHistory deleteHistory1 = saveDeleteHistory(1);
        DeleteHistory deleteHistory2 = saveDeleteHistory(2);

        // when
        List<DeleteHistory> deleteHistorys = deleteHistoryRepository.findAll();

        // then
        assertThat(deleteHistorys).containsExactly(deleteHistory1, deleteHistory2);
    }

    @Test
    @DisplayName("id로 삭제이력를 조회한다.")
    void findById() {
        // given
        DeleteHistory deleteHistory1 = saveDeleteHistory(1);

        // when
        DeleteHistory findDeleteHistory1 = deleteHistoryRepository.findById(deleteHistory1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findDeleteHistory1).isEqualTo(deleteHistory1);
    }

    private DeleteHistory saveDeleteHistory(int no) {
        return deleteHistoryRepository.save(newDeleteHistory(no));
    }

    private DeleteHistory newDeleteHistory(int no) {
        return DeleteHistory.builder()
                .contentId(QuestionTest.Q1.getId())
                .contentType(ContentType.QUESTION)
                .deletedById(UserTest.JAVAJIGI.getId())
                .createDate(LocalDateTime.now())
                .build();
    }
}
