package mobile.screen;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginScreen {
    private final SelenideElement enterLoginButton = $(AppiumBy.xpath("//android.widget.TextView[@text='Войти']"));
    private final SelenideElement loginField = $(AppiumBy.xpath("//android.widget.EditText[@resource-id='email']"));
    private final SelenideElement passwordField = $(AppiumBy.xpath("//android.widget.EditText[@resource-id='password']"));
    private final SelenideElement submitButton =
            $(AppiumBy.xpath("//android.widget.EditText[@resource-id='password']/ancestor::android.view.ViewGroup//android.widget.TextView[@text='Войти']"));

    // --- Действия ---
    @Step("[MOB] Открываем экран Входа в личный кабинет")
    public LoginScreen clickSearch() {
        enterLoginButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        return this;
    }

    @Step("[MOB] Ввести email/телефон")
    public LoginScreen enterLogin(String phone) {
        loginField.shouldBe(visible).click();
        loginField.sendKeys(phone);
        return this;
    }

    @Step("[MOB] Ввести пароль")
    public LoginScreen enterPassword(String password) {
        passwordField.shouldBe(visible).click();
        passwordField.sendKeys(password);
        return this;
    }

    @Step("[MOB] Нажать кнопку 'Войти'")
    public LoginScreen clickLogin() {
        submitButton.shouldBe(visible).click();
        return this;
    }

    @Step("[MOB] Закрываем модалку обновления (если она появилась)")
    public LoginScreen closeUpdateModalIfPresent() {
        if ($(AppiumBy.xpath("//*[contains(@text, 'Обновите приложение')]")).exists()) {
            Selenide.back();
            Selenide.sleep(500);
        }
        return new LoginScreen();
    }

    @Step("[MOB] Закрываем модалку с информацией о платежах в ФКР")
    public LoginScreen closePaymentsConfirmationModal() {
        $(AppiumBy.xpath("//*[contains(@text, 'Информация о платежах в фонд')]"))
                .shouldBe(visible, Duration.ofSeconds(15));
        Selenide.back();
        Selenide.sleep(500);
        return this;
    }

    // --- Проверки ---

}
