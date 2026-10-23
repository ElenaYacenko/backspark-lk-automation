package mobile.screen;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainScreen {

    // --- Локаторы ---
    private final SelenideElement pageMainTitle = $(AppiumBy.xpath("//android.widget.TextView[@text='Главная']"));
    private final SelenideElement payButton = $(
            AppiumBy.xpath("//*[contains(@content-desc, 'Оплатить')]"));

    private final SelenideElement submitReadingsButton = $(
            AppiumBy.xpath("//*[@content-desc='Передать, показания']"));

    private final SelenideElement accountInfoButton = $(
            AppiumBy.xpath("//*[@content-desc='Информация о счете']"));

    private final SelenideElement detachAccountButton = $(
            AppiumBy.xpath("//*[@content-desc='Открепить счет']"));

    // --- Действия ---
    @Step("[MOB] Кликнуть на кнопку 'Передать показания'")
    public MainScreen clickSubmitReadingsButtonVisible() {
        submitReadingsButton.shouldBe(visible).click();
        return this;
    }

    // --- Проверки ---
    @Step("[MOB] Проверить отображение заголовка главной страницы 'Главная'")
    public MainScreen verifyPageTitle() {
        pageMainTitle.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение кнопки 'Оплатить'")
    public MainScreen verifyPayButtonVisible() {
        payButton.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение кнопки 'Передать показания'")
    public MainScreen verifySubmitReadingsButtonVisible() {
        submitReadingsButton.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение кнопки 'Информация о счете'")
    public MainScreen verifyAccountInfoButtonVisible() {
        accountInfoButton.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение кнопки 'Открепить счет'")
    public MainScreen verifyDetachAccountButtonVisible() {
        detachAccountButton.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение всех кнопок действий")
    public MainScreen verifyAllActionButtonsVisible() {
        verifyPayButtonVisible();
        verifySubmitReadingsButtonVisible();
        verifyAccountInfoButtonVisible();
        verifyDetachAccountButtonVisible();
        return this;
    }
}
