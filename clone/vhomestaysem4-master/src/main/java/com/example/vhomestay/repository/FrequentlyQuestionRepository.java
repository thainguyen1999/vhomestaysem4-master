package com.example.vhomestay.repository;

import com.example.vhomestay.enums.FrequentlyQuestionType;
import com.example.vhomestay.model.entity.FrequentlyQuestion;
import com.example.vhomestay.model.entity.VillageInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrequentlyQuestionRepository extends JpaRepository<FrequentlyQuestion, Long> {
    @Query("select fq from FrequentlyQuestion fq where fq.status = 'ACTIVE'")
    List<FrequentlyQuestion> findAllActive();
    @Query("select fq from FrequentlyQuestion fq where fq.id = :questionId and fq.status = 'ACTIVE'")
    Optional<FrequentlyQuestion> findByIdActive(@Param("questionId") Long questionId);
    @Query("select fq from FrequentlyQuestion fq where fq.status = 'ACTIVE' and fq.type = :fqt")
    List<FrequentlyQuestion> findAllActiveAndType(@Param("fqt") FrequentlyQuestionType fqt);
}
