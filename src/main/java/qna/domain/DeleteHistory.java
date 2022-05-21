package qna.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeleteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "deleted_by_id", foreignKey = @ForeignKey(name = "fk_delete_history_to_user"))
    private User deletedBy;

    private LocalDateTime createDate;

    @Builder
    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createDate = createDate;
        if (this.createDate == null) {
            this.createDate = LocalDateTime.now();
        }
    }

    public static DeleteHistory from(Answer answer) {
        return DeleteHistory.builder()
                .contentType(ContentType.ANSWER)
                .contentId(answer.getId())
                .deletedBy(answer.getWriter())
                .build();
    }

    public static DeleteHistory from(Question question) {
        return DeleteHistory.builder()
                .contentType(ContentType.QUESTION)
                .contentId(question.getId())
                .deletedBy(question.getWriter())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteHistory that = (DeleteHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeleteHistory{" +
                "id=" + id +
                ", contentType=" + contentType +
                ", contentId=" + contentId +
                ", deletedBy.id=" + deletedBy.getId() +
                ", createDate=" + createDate +
                '}';
    }
}
