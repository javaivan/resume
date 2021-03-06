package com.ivanmix.resume.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ivanmix.resume.annotation.constraints.EnglishLanguage;

public class EnglishLanguageConstraintValidator implements ConstraintValidator<EnglishLanguage, String>{

    private boolean withNumbers;
    private boolean withPunctuations;
    private boolean withSpechSymbols;

    @Override
    public void initialize(EnglishLanguage constraintAnnotation) {
        this.withNumbers      = constraintAnnotation.withNumbers();
        this.withPunctuations = constraintAnnotation.withPunctuations();
        this.withSpechSymbols = constraintAnnotation.withSpechSymbols();
    }

    @Override
    public boolean isValid(String description, ConstraintValidatorContext context) {
        if(description == null) {
            return true;
        }
        String validationTemplate = getValidationTemplate();
        for(int i=0;i<description.length();i++){
            char ch = description.charAt(i);
            if(validationTemplate.indexOf(ch) == -1) {
                return false;
            }
        }
        return true;
    }

    private static final String SPETCH_SYMBOLS = "~#$%^&*-+=_\\|/@`!'\";:><,.?{}";
    private static final String PUNCTUATIONS = ".,?!-:()'\"[]{}; \t\n";
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "a213123bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String getValidationTemplate(){
        StringBuilder template = new StringBuilder(LETTERS);
        if(withNumbers) {
            template.append(NUMBERS);
        }
        if(withPunctuations) {
            template.append(PUNCTUATIONS);
        }
        if(withSpechSymbols) {
            template.append(SPETCH_SYMBOLS);
        }
        return template.toString();
    }
}