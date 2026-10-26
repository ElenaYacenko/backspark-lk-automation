package api.helpers;

import api.models.devices.DevicesResponseModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Хелпер для проверки ответов API приборов учёта (devices).
 * Содержит вспомогательные методы для валидации структуры DevicesResponseModel.
 */
public class DevicesResponseHelper {

    /**
     * Проверяет, что в ответе есть хотя бы один прибор учёта.
     */
    public static void assertHasAtLeastOneDevice(DevicesResponseModel response) {
        assertThat(response.coreDevices())
                .as("Должен быть хотя бы один прибор учёта")
                .isNotEmpty();
    }

    /**
     * Проверяет, что у первого прибора есть хотя бы один тариф.
     */
    public static void assertFirstDeviceHasAtLeastOneMeter(DevicesResponseModel response) {
        assertThat(response.coreDevices().get(0).meters())
                .as("У первого прибора должен быть хотя бы один тариф")
                .isNotEmpty();
    }

    /**
     * Проверяет, что у первого прибора ровно N тарифов.
     */
    public static void assertFirstDeviceHasExactlyNMeters(DevicesResponseModel response, int expectedCount) {
        assertThat(response.coreDevices().get(0).meters())
                .as("У первого прибора должно быть ровно %d тарифов", expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * Проверяет, что у первого прибора ровно один тариф с указанным названием.
     */
    public static void assertFirstDeviceHasSingleTariffWithTitle(DevicesResponseModel response, String expectedTitle) {
        assertThat(response.coreDevices().get(0).meters().get(0).title())
                .as("Название тарифа должно быть '%s'", expectedTitle)
                .isEqualTo(expectedTitle);
    }

    /**
     * Проверяет, что у первого прибора тарифы имеют указанные названия в указанном порядке.
     */
    public static void assertFirstDeviceHasMetersWithTitleInOrder(DevicesResponseModel response, String... expectedTitles) {
        List<String> actualTitles = response.coreDevices().get(0).meters().stream()
                .map(DevicesResponseModel.Meter::title)
                .toList();

        assertThat(actualTitles)
                .as("Тарифы первого прибора должны быть: %s", String.join(", ", expectedTitles))
                .containsExactly(expectedTitles);
    }

    /**
     * Проверяет, что у каждого прибора есть непустой список тарифов.
     */
    public static void assertAllDevicesHaveMeters(DevicesResponseModel response) {
        assertThat(response.coreDevices())
                .as("У каждого прибора должен быть список тарифов")
                .allSatisfy(device -> assertThat(device.meters()).isNotEmpty());
    }

    /**
     * Проверяет, что номер ЛС пустой и coreDevices == null.
     */
    public static void assertEmptyDevicesStructure(DevicesResponseModel response) {
        assertThat(response.number())
                .as("Номер ЛС должен быть пустой строкой")
                .isEqualTo("");

        assertThat(response.coreDevices())
                .as("Список приборов учёта должен быть null")
                .isNull();
    }
}
