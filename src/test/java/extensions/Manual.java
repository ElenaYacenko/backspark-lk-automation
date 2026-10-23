package extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки тестов как ручных (не автоматизированных).
 * Такие тесты будут отображаться в Allure отчете с пометкой "Manual".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Manual {
    String value() default "";

    Priority priority() default Priority.NORMAL;

    String[] tags() default {};

    enum Priority {
        LOW, NORMAL, HIGH, CRITICAL
    }
}