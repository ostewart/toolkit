package com.trailmagic.image;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ImageValidator implements Validator {

    public boolean supports(Class clazz) {
        return Image.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "image.name", "image.name.empty");
        ValidationUtils.rejectIfEmpty(errors, "image.displayName",
                                      "image.displayName.empty");

    }
}
