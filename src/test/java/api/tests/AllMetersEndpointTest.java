package api.tests;

import api.helpers.AllMetersHelper;
import api.models.allMeters.ErrorResponseModel;
import api.models.login.LoginBodyModel;
import base.ApiTestBase;
import base.TestData;
import config.TestConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Показания")
@Feature("Получение информации о показаниях")
@Owner("Elena Yatsenko")
public class AllMetersEndpointTest extends ApiTestBase {

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
    @Description("Проверка корректной работы фильтрации по startDate и endDate: все возвращённые даты находятся в заданном диапазоне.")
    @Tags({@Tag("api"), @Tag("positive"), @Tag("regression"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnFilteredHistoryByDates() {
        var response = api.allMeters.getDeviceReadingsHistory(token, TestData.SINGLE_TARIFF_METER);

        step("Проверка, что история не пустая", () -> assertThat(response).isNotEmpty());
        AllMetersHelper.assertAllReadingDatesInRange(
                response,
                LocalDate.parse(TestData.DATE_START),
                LocalDate.parse(TestData.DATE_END)
        );
    }

    @ParameterizedTest(name = "Тарифы: {0}")
    @MethodSource("tariffData")
    @DisplayName("История показаний для ПУ с несколькими тарифами")
    @Description("Проверка, что API возвращает показания для всех тарифов (День/Ночь или Пик/Полупик/Ночь).")
    @Tags({@Tag("api"), @Tag("positive"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnHistoryForMultiTariffMeter(int meterId, String[] expectedTariffs) {
        var response = api.allMeters.getDeviceReadingsHistory(token, meterId);

        step("Проверка наличия всех тарифов", () -> {
            assertThat(response).as("История показаний не должна быть пустой").isNotEmpty();
            AllMetersHelper.assertTariffsPresent(response, expectedTariffs);
        });
    }

    @Test
    @DisplayName("История показаний для ПУ по водоснабжению")
    @Description("Проверка корректности данных для водяного счётчика — история не пустая.")
    @Tags({@Tag("api"), @Tag("positive"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnHistoryForWaterMeter() {
        var response = api.allMeters.getDeviceReadingsHistory(token, TestData.WATER_METER);
        step("Проверка структуры ответа для водяного ПУ", () ->
                assertThat(response).as("История показаний не должна быть пустой").isNotEmpty());
    }

    @Test
    @DisplayName("История показаний для ЛС без ПУ ФКР — пустой массив")
    @Description("API возвращает пустой массив для ЛС без приборов учёта ФКР.")
    @Tags({@Tag("api"), @Tag("positive"), @Tag("regression")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturnEmptyArrayForAccountWithoutFkrMeters() {
        var response = api.allMeters.getDeviceReadingsHistory(token, TestData.FKR_METER);
        step("Проверка, что ответ — пустой массив", () ->
                assertThat(response).as("Для ЛС без ПУ ФКР должен возвращаться пустой массив").isEmpty());
    }

    @Test
    @DisplayName("Если endDate раньше startDate — возвращается пустой массив")
    @Description("API не возвращает ошибку при некорректном диапазоне дат, а возвращает пустой массив.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturnEmptyArrayWhenEndDateBeforeStartDate() {
        var response = api.allMeters.getAllMetersHistoryWithStartDateBeforeEndDate(token, TestData.SINGLE_TARIFF_METER);
        step("Проверка, что ответ — пустой массив", () ->
                assertThat(response).as("При endDate < startDate API должен вернуть пустой массив").isEmpty());
    }

    @Test
    @DisplayName("Запрос без параметров startDate и endDate возвращает 400")
    @Description("API возвращает 400 и ошибку валидации при отсутствии обязательных параметров дат.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenDatesNotProvided() {
        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDates(token, TestData.SINGLE_TARIFF_METER);
        AllMetersHelper.assertErrorResponse(errorResponse, TestData.ERROR_MISSING);
    }

    @Test
    @DisplayName("Запрос без параметра startDate возвращает 400")
    @Description("API возвращает 400 и ошибку валидации при отсутствии startDate.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenStartDateMissing() {
        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDATE_START(token, TestData.SINGLE_TARIFF_METER);
        AllMetersHelper.assertErrorResponse(errorResponse, TestData.ERROR_MISSING);
    }

    @Test
    @DisplayName("Запрос без параметра endDate возвращает 400")
    @Description("API возвращает 400 и ошибку валидации при отсутствии endDate.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400WhenEndDateMissing() {
        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutDATE_END(token, TestData.SINGLE_TARIFF_METER);
        AllMetersHelper.assertErrorResponse(errorResponse, TestData.ERROR_MISSING);
    }

    @Test
    @DisplayName("Запрос с пустыми строками в датах возвращает 400")
    @Description("API возвращает 400 при передаче пустых строк вместо значений дат.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn400ForEmptyDateStrings() {
        ErrorResponseModel errorResponse = api.allMeters.getDeviceReadingsHistoryWithoutEmptyDate(token, TestData.SINGLE_TARIFF_METER);
        AllMetersHelper.assertErrorResponse(errorResponse, TestData.ERROR_MISSING);
    }

    @Test
    @DisplayName("Запрос без id_pa возвращает 404")
    @Description("API возвращает 404 при отсутствии path-параметра id_pa.")
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturn404WhenIdPaMissing() {
        Response response = api.allMeters.getDeviceReadingsHistoryWithoutIdPa(token);
        step("Проверка статуса ответа", () -> assertThat(response.statusCode()).isEqualTo(404));
    }

    static Stream<Arguments> tariffData() {
        return Stream.of(
                Arguments.of(TestData.TWO_TARIFF_METER, new String[]{"День", "Ночь"}),
                Arguments.of(TestData.THREE_TARIFF_METER, new String[]{"Пик", "Полупик", "Ночь"})
        );
    }
}
