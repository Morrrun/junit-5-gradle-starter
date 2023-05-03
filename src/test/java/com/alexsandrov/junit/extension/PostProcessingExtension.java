package com.alexsandrov.junit.extension;

import org.example.annotation.ForPresentation;
import org.example.service.UserService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        System.out.println("post processing extension");
        var declaredFields = testInstance.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(ForPresentation.class)) {
                declaredField.setAccessible(true);
                declaredField.set(testInstance, new UserService(null));
            }
        }
    }
}
