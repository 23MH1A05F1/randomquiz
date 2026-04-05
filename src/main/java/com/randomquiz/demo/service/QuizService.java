package com.randomquiz.demo.service;

import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;

    // Fetch 10 random questions for the selected language
    public List<Question> getRandomQuestions(String language) {
        return questionRepository.findRandom10ByLanguage(language.toLowerCase());
    }

    // Calculate score by comparing submitted answers with correct answers
    public int calculateScore(List<Question> questions, Map<String, String> answers) {
        int score = 0;
        for (Question q : questions) {
            String submitted = answers.get("q" + q.getId());
            if (submitted != null && submitted.equalsIgnoreCase(q.getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }
}