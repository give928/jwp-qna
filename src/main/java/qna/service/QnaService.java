package qna.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qna.domain.DeleteHistory;
import qna.domain.Question;
import qna.domain.User;
import qna.exception.CannotDeleteException;
import qna.exception.NotFoundException;
import qna.repository.QuestionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QuestionRepository questionRepository;
    private final DeleteHistoryService deleteHistoryService;

    @Transactional(readOnly = true)
    public Question findQuestionById(Long id) {
        return questionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void deleteQuestion(User loginUser, Long questionId) throws CannotDeleteException {
        Question question = findQuestionById(questionId);
        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }
}
