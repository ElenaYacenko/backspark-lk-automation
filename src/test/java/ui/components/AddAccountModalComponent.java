package ui.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class AddAccountModalComponent {

    private final SelenideElement addModalTitle = $(byText("Добавление лицевого счета"));
    private final SelenideElement accountNumberInput = $("[data-testid=account-number-input]");
    private final SelenideElement uniqueCodeInput = $("[data-testid=unique-code-input]");
    private final SelenideElement submitButton = $("[data-testid=add-account-submit]");
    private final SelenideElement closeButton = $("[data-testid=modal-close]");

    @Step("Закрыть модальное окно добавления счета")
    public AddAccountModalComponent close() {
        closeButton.click();
        return this;
    }

    @Step("Ввести номер счета {accountNumber}")
    public AddAccountModalComponent enterAccountNumber(String accountNumber) {
        accountNumberInput.setValue(accountNumber);
        return this;
    }

    @Step("Ввести уникальный код {uniqueCode}")
    public AddAccountModalComponent enterUniqueCode(String uniqueCode) {
        uniqueCodeInput.setValue(uniqueCode);
        return this;
    }

    @Step("Нажать кнопку 'Добавить'")
    public void submit() {
        submitButton.click();
    }

    @Step("Проверить, что модальное окно отображается")
    public AddAccountModalComponent verifyModalVisible() {
        addModalTitle.shouldBe(visible);
        return this;
    }
}