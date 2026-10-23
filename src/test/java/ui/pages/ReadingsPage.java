package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static config.Pages.READINGS;
import static org.assertj.core.api.Assertions.assertThat;

public class ReadingsPage {
    private final SelenideElement submitReadingsTabs = $("[data-testid=tabs-submit-readings]");
    private final SelenideElement historyReadingsTabs = $("[data-testid=tabs-readings-history]");
    private final SelenideElement accountsSection = $("[data-testid=accounts-section-title]");
    private final SelenideElement unifiedAccountsSection = $("[data-testid=unified-accounts-section-title]");


    private final SelenideElement accountTitle = $("[data-testid=account-title]");
    private final SelenideElement meterDeviceToggle = $("[data-testid=meter-device-toggle]");
    private final SelenideElement verificationDate = $("[data-testid=meter-next-verification-date]");
    private final SelenideElement meterDeviceName = $("[data-testid=meter-device-name]");
    private final SelenideElement tariffZone = $("[data-testid*='tariff-zone-'][data-testid$='-title']");
    private final SelenideElement lastReading = $("[data-testid*='tariff-zone-'][data-testid$='-last-reading']");

    private final SelenideElement readingFieldInput = $("[data-testid*='tariff-zone-'][data-testid$='-input']");
    private final SelenideElement differenceReading = $(withText("Разница:"));
    private final SelenideElement submitReadingsButton = $("[data-testid=submit-readings-btn]");
    private final SelenideElement readingPeriodInfo = $("[data-testid=reading-period-info]");
    private final SelenideElement tariffZoneError = $("[data-testid*='tariff-zone-'][data-testid$='-error']");

    private final SelenideElement successDialog = $("div[role='dialog'][aria-modal='true']");
    private final SelenideElement successDialogTitle =
            $x("//div[@role='dialog']//div[contains(text(),'Данные успешно переданы')]");
    private final SelenideElement okButton = $x("//div[text()='Отлично']");


    @Step("[UI] Открыть страницу 'Передать показания'")
    public ReadingsPage openReadingsPage() {
        open(READINGS);
        return this;
    }

    @Step("[UI] Нажать кнопку для открытия карточки ПУ")
    public ReadingsPage clickMeterDeviceToggle() {
        SelenideElement pageTitle = $(byText("Передать показания"));
        pageTitle.shouldBe(visible);
        meterDeviceToggle.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Выбрать карточку лицевого счета {accountNumber}")
    public ReadingsPage selectAccountCard(String accountNumber) {
        String dynamicTestId = "account-card-" + accountNumber;
        SelenideElement accountCard = $("[data-testid='" + dynamicTestId + "']");
        accountCard.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Получить предыдущее показание")
    public int getLastReading() {
        String text = lastReading.getText();
        String number = text.replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

    @Step("[UI] Получить текущую разницу показаний")
    public int getDifferenceReading() {
        String text = differenceReading.shouldBe(visible).getText();
        String number = text.replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

    @Step("[UI] Ввести новое показание: {value}")
    public ReadingsPage enterReading(String value) {
        readingFieldInput.clear();
        readingFieldInput.shouldBe(visible).setValue(value);
        return this;
    }

    @Step("[UI] Получить текущее значение поля ввода показания")
    public String getReadingInputValue() {
        return readingFieldInput.getValue();
    }

    @Step("[UI] Проверить, что поле содержит значение: {expected}")
    public ReadingsPage verifyReadingInputValue(String expected) {
        String actual = getReadingInputValue();
        assertThat(actual).as("Поле ввода показаний содержит неожиданное значение").isEqualTo(expected);
        return this;
    }

    @Step("[UI] Нажимаем на тогл вкладки 'Передать показания'")
    public ReadingsPage clickSubmitReadingsTabs() {
        submitReadingsTabs.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Нажимаем на тогл вкладки 'История показаний'")
    public ReadingsPage clickHistoryReadingsTabs() {
        historyReadingsTabs.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Нажимаем кнопку 'Отправить показания'")
    public ReadingsPage clickSubmitReadingsButton() {
        submitReadingsButton.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Ожидать отображения данных ПУ")
    public ReadingsPage waitForDeviceDataToLoad() {
        meterDeviceName.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    // Проверки
    @Step("[UI] Проверка URL страницы")
    public ReadingsPage verifyPageUrl() {
        webdriver().shouldHave(urlContaining(READINGS));
        return this;
    }

    @Step("[UI] Проверка наличие кнопок Передать покания и История показаний")
    public ReadingsPage verifyReadingSubmitAndHistoryButtons() {
        submitReadingsTabs.shouldBe(visible);
        historyReadingsTabs.shouldBe(visible);
        return this;
    }

    @Step("[UI] Проверка наличия секций с ЛС и ЕЛС")
    public ReadingsPage verifyAccountsAndUnifiedAccountsSection() {
        accountsSection.shouldBe(visible);
        unifiedAccountsSection.shouldBe(visible);
        return this;
    }

    @Step("Проверяем отображение номера ЛС")
    public ReadingsPage verifyAccountNumberIsDisplayed(String expectedAccountNumber) {
        accountTitle.shouldBe(visible);
        accountTitle.shouldHave(text(expectedAccountNumber));
        return this;
    }

    @Step("Проверяем отображение данных прибора учета")
    public ReadingsPage verifyMeterDeviceData(String deviceTitle, String modification, String serialNumber) {
        meterDeviceName.shouldBe(visible);
        meterDeviceName.shouldHave(text(deviceTitle));
        meterDeviceName.shouldHave(text(modification));
        meterDeviceName.shouldHave(text(serialNumber));
        return this;
    }

    @Step("Проверяем дату поверки")
    public ReadingsPage verifyVerificationDate(String expectedDateCheckNext) {
        verificationDate.shouldBe(visible);
        verificationDate.shouldHave(text(expectedDateCheckNext));
        return this;
    }

    @Step("Проверяем отображение названия тарифа")
    public ReadingsPage verifyTariffTitle(String expectedTariffName) {
        tariffZone.shouldHave(text(expectedTariffName));
        tariffZone.shouldBe(visible);
        return this;
    }

    @Step("Проверяем отображение периода передачи показаний")
    public ReadingsPage verifyReadingPeriodInfo(String expectedText) {
        readingPeriodInfo.shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверяем отображение ошибки")
    public ReadingsPage verifyTariffZoneError(String expectedText) {
        tariffZoneError.shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }

    @Step("[UI] Проверяем, что последнее показание равно {expectedReading}")
    public ReadingsPage verifyLastReading(String expectedReading) {
        lastReading.shouldBe(visible).shouldHave(text(expectedReading));

        return this;
    }

    @Step("[UI] Проверяем наличие поля для ввода показаний")
    public ReadingsPage verifyReadingFieldInput() {
        readingFieldInput.shouldBe(visible);
        return this;
    }

    @Step("[UI] Проверяем наличие кнопки 'Отправить показания'")
    public ReadingsPage verifySubmitReadingsButton() {
        submitReadingsTabs.shouldBe(visible);
        return this;
    }

    @Step("[UI] Проверить, что разница показаний равна {expected}")
    public ReadingsPage verifyDifference(int expected) {
        int actual = getDifferenceReading();
        assertThat(actual).as("Разница показаний").isEqualTo(expected);
        return this;
    }

    @Step("[UI] Проверить, что открылось диалоговое окно")
    public ReadingsPage checkSuccessDialogIsVisible() {
        successDialog.shouldBe(visible);
        successDialogTitle.shouldHave(text("Данные успешно переданы"));
        return this;
    }

    @Step("[UI] Проверить, что закрылось диалоговое окно")
    public ReadingsPage closeSuccessDialog() {
        okButton.shouldBe(visible).click();
        successDialog.shouldNotBe(visible);
        return this;
    }

    @Step("Проверяем, что поле ввода подсвечено красным бордюром")
    public ReadingsPage verifyReadingInputHasErrorBorder() {
        readingFieldInput.parent().shouldHave(cssClass("border-red"));
        return this;
    }

    // Методы для работы с несколькими тарифами
    @Step("[UI] Получить количество тарифных зон")
    public int getTariffZoneCount() {
        return $$("[data-testid^='tariff-zone-'][data-testid$='-title']").size();
    }

    @Step("[UI] Получить предыдущее показание тарифной зоны {index}")
    public int getLastReadingByZoneIndex(int index) {
        ElementsCollection lastReadings = $$("[data-testid^='tariff-zone-'][data-testid$='-last-reading']");
        SelenideElement element = lastReadings.get(index);
        String text = element.getText();
        String number = text.replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

    @Step("[UI] Ввести показание для тарифной зоны {index}: {value}")
    public ReadingsPage enterReadingByZoneIndex(int index, String value) {
        ElementsCollection inputs = $$("[data-testid^='tariff-zone-'][data-testid$='-input']");
        SelenideElement input = inputs.get(index);
        input.clear();
        input.shouldBe(visible).setValue(value);
        return this;
    }

    @Step("[UI] Проверить разницу для тарифной зоны {index}: {expected}")
    public ReadingsPage verifyDifferenceByZoneIndex(int index, int expected) {
        ElementsCollection differences = $$("[data-testid^='tariff-zone-'][data-testid$='-difference']");
        SelenideElement diffElement = differences.get(index);
        String text = diffElement.shouldBe(visible).getText();
        String number = text.replaceAll("[^0-9]", "");
        int actual = Integer.parseInt(number);
        assertThat(actual).as("Разница показаний для тарифной зоны %d", index).isEqualTo(expected);
        return this;
    }
}
