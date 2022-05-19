package qna.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자를 등록한다.")
    void save() {
        // given
        User user1 = newUser(1);

        // when
        userRepository.save(user1);

        // then
        User findUser1 = userRepository.findById(user1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findUser1).isEqualTo(user1);
    }

    @DisplayName("user_id, password, name, email 컬럼 길이보다 긴 값을 등록하면 예외가 발생한다.")
    @ParameterizedTest(name = "{displayName} userId={0}, password={1}, name={2}, email={3}")
    @CsvSource(value = {"가나다라마바사아자차가나다라마바사아자차?,test,test,test",
            "test,가나다라마바사아자차가나다라마바사아자차?,test,test",
            "test,test,가나다라마바사아자차가나다라마바사아자차?,test",
            "test,test,test,가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차가나다라마바사아자차?"}, delimiterString = ",")
    void thrownByTooLongValue(String userId, String password, String name, String email) {
        // given
        User user1 = User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .email(email)
                .build();

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userRepository.save(user1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("사용자를 수정한다.")
    void update() {
        // given
        User user1 = saveUser(1);
        String updateName = "test2";

        // when
        user1.setName(updateName);

        // then
        User findUser1 = userRepository.findById(user1.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(findUser1.getName()).isEqualTo(updateName);
    }

    @Test
    @DisplayName("사용자를 삭제한다.")
    void delete() {
        // given
        User user1 = saveUser(1);

        // when
        userRepository.delete(user1);

        // then
        Optional<User> deletedUser = userRepository.findById(user1.getId());
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    @DisplayName("전체 사용자를 조회한다.")
    void findAll() {
        // given
        User user1 = saveUser(1);
        User user2 = saveUser(2);

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).containsExactly(user1, user2);
    }

    @Test
    @DisplayName("id로 사용자를 조회한다.")
    void findById() {
        // given
        User user1 = saveUser(1);

        // when
        User findUser1 = userRepository.findById(user1.getId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findUser1).isEqualTo(user1);
    }

    @Test
    @DisplayName("user_id로 사용자를 조회한다.")
    void findByUserId() {
        // given
        User user1 = saveUser(1);

        // when
        User findUser1 = userRepository.findByUserId(user1.getUserId())
                .orElseThrow(IllegalStateException::new);

        // then
        assertThat(findUser1).isEqualTo(user1);
    }

    private User saveUser(int no) {
        return userRepository.save(newUser(no));
    }

    private User newUser(int no) {
        return User.builder()
                .userId("test" + no)
                .password("pass1234")
                .name("TEST" + no)
                .email("test" + no + "@test.com")
                .build();
    }
}
