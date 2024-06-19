package com.docGenSvc.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuoteIdValidator implements ConstraintValidator<ValidQuoteId, String> {

    @Override
    public boolean isValid(String quoteId, ConstraintValidatorContext context) {
        if (quoteId == null) {
            return false;
        }
        return quoteId.length() >= 2 && quoteId.length() <= 50;
    }
}