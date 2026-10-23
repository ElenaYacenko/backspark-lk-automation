package api.tests;

import base.TestBase;
import config.TestConfig;
import io.qameta.allure.*;
import api.models.allMeters.AllMetersResponseModel;
import api.models.login.LoginBodyModel;
import api.models.meter.MeterBodyModel;
import api.models.userAccount.UsersAccountResponseModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static base.TestData.*;

@Epic("Показания")
@Feature("Передача показаний")
@Owner("Elena Yatsenko")

public class MeterSubmissionTest extends TestBase {

    public static final String INVALID_READINGS_ERROR_MESSAGE =
            "Ошибка сохранения показания. Проверьте корректность переданных данных.";
    public static final String SUCCESS_SUBMISSION_MESSAGE = "Показания переданы успешно";
    public static final Long READING_INCREMENT = 10L;
    public static final Integer VALID_RECEIVING_METHOD = 41; // ЛКК



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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });
        Long newReading = step("Расчет новых показаний", () -> firstMeter.reading() + READING_INCREMENT
        );

        MeterBodyModel requestBody = step("Формирование тела запроса", () -> new MeterBodyModel(
                        firstRecord.idDevice(),
                        VALID_RECEIVING_METHOD,
                        List.of(new MeterBodyModel.Rate(
                                firstMeter.idTariff(),
                                newReading,
                                firstMeter.idIndication()
                        ))
                )
        );

        String response = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitMeterReadings(token, requestBody)
        );
        step("Проверка успешного ответа", () -> {
            assertThat(response)
                    .as("Ответ API должен содержать сообщение об успехе")
                    .isNotBlank()
                    .contains(SUCCESS_SUBMISSION_MESSAGE);
        });
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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, TWO_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter dayMeter = step("Извлечение данных тарифа 'День'",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });
        AllMetersResponseModel.Meter nightMeter = step("Извлечение данных тарифа 'Ночь'",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(1);
                    return meter;
                });
        Long newDayReading = step("Расчет новых показаний 'День'", () -> dayMeter.reading() + READING_INCREMENT
        );
        Long newNightReading = step("Расчет новых показаний 'Ночь'", () -> nightMeter.reading() + READING_INCREMENT
        );

        MeterBodyModel requestBody = step("Формирование тела запроса", () -> new MeterBodyModel(
                        firstRecord.idDevice(),
                        VALID_RECEIVING_METHOD,
                        List.of(
                                new MeterBodyModel.Rate(
                                        dayMeter.idTariff(),
                                        newDayReading,
                                        dayMeter.idIndication()),
                                new MeterBodyModel.Rate(
                                        nightMeter.idTariff(),
                                        newNightReading,
                                        nightMeter.idIndication()
                                ))
                )
        );

        String response = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitMeterReadings(token, requestBody)
        );
        step("Проверка успешного ответа", () -> {
            assertThat(response)
                    .as("Ответ API должен содержать сообщение об успехе")
                    .isNotBlank()
                    .contains(SUCCESS_SUBMISSION_MESSAGE);
        });
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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, THREE_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter peakMeter = step("Извлечение данных тарифа 'Пик'",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });
        AllMetersResponseModel.Meter halfPeakMeter = step("Извлечение данных тарифа 'Полупик'",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(1);
                    return meter;
                });
        AllMetersResponseModel.Meter nightMeter = step("Извлечение данных тарифа 'Ночь'",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(2);
                    return meter;
                });
        Long newPeakReading = step("Расчет новых показаний 'Пик'", () -> peakMeter.reading() + READING_INCREMENT
        );
        Long newhalfPeakReading = step("Расчет новых показаний 'Полупик'", () -> halfPeakMeter.reading() + READING_INCREMENT
        );
        Long newNightReading = step("Расчет новых показаний 'Ночь'", () -> nightMeter.reading() + READING_INCREMENT
        );

        MeterBodyModel requestBody = step("Формирование тела запроса", () -> new MeterBodyModel(
                        firstRecord.idDevice(),
                        VALID_RECEIVING_METHOD,
                        List.of(
                                new MeterBodyModel.Rate(
                                        peakMeter.idTariff(),
                                        newPeakReading,
                                        peakMeter.idIndication()),
                                new MeterBodyModel.Rate(
                                        halfPeakMeter.idTariff(),
                                        newhalfPeakReading,
                                        halfPeakMeter.idIndication()),
                                new MeterBodyModel.Rate(
                                        nightMeter.idTariff(),
                                        newNightReading,
                                        nightMeter.idIndication()
                                ))
                )
        );

        String response = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitMeterReadings(token, requestBody)
        );
        step("Проверка успешного ответа", () -> {
            assertThat(response)
                    .as("Ответ API должен содержать сообщение об успехе")
                    .isNotBlank()
                    .contains(SUCCESS_SUBMISSION_MESSAGE);
        });
    }

    @Test
    @DisplayName("Успешная передача показаний счетчика воды")
    @Description("""
            Цель: Проверка успешной передачи показаний прибора учёта воды.
            Дано: Авторизованный пользователь, ЛС с ПУ по водоснабжению.
            Когда: Отправлен POST запрос на /meters/meter с корректным телом запроса.
            Тогда: HTTP статус 200, возвращено сообщение об успешной передаче.
            Примечание: Показания воды могут быть дробными (интерполяция), поэтому используется BigDecimal.
            """)
    @Tags({@Tag("api"), @Tag("regression"), @Tag("positive"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldSuccessfullySubmitWaterMeterReadings() {
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, WATER_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });
        Long newReading = step("Расчет новых показаний", () -> firstMeter.reading() + READING_INCREMENT
        );

        MeterBodyModel requestBody = step("Формирование тела запроса", () -> new MeterBodyModel(
                        firstRecord.idDevice(),
                        VALID_RECEIVING_METHOD,
                        List.of(new MeterBodyModel.Rate(
                                firstMeter.idTariff(),
                                newReading,
                                firstMeter.idIndication()
                        ))
                )
        );

        String response = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitMeterReadings(token, requestBody)
        );
        step("Проверка успешного ответа", () -> {
            assertThat(response)
                    .as("Ответ API должен содержать сообщение об успехе")
                    .isNotBlank()
                    .contains(SUCCESS_SUBMISSION_MESSAGE);
        });
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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });
        Long previousReading = firstMeter.reading();

        MeterBodyModel requestBody = step("Формирование тела запроса с показаниями, равными предыдущим", () ->
                new MeterBodyModel(
                        firstRecord.idDevice(),
                        VALID_RECEIVING_METHOD,
                        List.of(new MeterBodyModel.Rate(
                                firstMeter.idTariff(),
                                previousReading,
                                firstMeter.idIndication()
                        ))
                )
        );

        String response = step("Отправка POST-запроса на передачу показаний", () ->
                api.meter.submitMeterReadings(token, requestBody)
        );
        step("Проверка успешного ответа", () -> {
            assertThat(response)
                    .as("Ответ API должен содержать сообщение об успехе")
                    .isNotBlank()
                    .contains(SUCCESS_SUBMISSION_MESSAGE);
        });
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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });

        MeterBodyModel requestBody = new MeterBodyModel(
                firstRecord.idDevice(),
                VALID_RECEIVING_METHOD,
                List.of(new MeterBodyModel.Rate(
                        firstMeter.idTariff(),
                        Long.parseLong(readingValue),
                        firstMeter.idIndication()
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
        List<AllMetersResponseModel> historyResponse = step("Получение истории показаний для подготовки данных",
                () -> api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER));
        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });

        String requestBody = rawJsonBody.formatted(firstRecord.idDevice(), VALID_RECEIVING_METHOD, firstMeter.idTariff());

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

        Long nonExistentIdDevice = 999999999L;

        List<AllMetersResponseModel> historyResponse = api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER);

        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });

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
                """.formatted(nonExistentIdDevice, VALID_RECEIVING_METHOD, firstMeter.idTariff());

        String errorResponse = step(
                "Отправка POST-запроса с несуществующим id_device=" + nonExistentIdDevice,
                () -> api.meter.submitInvalidReadingsRaw(token, requestBody)
        );

        step("Проверка ошибки в ответе", () -> {
            assertThat(errorResponse)
                    .as("Ответ должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(INVALID_READINGS_ERROR_MESSAGE);
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

        Long nonExistentIdTariff = 999999999L;

        List<AllMetersResponseModel> historyResponse = api.allMeters.getDeviceReadingsHistory(token, SINGLE_TARIFF_METER);

        AllMetersResponseModel firstRecord = step("Извлечение данных первого прибора из истории",
                () -> {
                    AllMetersResponseModel record = historyResponse.get(0);
                    return record;
                });
        AllMetersResponseModel.Meter firstMeter = step("Извлечение данных первого тарифа",
                () -> {
                    AllMetersResponseModel.Meter meter = firstRecord.meters().get(0);
                    return meter;
                });

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
                """.formatted(firstRecord.idDevice(), VALID_RECEIVING_METHOD, nonExistentIdTariff);

        String errorResponse = step(
                "Отправка POST-запроса с несуществующим id_tariff=" + nonExistentIdTariff,
                () -> api.meter.submitInvalidReadingsRaw(token, requestBody)
        );

        step("Проверка ошибки в ответе", () -> {
            assertThat(errorResponse)
                    .as("Ответ должен содержать сообщение об ошибке")
                    .isNotBlank()
                    .contains(INVALID_READINGS_ERROR_MESSAGE);
        });
    }
}
