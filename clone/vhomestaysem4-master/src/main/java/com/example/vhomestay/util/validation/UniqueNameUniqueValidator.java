package com.example.vhomestay.util.validation;

import com.example.vhomestay.model.entity.Area;
import com.example.vhomestay.repository.AreaRepository;
import com.example.vhomestay.util.annotation.UniqueName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueNameUniqueValidator implements
        ConstraintValidator<UniqueName, String> {
    private final AreaRepository areaRepository;

    @Override
    public void initialize(UniqueName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Area> area = areaRepository.findAreByName(name);

        if (area.isPresent()){
            String areaName = area.get().getName().toLowerCase().replace("//s+", " ");

            if (areaName.equals(name.toLowerCase().replace("//s+", " ")))
                return false;
        }
        return true;
    }
}
