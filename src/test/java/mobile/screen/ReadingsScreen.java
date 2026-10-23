package mobile.screen;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ReadingsScreen {

    // --- Локаторы ---
    private final SelenideElement pageTitle = $(AppiumBy.xpath("//android.widget.TextView[@text='Показания']"));
    private final SelenideElement submitTab = $(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='передать']//android.widget.TextView[@text='передать']"));
    private final SelenideElement historyTab = $(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='история']//android.widget.TextView[@text='история']"));
    private final SelenideElement accountNumber = $(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Лицевой счет №')]"));
    private final SelenideElement meterDeviceTitle = $(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'ПУ:')]"));
    private final SelenideElement verificationDate = $(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Дата следующей поверки:')]"));
    private final SelenideElement lastReading = $(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Последнее показание:')]"));

    // --- Действия ---
    @Step("[MOB] Раскрыть карточку прибора учёта")
    public ReadingsScreen clickVerificationDateDisplayed() {
        verificationDate.shouldBe(visible).click();
        return this;
    }

    // --- Проверки ---
    @Step("[MOB] Проверить отображение заголовка страницы 'Показания'")
    public ReadingsScreen verifyPageTitle() {
        pageTitle.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение вкладки 'передать'")
    public ReadingsScreen verifySubmitTabVisible() {
        submitTab.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение вкладки 'история'")
    public ReadingsScreen verifyHistoryTabVisible() {
        historyTab.shouldBe(visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("[MOB] Проверить отображение номера ЛС: {expectedAccountNumber}")
    public ReadingsScreen verifyAccountNumberDisplayed(String expectedAccountNumber) {
        String fullText = "Лицевой счет № " + expectedAccountNumber;
        accountNumber.shouldHave(visible, Duration.ofSeconds(5));
        accountNumber.shouldHave(text(fullText));
        return this;
    }

    @Step("[MOB] Проверить отображение названия ПУ")
    public ReadingsScreen verifyMeterDeviceTitleDisplayed(String deviceTitle, String modification, String serialNumber) {
        meterDeviceTitle.shouldBe(visible, Duration.ofSeconds(5));
        meterDeviceTitle.shouldHave(text(deviceTitle));
        meterDeviceTitle.shouldHave(text(modification));
        meterDeviceTitle.shouldHave(text(serialNumber));
        return this;
    }

    @Step("[MOB] Проверить отображение даты следующей поверки")
    public ReadingsScreen verifyVerificationDateDisplayed(String expectedDateCheckNext) {
        verificationDate.shouldBe(visible, Duration.ofSeconds(5));
        verificationDate.shouldHave(text(expectedDateCheckNext));
        return this;
    }

    @Step("[MOB] Проверить, что последнее показание равно {expectedReading}")
    public ReadingsScreen verifyLastReading(String expectedReading) {
        lastReading.shouldBe(visible).shouldHave(text(expectedReading));
        return this;
    }
}
