package com.agibank.sisagi.validator;

import com.agibank.sisagi.util.CpfValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidatorConstraint implements ConstraintValidator<Cpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }
        return CpfValidator.isValid(cpf);
    }
}
