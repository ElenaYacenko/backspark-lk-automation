package api.tests;

import base.TestBase;
import config.TestConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import api.models.devices.DevicesResponseModel;
import api.models.login.LoginBodyModel;
import org.junit.jupiter.api.*;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static base.TestData.*;

@Epic("Приборы учёта")
@Feature("Получение информации о ПУ")
@Owner("Elena Yatsenko")

public class DevicesEndpointTest extends TestBase {
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
    @DisplayName("Получение информации о ПУ, однотарифный счетчик")
    @Description("""
            Цель: Проверка получения корректной информации о ПУ, его тарифов и показаниях.
            Дано: ЛС с однотарифным ПУ.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 200, у прибора есть тариф 'Сутки'.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnSingleTariffForValidAccount() {

        DevicesResponseModel response = api.devices.userDevicesInfo(token, SINGLE_TARIFF_METER);

        step("Проверка схемы ответа, в ответе тариф 'Сутки'", () -> {
            assertThat(response.coreDevices())
                    .as("Должен быть хотя бы один прибор учёта")
                    .isNotEmpty();

            assertThat(response.coreDevices().get(0).meters())
                    .as("Должен быть хотя бы один тариф")
                    .isNotEmpty();

            assertThat(response.coreDevices().get(0).meters().get(0).title())
                    .as("Название тарифа должно быть 'Сутки'")
                    .isEqualTo("Сутки");
        });
    }

    @Test
    @DisplayName("Получение информации о ПУ, двутарифный счетчик")
    @Description("""
            Цель: Проверка получения корректной информации о двутарифном ПУ, его тарифов и показаниях.
            Дано: ЛС с двутарифным ПУ.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 200, у прибора ровно 2 тарифа: 'День' и 'Ночь'.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnTwoTariffsForDualTariffMeter() {

        DevicesResponseModel response = api.devices.userDevicesInfo(token, TWO_TARIFF_METER);

        step("Проверка базовой структуры ответа", () -> {
            assertThat(response.coreDevices())
                    .as("Должен быть ровно 1 прибор учёта в ответе")
                    .hasSize(1);
        });
        step("Проверка наличия двух тарифов 'День' и 'Ночь'", () -> {
            var meters = response.coreDevices().get(0).meters();

            assertThat(meters)
                    .as("У прибора должно быть ровно 2 тарифа")
                    .hasSize(2);

            assertThat(meters.get(0).title())
                    .as("Название первого тарифа")
                    .isEqualTo("День");

            assertThat(meters.get(1).title())
                    .as("Название второго тарифа")
                    .isEqualTo("Ночь");
        });
    }

    @Test
    @DisplayName("Получение информации о ПУ, трехтарифный счетчик")
    @Description("""
            Цель: Проверка получения корректной информации о трехтарифном ПУ, его тарифов и показаниях.
            Дано: ЛС с трехтарифным ПУ.
            Когда: Отправлен GET запрос на эндпоинт meters/devices.
            Тогда: HTTP статус 200, у прибора ровно 3 тарифа: 'Пик', 'Полупик' и 'Ночь'.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldReturnThreeTariffsForTripleTariffMeter() {

        DevicesResponseModel response = api.devices.userDevicesInfo(token, THREE_TARIFF_METER);

        step("Проверка базовой структуры ответа", () -> {
            assertThat(response.coreDevices())
                    .as("Должен быть ровно 1 прибор учёта в ответе")
                    .hasSize(1);
        });
        step("Проверка наличия трех тарифов 'Пик', 'Полупик' и 'Ночь'", () -> {
            var meters = response.coreDevices().get(0).meters();

            assertThat(meters)
                    .as("У прибора должно быть ровно 3 тарифа")
                    .hasSize(3);

            assertThat(meters.get(0).title())
                    .as("Название первого тарифа")
                    .isEqualTo("Пик");

            assertThat(meters.get(1).title())
                    .as("Название второго тарифа")
                    .isEqualTo("Полупик");

            assertThat(meters.get(2).title())
                    .as("Название третьего тарифа")
                    .isEqualTo("Ночь");
        });
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

        step("Проверка наличия приборов учёта", () -> {
            assertThat(response.coreDevices())
                    .as("Список приборов учёта не должен быть пустым")
                    .isNotEmpty();
        });

        step("Проверка целостности данных по всем тарифам", () -> {
            assertThat(response.coreDevices())
                    .as("У каждого прибора должен быть список тарифов")
                    .allSatisfy(device -> assertThat(device.meters()).isNotEmpty());
        });
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

        step("Проверка содержимого ответа", () -> {
            assertThat(response.number())
                    .as("Номер ЛС должен быть пустой строкой")
                    .isEqualTo("");

            assertThat(response.coreDevices())
                    .as("Список приборов учёта должен быть null")
                    .isNull();
        });
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

        step("Проверка статуса и структуры ответа", () -> {
            assertThat(response.statusCode())
                    .as("HTTP статус должен быть 401")
                    .isEqualTo(401);

            assertThat(response.jsonPath().getString("message"))
                    .as("Тело ответа должно содержать сообщение об ошибке")
                    .isNotBlank();
        });
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

        step("Проверка статуса и структуры ответа", () -> {
            assertThat(response.statusCode())
                    .as("HTTP статус должен быть 404")
                    .isEqualTo(404);
        });
    }
}
