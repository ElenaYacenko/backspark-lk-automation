package api.tests;

import api.helpers.MeterTestHelper;
import api.models.allMeters.AllMetersResponseModel;
import api.models.login.LoginBodyModel;
import api.models.meter.MeterBodyModel;
import base.ApiTestBase;
import base.TestData;
import config.TestConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Показания")
@Feature("Передача показаний")
@Owner("Elena Yatsenko")

public class MeterSubmissionTest extends ApiTestBase {

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
    @DisplayName("Успешная передача показаний счетчика")
    @Description("""
            Цель: Проверка успешной передачи показаний прибора учета.
            Дано: Авторизованный пользователь и валидные данные показаний (ID устройства, тарифа и показания).
            Когда: Отправлен POST запрос на /meters/meter с корректным телом запроса.
            Тогда: HTTP статус 200, возвращено текстовое сообщение об успешной передаче.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitMeterReadings() {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        Long newReading = meterData.reading() + TestData.READING_INCREMENT;
        MeterBodyModel requestBody = step("Формирование тела запроса",
                () -> MeterTestHelper.buildSubmissionRequest(
                        meterData.idDevice(), meterData.idTariff(), newReading, meterData.idIndication()));

        String response = step("Отправка показаний",
                () -> api.meter.submitMeterReadings(token, requestBody));
        MeterTestHelper.assertSuccessResponse(response);
    }

    @Test
    @DisplayName("Успешная передача показаний двухтарифного счетчика (День/Ночь)")
    @Description("""
            Цель: Проверка успешной передачи показаний двухтарифного прибора учета.
            Дано: Авторизованный пользователь, ЛС с двухтарифным ПУ.
            Когда: Отправлен POST запрос на /meters/meter с показаниями для обоих тарифов.
            Тогда: HTTP статус 200, возвращено сообщение об успешной передаче.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitTwoTariffMeterReadings() {
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний",
                () -> api.allMeters.getDeviceReadingsHistory(token, TestData.TWO_TARIFF_METER));
        AllMetersResponseModel record = historyResponse.get(0);

        MeterBodyModel requestBody = step("Формирование тела запроса",
                () -> MeterTestHelper.buildMultiTariffRequest(record.idDevice(), List.of(
                        new MeterTestHelper.RateData(
                                record.meters().get(0).idTariff(),
                                record.meters().get(0).reading() + TestData.READING_INCREMENT,
                                record.meters().get(0).idIndication()),
                        new MeterTestHelper.RateData(
                                record.meters().get(1).idTariff(),
                                record.meters().get(1).reading() + TestData.READING_INCREMENT,
                                record.meters().get(1).idIndication())
                )));

        String response = step("Отправка показаний",
                () -> api.meter.submitMeterReadings(token, requestBody));
        MeterTestHelper.assertSuccessResponse(response);
    }

    @Test
    @DisplayName("Успешная передача показаний трёхтарифного счетчика (Пик/Полупик/Ночь)")
    @Description("""
            Цель: Проверка успешной передачи показаний трёхтарифного прибора учета.
            Дано: Авторизованный пользователь, ЛС с трёхтарифным ПУ (Пик, Полупик, Ночь).
            Когда: Отправлен POST запрос на /meters/meter с показаниями для всех трёх тарифов.
            Тогда: HTTP статус 200, возвращено сообщение об успешной передаче.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitThreeTariffMeterReadings() {
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний",
                () -> api.allMeters.getDeviceReadingsHistory(token, TestData.THREE_TARIFF_METER));
        AllMetersResponseModel record = historyResponse.get(0);

        MeterBodyModel requestBody = step("Формирование тела запроса",
                () -> MeterTestHelper.buildMultiTariffRequest(record.idDevice(), List.of(
                        new MeterTestHelper.RateData(
                                record.meters().get(0).idTariff(),
                                record.meters().get(0).reading() + TestData.READING_INCREMENT,
                                record.meters().get(0).idIndication()),
                        new MeterTestHelper.RateData(
                                record.meters().get(1).idTariff(),
                                record.meters().get(1).reading() + TestData.READING_INCREMENT,
                                record.meters().get(1).idIndication()),
                        new MeterTestHelper.RateData(
                                record.meters().get(2).idTariff(),
                                record.meters().get(2).reading() + TestData.READING_INCREMENT,
                                record.meters().get(2).idIndication())
                )));

        String response = step("Отправка показаний",
                () -> api.meter.submitMeterReadings(token, requestBody));
        MeterTestHelper.assertSuccessResponse(response);
    }

    @Test
    @DisplayName("Успешная передача показаний счетчика воды")
    @Description("""
            Цель: Проверка успешной передачи показаний прибора учёта воды.
            Дано: Авторизованный пользователь, ЛС с ПУ по водоснабжению.
            Когда: Отправлен POST запрос на /meters/meter с корректным телом запроса.
            Тогда: HTTP с��атус 200, возвращено сообщение об успешной передаче.
            Примечание: Показания воды могут быть дробными (интерполяция), поэтому используется BigDecimal.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitWaterMeterReadings() {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.WATER_METER));

        Long newReading = meterData.reading() + TestData.READING_INCREMENT;
        MeterBodyModel requestBody = step("Формирование тела запроса",
                () -> MeterTestHelper.buildSubmissionRequest(
                        meterData.idDevice(), meterData.idTariff(), newReading, meterData.idIndication()));

        String response = step("Отправка показаний",
                () -> api.meter.submitMeterReadings(token, requestBody));
        MeterTestHelper.assertSuccessResponse(response);
    }

    @Test
    @DisplayName("Успешная передача показаний, равных предыдущим")
    @Description("""
            Цель: Проверка, что API позволяет передавать показания, равные предыдущим.
            Дано: Авторизованный пользователь, ЛС с однотарифным ПУ. Последние показания получены из истории.
            Когда: Отправлен POST запрос на /meters/meter с показаниями, равными последним переданным.
            Тогда: HTTP статус 200, возвращено сообщение об успешной передаче.
            Бизнес-правило: Показания могут быть равны предыдущим (не обязательно строго больше).
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitReadingsEqualToPrevious() {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        MeterBodyModel requestBody = step("Формирование тела запроса с показаниями, равными предыдущим",
                () -> MeterTestHelper.buildSubmissionRequest(
                        meterData.idDevice(), meterData.idTariff(), meterData.reading(), meterData.idIndication()));

        String response = step("Отправка показаний",
                () -> api.meter.submitMeterReadings(token, requestBody));
        MeterTestHelper.assertSuccessResponse(response);
    }

    @ParameterizedTest(name = "Показания={0}, ожидаемая ошибка={1}")
    @MethodSource("invalidReadingScenarios")
    @DisplayName("Негативные тесты: валидация показаний счетчика")
    @Description("""
            Цель: Проверка, что API корректно отклоняет невалидные показания.
            Дано: Авторизованный пользователь, валидный id_device и id_tariff.
            Когда: Отправлен POST запрос на /meters/meter с невалидными показаниями.
            Тогда: HTTP статус 400, возвращено сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldRejectInvalidReadings(String readingValue, String expectedError) {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        MeterBodyModel requestBody = new MeterBodyModel(
                meterData.idDevice(),
                TestData.VALID_RECEIVING_METHOD,
                List.of(new MeterBodyModel.Rate(
                        meterData.idTariff(),
                        Long.parseLong(readingValue),
                        meterData.idIndication()
                ))
        );

        String errorResponse = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitInvalidReadings(token, requestBody)
        );

        step("Проверка ошибки в ответе", () -> {
            assertThat(errorResponse)
                    .as("Ответ API должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(expectedError);
        });
    }

    static Stream<Arguments> invalidReadingScenarios() {
        return Stream.of(
                Arguments.of("123456789", "Показание превышает допустимое количество символов: 8"),
                Arguments.of("-10", "Показание не может быть отрицательным"),
                Arguments.of("0", "Показание не может быть равным нулю"),
                Arguments.of("1", "Показание не может быть меньше предыдущего")
        );
    }

    @ParameterizedTest(name = "Сценарий: {0}")
    @MethodSource("invalidStructureScenarios")
    @DisplayName("Негативные тесты: валидация структуры запроса")
    @Description("""
            Цель: Проверка, что API отклоняет запросы с невалидной структурой тела.
            Дано: Авторизованный пользователь, валидный id_device и id_tariff.
            Когда: Отправлен POST запрос на /meters/meter с невалидной структурой JSON.
            Тогда: HTTP статус 400, возвращено сообщение об ошибке.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldRejectInvalidRequestStructure(String scenarioName, String errorName, String rawJsonBody) {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        String requestBody = rawJsonBody.formatted(meterData.idDevice(), TestData.VALID_RECEIVING_METHOD, meterData.idTariff());

        String errorResponse = step(
                "Отправка POST-запроса со сценарием: " + scenarioName,
                () -> api.meter.submitInvalidReadingsRaw(token, requestBody)
        );

        step("Проверка ответа для сценария: " + scenarioName, () -> {
            assertThat(errorResponse)
                    .as("Ответ для сценария '%s' должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(errorName);
        });
    }

    static Stream<Arguments> invalidStructureScenarios() {
        return Stream.of(
                Arguments.of(
                        "Пустой массив rates", "Показания отсутствуют",
                        """
                                {
                                    "id_device": %d,
                                    "id_receiving_method": %d,
                                    "rates": []
                                }
                                """
                ),
                Arguments.of(
                        "Отсутствует поле reading", "Показание не может быть пустым",
                        """
                                {
                                    "id_device": %d,
                                    "id_receiving_method": %d,
                                    "rates": [
                                        {
                                            "id_tariff": %d,
                                            "id_indication": 0
                                        }
                                    ]
                                }
                                """
                ),
                Arguments.of(
                        "Неверный тип поля reading (строка вместо числа)", "Некорректный формат данных в запросе",
                        """
                                {
                                    "id_device": %d,
                                    "id_receiving_method": %d,
                                    "rates": [
                                        {
                                            "id_tariff": %d,
                                            "reading": "abc",
                                            "id_indication": 0
                                        }
                                    ]
                                }
                                """
                )
        );
    }

    @Test
    @DisplayName("Передача показаний для несуществующего id_device")
    @Description("""
            Цель: Проверка, что API отклоняет запрос с несуществующим id_device.
            Дано: Авторизованный пользователь, несуществующий id_device.
            Когда: Отправлен POST запрос на /meters/meter с id_device = 999999999.
            Тогда: HTTP статус не 200 (400 или 404), запрос отклонён.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("negative")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldRejectNonExistentIdDevice() {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        Long nonExistentIdDevice = 999999999L;

        String requestBody = """
                {
                    "id_device": %d,
                    "id_receiving_method": %d,
                    "rates": [
                        {
                            "id_tariff": %d,
                            "reading": 12345,
                            "id_indication": 0
                        }
                    ]
                }
                """.formatted(nonExistentIdDevice, TestData.VALID_RECEIVING_METHOD, meterData.idTariff());

        String errorResponse = step(
                "Отправка POST-запроса с несуществующим id_device=" + nonExistentIdDevice,
                () -> api.meter.submitInvalidReadingsRaw(token, requestBody)
        );

        step("Проверка ошибки в ответе", () -> {
            assertThat(errorResponse)
                    .as("Ответ должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(TestData.INVALID_READINGS_ERROR_MESSAGE);
        });
    }

    @Test
    @DisplayName("Передача показаний для несуществующего id_tariff")
    @Description("""
            Цель: Проверка, что API отклоняет запрос с несуществующим id_tariff.
            Дано: Авторизованный пользователь, несуществующий id_tariff.
            Когда: Отправлен POST запрос на /meters/meter с id_tariff = 999999999.
            Тогда: HTTP статус не 200 (400 или 404), запрос отклонён.
            """)
    @Tags({@Tag("api"), @Tag("negative"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldRejectNonExistentIdTariff() {
        MeterTestHelper.MeterData meterData = step("Получение данных ПУ",
                () -> MeterTestHelper.extractFirstMeterData(token, TestData.SINGLE_TARIFF_METER));

        Long nonExistentIdTariff = 999999999L;

        String requestBody = """
                {
                    "id_device": %d,
                    "id_receiving_method": %d,
                    "rates": [
                        {
                            "id_tariff": %d,
                            "reading": 12345,
                            "id_indication": 0
                        }
                    ]
                }
                """.formatted(meterData.idDevice(), TestData.VALID_RECEIVING_METHOD, nonExistentIdTariff);

        String errorResponse = step(
                "Отправка POST-запроса с несуществующим id_tariff=" + nonExistentIdTariff,
                () -> api.meter.submitInvalidReadingsRaw(token, requestBody)
        );

        step("Проверка ошибки в ответе", () -> {
            assertThat(errorResponse)
                    .as("Ответ должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(TestData.INVALID_READINGS_ERROR_MESSAGE);
        });
    }
}
