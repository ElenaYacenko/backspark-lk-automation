package api.helpers;

import api.apiClients.ApiClient;
import api.models.allMeters.AllMetersResponseModel;
import api.models.meter.MeterBodyModel;
import base.TestData;
import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MeterTestHelper {

    private static final ApiClient api = new ApiClient();

    /**
     * Извлекает данные первого прибора и первого тарифа из истории показаний.
     */
    public static MeterData extractFirstMeterData(String token, int meterId) {
        List<AllMetersResponseModel> history = api.allMeters.getDeviceReadingsHistory(token, meterId);
        AllMetersResponseModel record = history.get(0);
        AllMetersResponseModel.Meter meter = record.meters().get(0);
        return new MeterData(record.idDevice(), meter.idTariff(), meter.reading(), meter.idIndication());
    }

    /**
     * Формирует тело запроса для однотарифного счётчика.
     */
    @Step("[API] Формирование тела запроса для однотарифного счётчика")
    public static MeterBodyModel buildSubmissionRequest(Long idDevice, Long idTariff, Long reading, Long idIndication) {
        return new MeterBodyModel(
                idDevice,
                TestData.VALID_RECEIVING_METHOD,
                List.of(new MeterBodyModel.Rate(idTariff, reading, idIndication))
        );
    }

    /**
     * Формирует тело запроса для мульти-тарифного счётчика.
     */
    @Step("[API] Формирование тела запроса для мульти-тарифного счётчика")
    public static MeterBodyModel buildMultiTariffRequest(Long idDevice, List<RateData> rates) {
        List<MeterBodyModel.Rate> rateList = rates.stream()
                .map(r -> new MeterBodyModel.Rate(r.idTariff(), r.reading, r.idIndication))
                .collect(Collectors.toList());
        return new MeterBodyModel(idDevice, TestData.VALID_RECEIVING_METHOD, rateList);
    }

    /**
     * Проверяет успешный ответ API.
     */
    public static void assertSuccessResponse(String response) {
        assertThat(response)
                .as("Ответ API должен содержать сообщение об успехе")
                .isNotBlank()
                .contains(TestData.SUCCESS_SUBMISSION_MESSAGE);
    }

    /**
     * Данные первого прибора и тарифа.
     */
    public record MeterData(Long idDevice, Long idTariff, Long reading, Long idIndication) {}

    /**
     * Данные тарифа для мульти-тарифного запроса.
     */
    public record RateData(Long idTariff, Long reading, Long idIndication) {}
}
