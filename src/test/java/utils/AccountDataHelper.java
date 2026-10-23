package utils;

import api.apiClients.ApiClient;
import api.models.devices.DevicesResponseModel;
import api.models.login.LoginBodyModel;
import api.models.userAccount.UsersAccountResponseModel;
import config.TestConfig;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.qameta.allure.Allure.step;

public class AccountDataHelper {

    private final ApiClient api;

    public AccountDataHelper(ApiClient api) {
        this.api = api;
    }

    /**
     * Record для хранения извлеченных данных об активном счете и его приборе учета.
     **/
    public record ActiveAccountData(
            String accountNumber,
            Integer idPa,
            String deviceTitle,
            String modification,
            String serialNumber,
            String formattedDateCheckNext,
            String lastReading,
            String tariffTitle
    ) {
    }

    /**
     * Получаем данные первого активного лицевого счета и его первого прибора учета.
     **/
    public ActiveAccountData getFirstActiveAccountData() {
        return step("[API] Получаем данные первого активного лицевого счета и его ПУ", () -> {

            String token = AuthContext.getToken();

            if (token == null || token.isEmpty()) {
                LoginBodyModel loginData = new LoginBodyModel(
                        TestConfig.getTestUserPhone(),
                        TestConfig.getTestUserPassword()
                );
                token = api.auth.loginAndGetToken(loginData);
                AuthContext.setToken(token);
            }

            List<UsersAccountResponseModel> usersList = api.usersAccount.accountGetInfo(token);

            UsersAccountResponseModel.Core activeCore = usersList.stream()
                    .flatMap(user -> user.accounts().stream())
                    .filter(account -> account.core() != null && !account.core().isEmpty())
                    .filter(account -> {
                        Integer status = account.core().get(0).idStatus();
                        return status != null && status == 2;
                    })
                    .map(account -> account.core().get(0))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Не найдено ни одного активного лицевого счета (статус 2)"));

            DevicesResponseModel devicesResponse = api.devices.userDevicesInfo(token, activeCore.idPA());
            DevicesResponseModel.CoreDevice device = devicesResponse.coreDevices().stream()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Список coreDevices пуст или null"));

            DevicesResponseModel.Meter firstMeter = device.meters().get(0);
            String formattedDate = ZonedDateTime.parse(device.dateCheckNext())
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            return new ActiveAccountData(
                    activeCore.number(),
                    activeCore.idPA(),
                    device.deviceTitle(),
                    device.modification(),
                    device.serialNumber(),
                    formattedDate,
                    firstMeter.reading().toString(),
                    firstMeter.title()
            );
        });
    }

    /**
     * Получаем данные двух активных лицевых счетов и их приборов учета.
     **/
    public record TwoAccountsData(
            ActiveAccountData first,
            ActiveAccountData second
    ) {
    }

    public TwoAccountsData getTwoActiveAccountData() {
        return step("[API] Получаем данные двух активных лицевых счетов и их ПУ", () -> {

            String token = AuthContext.getToken();

            if (token == null || token.isEmpty()) {
                LoginBodyModel loginData = new LoginBodyModel(
                        TestConfig.getTestUserPhone(),
                        TestConfig.getTestUserPassword()
                );
                token = api.auth.loginAndGetToken(loginData);
                AuthContext.setToken(token);
            }

            List<UsersAccountResponseModel> usersList = api.usersAccount.accountGetInfo(token);

            List<UsersAccountResponseModel.Core> activeCores = usersList.stream()
                    .flatMap(user -> user.accounts().stream())
                    .filter(account -> account.core() != null && !account.core().isEmpty())
                    .filter(account -> {
                        Integer status = account.core().get(0).idStatus();
                        return status != null && status == 2;
                    })
                    .map(account -> account.core().get(0))
                    .distinct()
                    .limit(2)
                    .toList();

            if (activeCores.size() < 2) {
                throw new AssertionError("Не найдено двух различных активных лицевых счетов. Найдено: " + activeCores.size());
            }

            ActiveAccountData first = extractAccountData(token, activeCores.get(0));

            // Задержка перед вторым API-запросом (защита от 429)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            ActiveAccountData second = extractAccountData(token, activeCores.get(1));

            return new TwoAccountsData(first, second);
        });
    }

    private ActiveAccountData extractAccountData(String token, UsersAccountResponseModel.Core core) {
        DevicesResponseModel devicesResponse = api.devices.userDevicesInfo(token, core.idPA());
        DevicesResponseModel.CoreDevice device = devicesResponse.coreDevices().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("coreDevices пуст для ЛС " + core.number()));

        DevicesResponseModel.Meter firstMeter = device.meters().get(0);
        String formattedDate = ZonedDateTime.parse(device.dateCheckNext())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        return new ActiveAccountData(
                core.number(),
                core.idPA(),
                device.deviceTitle(),
                device.modification(),
                device.serialNumber(),
                formattedDate,
                firstMeter.reading().toString(),
                firstMeter.title()
        );
    }
}