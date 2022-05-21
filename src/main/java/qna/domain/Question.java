package qna.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qna.exception.CannotDeleteException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Question extends BaseEntity {
    private static final String ERROR_MESSAGE_CANNOT_DELETE_QUESTION = "질문을 삭제할 권한이 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Column(nullable = false)
    private boolean deleted = false;

    @Embedded
    private Answers answers = new Answers();

    public Question(String title, String contents) {
        this(null, title, contents);
    }

    public Question(Long id, String title, String contents) {
        this(id, title, contents, null, false, null);
    }

    @Builder
    public Question(Long id, String title, String contents, User writer, boolean deleted, Answers answers) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.deleted = deleted;
        this.answers = answers;
        if (this.answers == null) {
            this.answers = new Answers();
        }
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    public boolean isOwner(User writer) {
        return this.writer.equals(writer);
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException(ERROR_MESSAGE_CANNOT_DELETE_QUESTION);
        }

        this.deleted = true;
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(DeleteHistory.from(this));

        deleteHistories.addAll(answers.delete(loginUser));

        return deleteHistories;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writer.id=" + writer.getId() +
                ", deleted=" + deleted +
                '}';
    }
}
