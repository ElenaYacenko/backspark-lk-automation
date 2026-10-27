package extensions;

import api.apiClients.ApiClient;
import api.models.localstorage.LocalStorageAuthModel;
import api.models.login.LoginBodyModel;
import config.TestConfig;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import utils.AuthContext;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;

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
        openFaviconAndSetLocalStorage("auth-tokens-production", authBody.toJson());
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

}

