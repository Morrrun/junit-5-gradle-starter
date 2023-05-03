package com.alexsandrov.junit.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Я указал переменную окружения в VM Options
 * -ea -Dskip="yes"
 */
public class ConditionalExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return System.getProperty("skip") != null
                ? ConditionEvaluationResult.disabled("test is skipped")
                : ConditionEvaluationResult.enabled("enable by default");
    }
}
