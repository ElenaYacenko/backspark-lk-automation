package api.helpers;

import api.models.allMeters.AllMetersResponseModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Хелпер для работы с API истории показаний приборов учёта (allMeters).
 * Содержит вспомогательные методы для проверки ответов API и извлечения данных.
 */
public class AllMetersHelper {

    /**
     * Проверяет, что ответ API содержит ошибку (success=false, непустое сообщение).
     */
    public static void assertErrorResponse(api.models.allMeters.ErrorResponseModel errorResponse, String expectedMessage) {
        assertThat(errorResponse.success())
                .as("Поле success должно быть false")
                .isFalse();

        assertThat(errorResponse.error())
                .as("Поле error должно содержать сообщение: '%s'", expectedMessage)
                .isNotBlank()
                .containsIgnoringCase(expectedMessage);
    }

    /**
     * Проверяет, что все даты показаний в ответе находятся в заданном диапазоне.
     */
    public static void assertAllReadingDatesInRange(List<AllMetersResponseModel> response, LocalDate limitStart, LocalDate limitEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        assertThat(response)
                .flatExtracting(AllMetersResponseModel::meters)
                .allSatisfy(meter -> {
                    String dateStr = meter.readingDate().substring(0, 10);
                    LocalDate readingDate = LocalDate.parse(dateStr, formatter);
                    assertThat(readingDate)
                            .as("Дата показания '%s' должна быть в диапазоне [%s, %s]", dateStr, limitStart, limitEnd)
                            .isBetween(limitStart, limitEnd);
                });
    }

    /**
     * Проверяет, что в ответах присутствуют ровно указанные тарифы.
     */
    public static void assertTariffsPresent(List<AllMetersResponseModel> response, String... expectedTariffs) {
        List<String> allTariffTitles = response.stream()
                .flatMap(r -> r.meters().stream())
                .map(AllMetersResponseModel.Meter::tariffTitle)
                .distinct()
                .toList();

        assertThat(allTariffTitles)
                .as("Должны присутствовать тарифы: %s", String.join(", ", expectedTariffs))
                .containsExactlyInAnyOrder(expectedTariffs);
    }
}
