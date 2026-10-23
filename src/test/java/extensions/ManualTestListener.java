package extensions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Обработчик аннотации @Manual для интеграции с Allure
 * Использует JUnit 5 TestWatcher для перехвата результатов тестов
 */
public class ManualTestListener implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        handleManualTest(context);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        handleManualTest(context);
    }

    @Override
    public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
        handleManualTest(context);
    }

    private void handleManualTest(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();
        Manual manualAnnotation = testMethod.getAnnotation(Manual.class);

        if (manualAnnotation != null) {
            Allure.parameter("Тип теста", "Ручной");
            Allure.parameter("Приоритет", manualAnnotation.priority().name());

            if (manualAnnotation.tags().length > 0) {
                Allure.parameter("Теги", String.join(", ", manualAnnotation.tags()));
            }

            String description = manualAnnotation.value();
            if (!description.isEmpty()) {
                Allure.description(description);
            }

            Allure.step("️ Это ручной тест-кейс", () -> {
                System.out.println("═══════════════════════════════════════");
                System.out.println("РУЧНОЙ ТЕСТ: " + testMethod.getName());
                System.out.println("Описание: " + description);
                System.out.println("Приоритет: " + manualAnnotation.priority());
                System.out.println("Теги: " + Arrays.toString(manualAnnotation.tags()));
                System.out.println("═══════════════════════════════════════");
            });
        }
    }
}