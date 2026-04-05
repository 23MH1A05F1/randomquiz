package com.randomquiz.demo;


import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    private Question createQuestion(String lang) {
        Question q = new Question();
        q.setLanguage(lang);
        q.setQuestionText("Sample question for " + lang);
        q.setOptionA("Option A");
        q.setOptionB("Option B");
        q.setOptionC("Option C");
        q.setOptionD("Option D");
        q.setCorrectAnswer("A");
        return q;
    }

    @Test
    void testFindRandom10_returnsMaxTen() {
        for (int i = 0; i < 15; i++) {
            questionRepository.save(createQuestion("java"));
        }
        List<Question> result = questionRepository.findRandom10ByLanguage("java");
        assertTrue(result.size() <= 10);
    }

    @Test
    void testFindRandom10_onlyCorrectLanguage() {
        for (int i = 0; i < 5; i++) questionRepository.save(createQuestion("java"));
        for (int i = 0; i < 5; i++) questionRepository.save(createQuestion("python"));

        List<Question> result = questionRepository.findRandom10ByLanguage("java");
        result.forEach(q -> assertEquals("java", q.getLanguage()));
    }

    @Test
    void testFindRandom10_emptyForUnknownLanguage() {
        List<Question> result = questionRepository.findRandom10ByLanguage("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveQuestion_persistsCorrectly() {
        Question q = createQuestion("python");
        Question saved = questionRepository.save(q);
        assertNotNull(saved.getId());
        assertEquals("python", saved.getLanguage());
        assertEquals("A", saved.getCorrectAnswer());
    }
}