package ui.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.pages.MainPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class UnifiedAccountsSidebarComponent {

    // --- Элементы ---
    private SelenideElement unifiedAccountsSection = $x("//*[contains(text(),'Единые лицевые счета')]");
    private final SelenideElement unifiedAccountsSectionTitle = $("[data-testid=unified-accounts-section-title]");
    private final SelenideElement unifiedAccountsSectionToggle = $("[data-testid=unified-accounts-section-toggle]");
    private final SelenideElement addUnifiedButton = $("[data-testid=unified-accounts-add-btn]");
    private final ElementsCollection unifiedAccountCards = $$("[data-testid^=unified-account-card-]");
    private final SelenideElement unifiedAccountExpandButtons =
            $("[data-testid*=unified-account-card-][data-testid$='-expand-btn']");
    private final SelenideElement thumbnailLogo = $("[data-src*='thumbnail.svg']");



    // Конструктор для работы с конкретным единым счетом
    private SelenideElement getUnifiedAccountCard(String unifiedAccountNumber) {
        return $("[data-testid=unified-account-card-" + unifiedAccountNumber + "]");
    }

    private SelenideElement getUnifiedAccountBalance(String unifiedAccountNumber) {
        return $("[data-testid=unified-account-card-" + unifiedAccountNumber + "-amount]");
    }

    // Для работы с вложенными обычными счетами внутри единого
    private ElementsCollection getChildAccounts(String unifiedAccountNumber) {
        return getUnifiedAccountCard(unifiedAccountNumber).$$("[data-testid^=account-card-]");
    }

    // --- Действия ---

    @Step("[UI] Проверка, отображения секции ЕЛС")
    public UnifiedAccountsSidebarComponent verifyUnifiedAccounts() {
        unifiedAccountsSection.shouldBe(visible);
        return this;
    }

    @Step("Развернуть/свернуть секцию 'Единые лицевые счета'")
    public UnifiedAccountsSidebarComponent toggleSection() {
        unifiedAccountsSectionToggle.click();
        return this;
    }

    @Step("Нажать кнопку 'Добавить единый лицевой счет'")
    public AddAccountModalComponent clickAddUnifiedButton() {
        addUnifiedButton.click();
        return new AddAccountModalComponent();
    }

    @Step("Кликнуть на единый лицевой счет {unifiedAccountNumber}")
    public UnifiedAccountsSidebarComponent selectUnifiedAccount(String unifiedAccountNumber) {
        getUnifiedAccountCard(unifiedAccountNumber).click();
        return this;
    }

    @Step("Кликнуть на кнопку открытия единого лицевого счета {unifiedAccountNumber}")
    public UnifiedAccountsSidebarComponent clickUnifiedAccountExpandButtons() {
        unifiedAccountExpandButtons.click();
        return this;
    }

    @Step("Кликнуть на вложенный счет {accountNumber} внутри единого счета {unifiedAccountNumber}")
    public UnifiedAccountsSidebarComponent selectChildAccount(String unifiedAccountNumber, String accountNumber) {
        getChildAccounts(unifiedAccountNumber)
                .findBy(text("Счет № " + accountNumber))  // ← используем findBy + Condition.text
                .click();
        return this;
    }

    @Step("Кликнуть на логотип ГУБ БОСС")
    public UnifiedAccountsSidebarComponent clickThumbnailLogo() {
        thumbnailLogo.click();
        return this;
    }

    // --- Проверки ---

    @Step("Проверить, что секция 'Единые лицевые счета' отображается")
    public UnifiedAccountsSidebarComponent verifySectionVisible() {
        unifiedAccountsSectionTitle.shouldHave(text("Единые лицевые счета"));
        return this;
    }

    @Step("Проверить, что единый лицевой счет {unifiedAccountNumber} отображается")
    public UnifiedAccountsSidebarComponent verifyUnifiedAccountExists(String unifiedAccountNumber) {
        getUnifiedAccountCard(unifiedAccountNumber).shouldBe(visible);
        return this;
    }

    @Step("Проверить баланс единого лицевого счета {unifiedAccountNumber}: {expectedBalance}")
    public UnifiedAccountsSidebarComponent verifyUnifiedAccountBalance(
            String unifiedAccountNumber,
            String expectedBalance) {

        getUnifiedAccountBalance(unifiedAccountNumber)
                .shouldBe(visible)
                .shouldHave(text(expectedBalance));
        return this;
    }
}