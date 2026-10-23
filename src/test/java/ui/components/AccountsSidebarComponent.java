package ui.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.pages.MainPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;

public class AccountsSidebarComponent {

    // --- Элементы ---
    private SelenideElement accountsSection = $x("//*[contains(text(),'Лицевые счета')]");
    private final SelenideElement accountsSectionTitle = $("[data-testid=accounts-section-title]");
    private final SelenideElement accountsSectionToggle = $("[data-testid=accounts-section-toggle]");
    private final SelenideElement addAccountButton = $("[data-testid=accounts-add-btn]");
    private final ElementsCollection accountCards = $$("[data-testid^=account-card-]");
    private final ElementsCollection accountCardsAmount = $$("[data-testid*=account-card-][data-testid$='-amount']");

    // Конструктор для работы с конкретным счетом
    private SelenideElement getAccountCard(String accountNumber) {
        return $("[data-testid=account-card-" + accountNumber + "]");
    }
    private SelenideElement getAccountBalance(String accountNumber) {
        return $("[data-testid=account-card-" + accountNumber + "-amount]");
    }

    // --- Действия ---
    @Step("[UI] Проверка, отображения  секции Лицевых счетов")
    public AccountsSidebarComponent verifyPersonalAccounts() {
        accountsSection.shouldBe(visible);
        return this;
    }

    @Step("Развернуть/свернуть секцию 'Лицевые счета'")
    public AccountsSidebarComponent clickToggleSection() {
        accountsSectionToggle.click();
        return this;
    }

    @Step("Нажать кнопку 'Добавить лицевой счет'")
    public AddAccountModalComponent clickAddAccountButton() {
        addAccountButton.click();
        return new AddAccountModalComponent(); // Возвращаем компонент модального окна
    }

    @Step("Кликнуть на карточку лицевого счета {accountNumber}")
    public AccountsSidebarComponent selectAccount(String accountNumber) {
        getAccountCard(accountNumber).click();
        return this;
    }

    @Step("Инициализация: клик по карточкам ЛС для вызова API getDeviceInfo")
    public void clickAllAccountCards() {
        ElementsCollection cards = $$("[data-testid^=account-card-]");
        if (cards.isEmpty()) {
            throw new AssertionError("Не найдено ни одной карточки лицевого счета для инициализации");
        }
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).click();
        }
    }

    // --- Проверки ---

    @Step("Проверить, что секция 'Лицевые счета' отображается")
    public AccountsSidebarComponent verifySectionVisible() {
        accountsSectionTitle.shouldHave(text("Лицевые счета"));
        return this;
    }

    @Step("Проверить, что лицевой счет {accountNumber} отображается")
    public AccountsSidebarComponent verifyAccountExists(String accountNumber) {
        getAccountCard(accountNumber).shouldBe(visible);
        return this;
    }

    @Step("Проверить баланс счета {accountNumber}: {expectedBalance}")
    public AccountsSidebarComponent verifyAccountBalance(String accountNumber, String expectedBalance) {
        SelenideElement balanceElement = $("[data-testid=account-card-" + accountNumber + "-amount]");
        balanceElement.shouldBe(visible).shouldHave(text(expectedBalance));
        return this;
    }

    @Step("Проверить, что счет {accountNumber} выделен (активен)")
    public AccountsSidebarComponent verifyAccountIsActive(String accountNumber) {
        getAccountCard(accountNumber).shouldHave(cssClass("active"));
        return this;
    }

    @Step("Проверить, что кнопка '+' отображается")
    public AccountsSidebarComponent verifyAddButtonVisible() {
        addAccountButton.shouldBe(visible);
        return this;
    }
}