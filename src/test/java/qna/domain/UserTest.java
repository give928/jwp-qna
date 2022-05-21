package qna.domain;

public class UserTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private static long id = 1;

    public static User newInstance() {
        long no = id++;
        return User.builder()
                .userId("test" + no)
                .password("pass1234")
                .name("TEST" + no)
                .email("test" + no + "@test.com")
                .build();
    }
}
