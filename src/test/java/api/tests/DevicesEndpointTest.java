package api.tests;

import api.helpers.DevicesResponseHelper;
import api.models.devices.DevicesResponseModel;
import api.models.login.LoginBodyModel;
import base.ApiTestBase;
import config.TestConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static base.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Приборы учёта")
@Feature("Получение информации о ПУ")
@Owner("Elena Yatsenko")
public class DevicesEndpointTest extends ApiTestBase {
    private String token;

    @BeforeEach
    void login() {
        LoginBodyModel loginData = new LoginBodyModel(
                TestConfig.getTestUserPhone(),
                TestConfig.getTestUserPassword()
        );
        token = api.auth.loginAndGetToken(loginData);
    }

    @ParameterizedTest(name = "ПУ={0}, тарифы: {1}")
    @MethodSource("singleMeterTariffScenarios")
    @DisplayName("Получение информации о ПУ с фиксированным набором тарифов")
    @Description("""
            Цель: Проверка получения корректной информации о ПУ, его тарифов и показаниях.
            Дано: ЛС с однотарифным ПУ.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 200, у прибора есть тариф 'Сутки'.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnCorrectTariffsForMeter(int meterId, String[] expectedTariffs) {
        DevicesResponseModel response = api.devices.userDevicesInfo(token, meterId);

        DevicesResponseHelper.assertHasAtLeastOneDevice(response);
        DevicesResponseHelper.assertFirstDeviceHasExactlyNMeters(response, expectedTariffs.length);
        DevicesResponseHelper.assertFirstDeviceHasMetersWithTitleInOrder(response, expectedTariffs);
    }

    @Test
    @DisplayName("Получение информации о ПУ по водоснабжению")
    @Description("""
            Цель: Проверка получения корректной информации о ПУ по водоснабжению, его тарифов и показаниях.
            Дано: ЛС водоснабжения.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 200, у каждого ПУ есть непустой список тарифов.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnWaterMeterDevicesForValidAccount() {
        DevicesResponseModel response = api.devices.userDevicesInfo(token, WATER_METER);

        DevicesResponseHelper.assertHasAtLeastOneDevice(response);
        DevicesResponseHelper.assertAllDevicesHaveMeters(response);
    }

    @Test
    @DisplayName("Получение информации о приборах учёта для лицевого счёта без привязанных ПУ")
    @Description("""
            Цель: Убедиться, что API корректно обрабатывает отсутствие приборов учёта и возвращает ожидаемую пустую структуру данных, а не ошибку сервера.
            Дано: Авторизованный пользователь с лицевым счётом, к которому не привязан ни один прибор учёта (ПУ).
            Когда: Отправлен GET-запрос на эндпоинт получения информации о ПУ (/meters/devices/{id_pa}).
            Тогда: Возвращается HTTP-статус 200, поле number является пустой строкой, а поле core_devices равно null.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn401WhenAccountNotLinkedToLkk() {
        DevicesResponseModel response = api.devices.userEmptyDevicesInfo(token, EMPTY_METER);
        DevicesResponseHelper.assertEmptyDevicesStructure(response);
    }

    @Test
    @DisplayName("Запрос ЛС, не добавленного в ЛКК, возвращает 401")
    @Description("""
            Цель: Проверка, что API отклоняет запросы к ЛС, которые не привязаны к ЛКК пользователя.
            Дано: Авторизованный пользователь. ЛС существует в системе, но не добавлен в ЛКК.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 401 Unauthorized, тело ответа содержит поле message.
            Примечание: Это ожидаемое поведение — доступ к ЛС возможен только через ЛКК.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturn401WhenAccountNotLinkedToLKK() {
        Response response = api.devices.accountNotInLKK(token, OTHER_USER_ACCOUNT);

        assertThat(response.statusCode())
                .as("HTTP статус должен быть 401")
                .isEqualTo(401);

        assertThat(response.jsonPath().getString("message"))
                .as("Тело ответа должно содержать сообщение об ошибке")
                .isNotBlank();
    }

    @Test
    @DisplayName("Запрос без параметра id_pa возвращает 404")
    @Description("""
            Цель: Проверка, что API корректно обрабатывает отсутствие обязательного параметра.
            Дано: Авторизованный пользователь.
            Когда: Отправлен GET запрос на эндпоинт meters/devices БЕЗ параметра id_pa.
            Тогда: HTTP статус 404 (маршрут без обязательного сегмента пути не существует).
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.NORMAL)
    void shouldReturn404WhenIdPaIsMissing() {
        Response response = api.devices.errorMissingParameter(token);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    static Stream<Arguments> singleMeterTariffScenarios() {
        return Stream.of(
                Arguments.of(SINGLE_TARIFF_METER, new String[]{"Сутки"}),
                Arguments.of(TWO_TARIFF_METER, new String[]{"День", "Ночь"}),
                Arguments.of(THREE_TARIFF_METER, new String[]{"Пик", "Полупик", "Ночь"})
        );
    }
}
