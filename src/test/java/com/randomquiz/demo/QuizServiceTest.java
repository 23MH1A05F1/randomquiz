package com.randomquiz.demo;

import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.repository.QuestionRepository;
import com.randomquiz.demo.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Question makeQuestion(Long id, String correct) {
        Question q = new Question();
        q.setId(id);
        q.setCorrectAnswer(correct);
        q.setLanguage("java");
        q.setQuestionText("Sample question " + id);
        q.setOptionA("Option A");
        q.setOptionB("Option B");
        q.setOptionC("Option C");
        q.setOptionD("Option D");
        return q;
    }

    // ---- getRandomQuestions tests ----

    @Test
    void testGetRandomQuestions_returnsTen() {
        List<Question> mockList = new ArrayList<>();
        for (int i = 0; i < 10; i++) mockList.add(makeQuestion((long) i, "A"));
        when(questionRepository.findRandom10ByLanguage("java")).thenReturn(mockList);

        List<Question> result = quizService.getRandomQuestions("java");
        assertEquals(10, result.size());
        verify(questionRepository, times(1)).findRandom10ByLanguage("java");
    }

    @Test
    void testGetRandomQuestions_lowercaseConversion() {
        when(questionRepository.findRandom10ByLanguage("python")).thenReturn(new ArrayList<>());
        quizService.getRandomQuestions("PYTHON");
        verify(questionRepository).findRandom10ByLanguage("python");
    }

    @Test
    void testGetRandomQuestions_mixedCaseConversion() {
        when(questionRepository.findRandom10ByLanguage("java")).thenReturn(new ArrayList<>());
        quizService.getRandomQuestions("Java");
        verify(questionRepository).findRandom10ByLanguage("java");
    }

    @Test
    void testGetRandomQuestions_returnsEmptyWhenNoData() {
        when(questionRepository.findRandom10ByLanguage("unknown")).thenReturn(new ArrayList<>());
        List<Question> result = quizService.getRandomQuestions("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRandomQuestions_forAllLanguages() {
        List<String> langs = Arrays.asList("java", "python", "c", "c++", ".net", "react.js", "node.js");
        for (String lang : langs) {
            when(questionRepository.findRandom10ByLanguage(lang)).thenReturn(new ArrayList<>());
            quizService.getRandomQuestions(lang);
            verify(questionRepository, atLeastOnce()).findRandom10ByLanguage(lang);
        }
    }

    // ---- calculateScore tests ----

    @Test
    void testCalculateScore_allCorrect() {
        List<Question> questions = List.of(
            makeQuestion(1L, "A"),
            makeQuestion(2L, "B"),
            makeQuestion(3L, "C")
        );
        Map<String, String> answers = Map.of("q1", "A", "q2", "B", "q3", "C");
        assertEquals(3, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_allWrong() {
        List<Question> questions = List.of(
            makeQuestion(1L, "A"),
            makeQuestion(2L, "B")
        );
        Map<String, String> answers = Map.of("q1", "D", "q2", "D");
        assertEquals(0, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_partiallyCorrect() {
        List<Question> questions = List.of(
            makeQuestion(1L, "A"),
            makeQuestion(2L, "B"),
            makeQuestion(3L, "C")
        );
        Map<String, String> answers = Map.of("q1", "A", "q2", "D", "q3", "C");
        assertEquals(2, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_emptyAnswers() {
        List<Question> questions = List.of(makeQuestion(1L, "A"));
        assertEquals(0, quizService.calculateScore(questions, Collections.emptyMap()));
    }

    @Test
    void testCalculateScore_caseInsensitive() {
        List<Question> questions = List.of(makeQuestion(1L, "A"));
        Map<String, String> answers = Map.of("q1", "a");
        assertEquals(1, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_emptyQuestionList() {
        assertEquals(0, quizService.calculateScore(new ArrayList<>(), new HashMap<>()));
    }

    @Test
    void testCalculateScore_tenQuestions_allCorrect() {
        List<Question> questions = new ArrayList<>();
        Map<String, String> answers = new HashMap<>();
        String[] opts = {"A", "B", "C", "D"};
        for (int i = 1; i <= 10; i++) {
            String correct = opts[i % 4];
            questions.add(makeQuestion((long) i, correct));
            answers.put("q" + i, correct);
        }
        assertEquals(10, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_tenQuestions_halfCorrect() {
        List<Question> questions = new ArrayList<>();
        Map<String, String> answers = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            questions.add(makeQuestion((long) i, "A"));
            answers.put("q" + i, i <= 5 ? "A" : "B");
        }
        assertEquals(5, quizService.calculateScore(questions, answers));
    }

    @Test
    void testCalculateScore_nullAnswerValue() {
        List<Question> questions = List.of(makeQuestion(1L, "A"));
        Map<String, String> answers = new HashMap<>();
        answers.put("q1", null);
        assertEquals(0, quizService.calculateScore(questions, answers));
    }

    // ---- Question model getter/setter tests ----

    @Test
    void testQuestionModel_allGettersSetters() {
        Question q = new Question();
        q.setId(99L);
        q.setLanguage("java");
        q.setQuestionText("What is JVM?");
        q.setOptionA("Java Virtual Machine");
        q.setOptionB("Java Variable Method");
        q.setOptionC("Just Virtual Memory");
        q.setOptionD("Java Verified Module");
        q.setCorrectAnswer("A");

        assertEquals(99L, q.getId());
        assertEquals("java", q.getLanguage());
        assertEquals("What is JVM?", q.getQuestionText());
        assertEquals("Java Virtual Machine", q.getOptionA());
        assertEquals("Java Variable Method", q.getOptionB());
        assertEquals("Just Virtual Memory", q.getOptionC());
        assertEquals("Java Verified Module", q.getOptionD());
        assertEquals("A", q.getCorrectAnswer());
    }

    @Test
    void testQuestionModel_defaultConstructor() {
        Question q = new Question();
        assertNull(q.getId());
        assertNull(q.getLanguage());
        assertNull(q.getQuestionText());
        assertNull(q.getCorrectAnswer());
    }

    @Test
    void testQuestionModel_setLanguageUpdates() {
        Question q = new Question();
        q.setLanguage("python");
        assertEquals("python", q.getLanguage());
        q.setLanguage("java");
        assertEquals("java", q.getLanguage());
    }

    @Test
    void testQuestionModel_setCorrectAnswerAllOptions() {
        Question q = new Question();
        for (String opt : new String[]{"A", "B", "C", "D"}) {
            q.setCorrectAnswer(opt);
            assertEquals(opt, q.getCorrectAnswer());
        }
    }
}