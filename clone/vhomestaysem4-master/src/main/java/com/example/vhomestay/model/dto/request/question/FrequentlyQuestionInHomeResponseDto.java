package com.example.vhomestay.model.dto.request.question;

import com.example.vhomestay.enums.FrequentlyQuestionType;
import com.example.vhomestay.model.entity.FrequentlyQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrequentlyQuestionInHomeResponseDto {
    private FrequentlyQuestionType type;
    private List<FrequentlyQuestion> frequentlyQuestions;
}
