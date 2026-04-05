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
        return q;
    }

    @Test
    void testGetRandomQuestions_returnsTenQuestions() {
        List<Question> mockList = new ArrayList<>();
        for (int i = 0; i < 10; i++) mockList.add(new Question());
        when(questionRepository.findRandom10ByLanguage("java")).thenReturn(mockList);

        List<Question> result = quizService.getRandomQuestions("java");

        assertEquals(10, result.size());
        verify(questionRepository, times(1)).findRandom10ByLanguage("java");
    }

    @Test
    void testGetRandomQuestions_languageConvertedToLowercase() {
        when(questionRepository.findRandom10ByLanguage("python")).thenReturn(new ArrayList<>());
        quizService.getRandomQuestions("PYTHON");
        verify(questionRepository).findRandom10ByLanguage("python");
    }

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
}