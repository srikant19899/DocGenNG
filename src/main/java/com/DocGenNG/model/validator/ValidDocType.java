package com.DocGenNG.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DocTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDocType {
    String message() default "Invalid docType: must be between 2 and 50 characters length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
