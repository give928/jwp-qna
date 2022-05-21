package qna.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answers {
    @OneToMany(mappedBy = "question", orphanRemoval = true)
    private List<Answer> values = new ArrayList<>();

    public Answers(List<Answer> answers) {
        this.values = answers;
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
    }

    public void add(Answer answer) {
        values.add(answer);
    }

    public List<DeleteHistory> delete(User loginUser) {
        return values.stream()
                .map(answer -> answer.delete(loginUser))
                .collect(Collectors.toList());
    }
}
