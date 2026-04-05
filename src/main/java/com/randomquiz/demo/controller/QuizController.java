package com.randomquiz.demo.controller;

import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    // All 7 languages shown on home page
    private static final List<String> LANGUAGES = Arrays.asList(
        "java", "python", "c", "c++", ".net", "react.js", "node.js"
    );

    // Home page — show language selection
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("languages", LANGUAGES);
        return "index";
    }

    // Quiz page — load 10 random questions for selected language
    @GetMapping("/quiz/{language}")
    public String startQuiz(@PathVariable String language,
                            Model model,
                            HttpSession session) {
        List<Question> questions = quizService.getRandomQuestions(language);
        session.setAttribute("questions", questions);
        model.addAttribute("questions", questions);
        model.addAttribute("language", language);
        return "quiz";
    }

    // Submit page — calculate and show score
    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Map<String, String> answers,
                             HttpSession session,
                             Model model) {
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int score = quizService.calculateScore(questions, answers);
        model.addAttribute("score", score);
        model.addAttribute("total", questions.size());
        return "result";
    }
}