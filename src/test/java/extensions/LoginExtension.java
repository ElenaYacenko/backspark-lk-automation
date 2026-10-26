package extensions;

import api.apiClients.ApiClient;
import config.TestConfig;
import io.qameta.allure.Step;
import api.models.localstorage.LocalStorageAuthModel;
import api.models.login.LoginBodyModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import utils.AuthContext;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class LoginExtension implements BeforeEachCallback {
    ApiClient api = new ApiClient();

    @Override
    @Step("[UI] Открытие страницы с существующим пользователем")
    public void beforeEach(ExtensionContext context) {
        LoginBodyModel loginData = new LoginBodyModel(
                TestConfig.getTestUserPhone(),
                TestConfig.getTestUserPassword()
        );

        String token = api.auth.loginAndGetToken(loginData);

        // Сохраняем токен + userId для доступа из теста
        AuthContext.setToken(token);

        LocalStorageAuthModel authBody = new LocalStorageAuthModel(token, token);
        String jsonPayload = authBody.toJson();

        // 🔍 ОТЛАДКА 1: Проверяем, что именно мы пытаемся записать
        System.out.println("==================================================");
        System.out.println("🔍 DEBUG: Проверка payload ПЕРЕД записью в localStorage");
        System.out.println("JSON строка: " + jsonPayload);
        if (jsonPayload == null || jsonPayload.isEmpty() || jsonPayload.equals("{}")) {
            System.out.println("❌ ВНИМАНИЕ: JSON пустой или невалидный! Проверь метод toJson()");
        }
        System.out.println("==================================================");
        openFaviconAndSetLocalStorage("auth-tokens-production", authBody.toJson());
        debugLocalStorage();
    }

    @Step("[UI] Открытие /favicon.ico и установка данных в localstorage")
    public void openFaviconAndSetLocalStorage(String key, String value) {
        openFavicon();
        setLocalStorage(key, value);
    }

    @Step("[UI] Установка данных в localstorage")
    public void setLocalStorage(String key, String value) {
        localStorage().setItem(key, value);
    }

    @Step("[UI] Открытие /favicon.ico")
    public void openFavicon() {
        open("/favicon.ico");
    }

    @Step("[UI] Отладка: проверка содержимого localStorage")
    public void debugLocalStorage() {
        System.out.println("==================================================");
        System.out.println("🔍 LOCALSTORAGE DEBUG (ПОСЛЕ записи)");
        System.out.println("1. Текущий URL: " + getWebDriver().getCurrentUrl());
        System.out.println("2. Все ключи в LS: " + localStorage().getItems());

        String savedValue = localStorage().getItem("auth-tokens-production");
        System.out.println("3. Значение 'auth-tokens-production': " + savedValue);

        if (savedValue == null || savedValue.isEmpty()) {
            System.out.println("❌ ОШИБКА: Токен НЕ сохранен в localStorage! Фронтенд сделает редирект на логин.");
        } else {
            System.out.println("✅ УСПЕХ: Токен успешно сохранен в localStorage.");
        }
        System.out.println("==================================================");
    }
}

