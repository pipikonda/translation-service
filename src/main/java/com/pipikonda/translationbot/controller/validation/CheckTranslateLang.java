package com.pipikonda.translationbot.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckLangValidator.class)
public @interface CheckTranslateLang {

    String message() default "Target and source should be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
