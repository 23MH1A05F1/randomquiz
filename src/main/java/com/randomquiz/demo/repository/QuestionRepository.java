package com.randomquiz.demo.repository;


import com.randomquiz.demo.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions WHERE language = :lang ORDER BY RAND() LIMIT 10",
           nativeQuery = true)
    List<Question> findRandom10ByLanguage(@Param("lang") String lang);
}