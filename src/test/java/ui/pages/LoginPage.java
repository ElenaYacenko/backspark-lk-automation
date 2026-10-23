package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import config.TestConfig;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage extends BasePage {
    // Селекторы для страницы Личный кабинет физического лица
    private SelenideElement mainPageTitle = $x("//*[contains(text(),'Личный кабинет для частных лиц')]");
    private SelenideElement loginButton = $x("//button[contains(text(),'Войти')]");
    private SelenideElement titleLoginPage = $x("//*[contains(text(),'Вход в личный кабинет')]");

    // Селекторы для страницы Вход в личный кабинет Для частных лиц
    private SelenideElement phoneInput = $("#iPhoneOrEmail");
    private SelenideElement passwordInput = $("#iPassword");
    private SelenideElement authButton = $x("//*[contains(text(),'Войти')]");

    // Действия
    @Step("[UI] Открыть страницу авторизации")
    public LoginPage open() {
        Selenide.open(TestConfig.getBaseUrl() + "/person-auth-form");
        return this;
    }

    @Step("[UI] Ввести email/телефон")
    public LoginPage enterPhone (String phone) {
        phoneInput.shouldBe(visible).setValue(phone);
        return this;
    }

    @Step("[UI] Ввести пароль")
    public LoginPage enterPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

   @Step("[UI] Нажать кнопку 'Войти'")
    public LoginPage clickLogin() {
       authButton.shouldBe(visible).click();
       return this;
    }

    // Проверки
    @Step("[UI] Проверка, что открыта страница авторизации")
    public LoginPage loginPageShouldBeOpened() {
        titleLoginPage.shouldBe(visible);
        return this;
    }

    @Step("[UI] Проверка, что открыта welcome-страница (ЛК для физических лиц)")
    public LoginPage mainPageShouldBeOpened() {

        mainPageTitle.shouldBe(visible);
        return this;
    }
}