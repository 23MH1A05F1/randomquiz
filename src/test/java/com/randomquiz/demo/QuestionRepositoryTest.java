package com.randomquiz.demo;
import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @BeforeEach
    void clearDatabase() {
        questionRepository.deleteAll();
    }

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

    // ---- Random query tests ----

    @Test
    void testFindRandom10_returnsMaxTen() {
        for (int i = 0; i < 15; i++) questionRepository.save(createQuestion("java"));
        List<Question> result = questionRepository.findRandom10ByLanguage("java");
        assertTrue(result.size() <= 10);
        assertFalse(result.isEmpty());
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
        questionRepository.save(createQuestion("java"));
        List<Question> result = questionRepository.findRandom10ByLanguage("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindRandom10_exactlyTenWhenMoreAvailable() {
        for (int i = 0; i < 20; i++) questionRepository.save(createQuestion("python"));
        List<Question> result = questionRepository.findRandom10ByLanguage("python");
        assertEquals(10, result.size());
    }

    @Test
    void testFindRandom10_lessWhenFewerAvailable() {
        for (int i = 0; i < 5; i++) questionRepository.save(createQuestion("c"));
        List<Question> result = questionRepository.findRandom10ByLanguage("c");
        assertEquals(5, result.size());
    }

    // ---- CRUD tests ----

    @Test
    void testSaveQuestion_persistsCorrectly() {
        Question q = createQuestion("python");
        Question saved = questionRepository.save(q);
        assertNotNull(saved.getId());
        assertEquals("python", saved.getLanguage());
        assertEquals("A", saved.getCorrectAnswer());
    }

    @Test
    void testFindById_returnsQuestion() {
        Question saved = questionRepository.save(createQuestion("java"));
        Optional<Question> found = questionRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("java", found.get().getLanguage());
    }

    @Test
    void testDeleteQuestion_removesFromDB() {
        Question saved = questionRepository.save(createQuestion("java"));
        Long id = saved.getId();
        questionRepository.deleteById(id);
        assertFalse(questionRepository.findById(id).isPresent());
    }

    @Test
    void testCountQuestions_afterSaving() {
        for (int i = 0; i < 5; i++) questionRepository.save(createQuestion("java"));
        assertEquals(5, questionRepository.count());
    }

    @Test
    void testSaveQuestion_allFieldsPersisted() {
        Question q = new Question();
        q.setLanguage("node.js");
        q.setQuestionText("What is Node.js?");
        q.setOptionA("A browser");
        q.setOptionB("A runtime");
        q.setOptionC("A database");
        q.setOptionD("A framework");
        q.setCorrectAnswer("B");

        Question saved = questionRepository.save(q);

        assertEquals("node.js", saved.getLanguage());
        assertEquals("What is Node.js?", saved.getQuestionText());
        assertEquals("A browser", saved.getOptionA());
        assertEquals("A runtime", saved.getOptionB());
        assertEquals("A database", saved.getOptionC());
        assertEquals("A framework", saved.getOptionD());
        assertEquals("B", saved.getCorrectAnswer());
    }

    @Test
    void testFindAll_returnsAllSaved() {
        questionRepository.save(createQuestion("java"));
        questionRepository.save(createQuestion("python"));
        questionRepository.save(createQuestion("c"));
        assertEquals(3, questionRepository.findAll().size());
    }

    @Test
    void testUpdateQuestion_changesValue() {
        Question saved = questionRepository.save(createQuestion("java"));
        saved.setCorrectAnswer("B");
        Question updated = questionRepository.save(saved);
        assertEquals("B", updated.getCorrectAnswer());
    }
}