package com.pipikonda.translationbot.controller.validation;

import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class CheckLangValidator implements ConstraintValidator<CheckTranslateLang, CreateWordDto> {

    @Override
    public boolean isValid(CreateWordDto value, ConstraintValidatorContext context) {
        return !value.getSourceLang().equals(value.getTargetLang());
    }
}
