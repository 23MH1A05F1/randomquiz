package com.randomquiz.demo;

import com.randomquiz.demo.controller.QuizController;
import com.randomquiz.demo.model.Question;
import com.randomquiz.demo.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("languages"));
    }

    @Test
    void testQuizPageLoads_java() throws Exception {
        List<Question> mockQs = new ArrayList<>();
        for (int i = 0; i < 10; i++) mockQs.add(new Question());
        when(quizService.getRandomQuestions("java")).thenReturn(mockQs);

        mockMvc.perform(get("/quiz/java"))
               .andExpect(status().isOk())
               .andExpect(view().name("quiz"))
               .andExpect(model().attributeExists("questions"))
               .andExpect(model().attributeExists("language"));
    }

    @Test
    void testQuizPageLoads_python() throws Exception {
        List<Question> mockQs = new ArrayList<>();
        for (int i = 0; i < 10; i++) mockQs.add(new Question());
        when(quizService.getRandomQuestions("python")).thenReturn(mockQs);

        mockMvc.perform(get("/quiz/python"))
               .andExpect(status().isOk())
               .andExpect(view().name("quiz"));
    }

    @Test
    void testHomePageContainsSevenLanguages() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("languages",
                   org.hamcrest.Matchers.hasSize(7)));
    }
}