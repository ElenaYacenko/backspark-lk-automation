package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ModalDialog {
    // Модалка о загрузке платежей ФКР
    //private final SelenideElement fkrModalTitle = $x("//*[contains(text(),'Уважаемые пользователи!')]");
    private final SelenideElement fkrModal = $("[data-testid=modal-info]");
    private final SelenideElement fkrModalTitle = $("[data-testid=modal-info-title]");

    private final SelenideElement fkrModalText = $x("//*[contains(text(),'Информация о платежах в фонд капитального ремонта')]");
    private final SelenideElement fkrModalCloseButton = $("[data-testid=modal-info-close-btn]");
    //private final SelenideElement fkrModalCloseButton = $("#cArrearsOfPaymentInfoClose");


    // Действия
    @Step("[UI] Нажать кнопку 'Закрыть'")
    public ModalDialog clickCloseButton() {
        fkrModalCloseButton.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Проверка и закрытие модального окна ФКР (если открыто)")
    public ModalDialog checkAndCloseFkrModal() {
        if (fkrModal.exists()) {
            fkrModalCloseButton.shouldBe(visible).click();
        }
        return this;
    }

    // Проверки
    @Step("[UI] Проверка, что открыто модальное окно с информацией о загрузке платежей ФКР")
    public ModalDialog modalFKROpened() {
        fkrModal.shouldBe(visible);
        return this;
    }
}
