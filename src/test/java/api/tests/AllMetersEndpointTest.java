package api.tests;

import api.models.allMeters.AllMetersResponseModel;
import api.models.allMeters.ErrorResponseModel;
import api.models.login.LoginBodyModel;
import base.TestBase;
import config.TestConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static base.TestData.*;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Показания")
@Feature("Получение информации о показаниях")
@Owner("Elena Yatsenko")

public class AllMetersEndpointTest extends TestBase {

    public static final String ERROR_MISSING = "Отсутствует обязательный параметр";
    public static final String DATE_START = "2026-04-01";
    public static final String DATE_END = "2026-11-01";
    private String token;

    @BeforeEach
    void login() {
        LoginBodyModel loginData = new LoginBodyModel(
                TestConfig.getTestUserPhone(),
                TestConfig.getTestUserPassword()
        );
        token = api.auth.loginAndGetToken(loginData);
    }

    @Test
    @DisplayName("Получение истории показаний с фильтрацией по датам")
    @Description("""
            Цель: Проверка корректной работы фильтрации по startDate и endDate.
            Дано: Авторизованный пользователь, даты начала и конца периода.
            Когда: Отправлен GET запрос на allMeters с query-параметрами startDate и endDate.
            Тогда: HTTP статус 200, ВСЕ возвращенные даты показаний находятся в заданном диапазоне.
            """)
    @Tags({@Tag("api"), @Tag("positive"), @Tag("regression"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnFilteredHistoryByDates() {
        List<AllMetersResponseModel> response = api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER);

        step("Проверка, что история не пустая", () -> {
            assertThat(response)
                    .as("Список истории показаний не должен быть пустым для выбранного периода")
                    .isNotEmpty();
        });

        step("Проверка, что ВСЕ даты показаний попадают в заданный диапазон", () -> {
            LocalDate limitStart = LocalDate.parse(DATE_START);
            LocalDate limitEnd = LocalDate.parse(DATE_END);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // для парсинга "YYYY-MM-DD" из начала строки

            assertThat(response)
                    .flatExtracting(AllMetersResponseModel::meters)
                    .allSatisfy(meter -> {
                        String dateStr = meter.readingDate().substring(0, 10);
                        LocalDate readingDate = LocalDate.parse(dateStr, formatter);

                        assertThat(readingDate)
                                .as("Дата показания '%s' должна быть в диапазоне [%s, %s]", dateStr, DATE_START, DATE_END)
                                .isBetween(limitStart, limitEnd);
                    });
        });
    }

    @Test
    @DisplayName("История показаний для двухтарифного ПУ")
    @Description("""
            Цель: Проверка, что API корректно возвращает показания для двухтарифного счётчика.
            Дано: ЛС с двухтарифным ПУ (День/Ночь).
            Когда: Отправлен GET запрос на allMeters с валидным периодом.
            Тогда: HTTP 200, в ответах присутствуют записи с тарифами 'День' и 'Ночь'.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnHistoryForTwoTariffMeter() {

        List<AllMetersResponseModel> response = api.allMeters.getDeviceReadingsHistory(token, TWO_TARIFF_METER);

        step("Проверка наличия двух тарифов в показаниях", () -> {
            assertThat(response).isNotEmpty();

            List<String> allTariffTitles = response.stream()
                    .flatMap(r -> r.meters().stream())
                    .map(AllMetersResponseModel.Meter::tariffTitle)
                    .distinct()
                    .toList();

            assertThat(allTariffTitles)
                    .as("Должны присутствовать тарифы 'День' и 'Ночь'")
                    .containsExactlyInAnyOrder("День", "Ночь");
        });
    }

    @Test
    @DisplayName("История показаний для трёхтарифного ПУ")
    @Description("""
            Цель: Проверка, что API корректно возвращает показания для трёхтарифного счётчика.
            Дано: ЛС с трёхтарифным ПУ (Пик/Полупик/Ночь).
            Когда: Отправлен GET запрос на allMeters с валидным периодом.
            Тогда: HTTP 200, в ответах присутствуют все 3 тарифа.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnHistoryForThreeTariffMeter() {

        List<AllMetersResponseModel> response = api.allMeters.getDeviceReadingsHistory(token, THREE_TARIFF_METER);

        step("Проверка наличия трёх тарифов в показаниях", () -> {
            assertThat(response).isNotEmpty();

            List<String> allTariffTitles = response.stream()
                    .flatMap(r -> r.meters().stream())
                    .map(AllMetersResponseModel.Meter::tariffTitle)
                    .distinct()
                    .toList();

            assertThat(allTariffTitles)
                    .as("Должны присутствовать тарифы 'Пик', 'Полупик', 'Ночь'")
                    .containsExactlyInAnyOrder("Пик", "Полупик", "Ночь");
        });
    }

    @Test
    @DisplayName("История показаний для ПУ по водоснабжению")
    @Description("""
            Цель: Проверка корректности данных для водяного счётчика.
            Дано: ЛС с ПУ по водоснабжению.
            Когда: Отправлен GET запрос на allMeters с валидным периодом.
            Тогда: HTTP 200, единица измерения — 'куб. м' или аналог.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnHistoryForWaterMeter() {

        List<AllMetersResponseModel> response = api.allMeters.getDeviceReadingsHistory(token, WATER_METER);

        step("Проверка структуры ответа для водяного ПУ", () -> {
            assertThat(response)
                    .as("История показаний не должна быть пустой")
                    .isNotEmpty();
        });
    }

    @Test
    @DisplayName("История показаний для ЛС без ПУ ФКР — пустой массив")
    @Description("""
            Цель: Проверка, что API возвращает пустой массив для ЛС без приборов учёта ФКР.
            Дано: ЛС без привязанных ПУ ФКР.
            Когда: Отправлен GET запрос на allMeters с валидным периодом.
            Тогда: HTTP 200, тело ответа — пустой JSON-массив [].
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturnEmptyArrayForAccountWithoutFkrMeters() {

        List<AllMetersResponseModel> response = api.allMeters.getDeviceReadingsHistory(token, FKR_METER);

        step("Проверка, что ответ — пустой массив", () -> {
            assertThat(response)
                    .as("Для ЛС без ПУ ФКР должен возвращаться пустой массив")
                    .isEmpty();
        });
    }

    @Test
    @DisplayName("Запрос истории показаний без обязательных параметров дат возвращает 400")
    @Description("""
            Цель: Проверка, что API валидирует обязательность параметров startDate и endDate.
            Дано: Авторизованный пользователь, валидный ЛС.
            Когда: Отправлен GET запрос на allMeters БЕЗ query-параметров startDate и endDate.
            Тогда: HTTP статус 400, тело содержит success=false и сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenDatesNotProvided() {

        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDates(token, SINGLE_TARIFF_METER);

        step("Проверка структуры ответа об ошибке", () -> {
            assertThat(errorResponse.success())
                    .as("Поле success должно быть false")
                    .isFalse();

            assertThat(errorResponse.error())
                    .as("Поле error должно содержать сообщение об ошибке")
                    .isNotBlank()
                    .containsIgnoringCase(ERROR_MISSING);
        });
    }

    @Test
    @DisplayName("Запрос без параметра startDate возвращает 400")
    @Description("""
            Цель: Проверка валидации обязательности параметра startDate.
            Дано: Передан только endDate, startDate отсутствует.
            Когда: Отправлен GET запрос на allMeters.
            Тогда: HTTP 400, success=false, сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenStartDateMissing() {

        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDATE_START(token, SINGLE_TARIFF_METER);

        step("Проверка структуры ответа об ошибке", () -> {
            assertThat(errorResponse.success())
                    .as("Поле success должно быть false")
                    .isFalse();

            assertThat(errorResponse.error())
                    .as("Поле error должно содержать сообщение об ошибке")
                    .isNotBlank()
                    .containsIgnoringCase(ERROR_MISSING);
        });
    }

    @Test
    @DisplayName("Запрос без параметра endDate возвращает 400")
    @Description("""
            Цель: Проверка валидации обязательности параметра endDate.
            Дано: Передан только startDate, endDate отсутствует.
            Когда: Отправлен GET запрос на allMeters.
            Тогда: HTTP 400, success=false, сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenEndDateMissing() {

        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDATE_END(token, SINGLE_TARIFF_METER);

        step("Проверка структуры ответа об ошибке", () -> {
            assertThat(errorResponse.success())
                    .as("Поле success должно быть false")
                    .isFalse();

            assertThat(errorResponse.error())
                    .as("Поле error должно содержать сообщение об ошибке")
                    .isNotBlank()
                    .containsIgnoringCase(ERROR_MISSING);
        });
    }

    @Test
    @DisplayName("Если endDate раньше startDate — возвращается пустой массив")
    @Description("""
            Цель: Проверка поведения API при некорректном диапазоне дат.
            Дано: startDate > endDate (логически неверный диапазон).
            Когда: Отправлен GET запрос на allMeters.
            Тогда: HTTP 200, тело ответа — пустой массив [].
            Примечание: API не возвращает ошибку, а просто не находит данных.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturnEmptyArrayWhenEndDateBeforeStartDate() {

        List<AllMetersResponseModel> response = api.allMeters.getAllMetersHistoryWithStartDateBeforeEndDate(token, SINGLE_TARIFF_METER);

        step("Проверка, что ответ — пустой массив", () -> {
            assertThat(response)
                    .as("При endDate < startDate API должен вернуть пустой массив")
                    .isEmpty();
        });
    }

    @Test
    @DisplayName("Запрос с пустыми строками в датах возвращает 400")
    @Description("""
            Цель: Проверка, что API не принимает пустые строки как валидные даты.
            Дано: Параметры startDate и endDate — пустые строки.
            Когда: Отправлен GET запрос на allMeters.
            Тогда: HTTP 400, success=false, сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400ForEmptyDateStrings() {

        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutEmptyDate(token, SINGLE_TARIFF_METER);

        step("Проверка структуры ответа об ошибке", () -> {
            assertThat(errorResponse.success())
                    .as("Поле success должно быть false")
                    .isFalse();

            assertThat(errorResponse.error())
                    .as("Поле error должно содержать сообщение об ошибке")
                    .isNotBlank()
                    .containsIgnoringCase(ERROR_MISSING);
        });
    }

    @Test
    @DisplayName("Запрос без id_pa возвращает 404")
    @Description("""
            Цель: Проверка, что API возвращает 404 при отсутствии path-параметра id_pa.
            Дано: Валидные даты, но id_pa не передан.
            Когда: Отправлен GET запрос на /cabinet/meters/allMeters/ (без id).
            Тогда: HTTP 404 Not Found.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturn404WhenIdPaMissing() {

        Response errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutIdPa(token);

        step("Проверка статуса ответа", () -> {
            assertThat(errorResponse.statusCode()).isEqualTo(404);
        });
    }
}
