package ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static config.Pages.FINANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentPage {
    private SelenideElement paymentFormTitle =
            $(withText("Единый лицевой счет №"));
    private SelenideElement submitPaymentButton =
            $(withText("Оплатить"));

    // Действия
    @Step("[UI] Нажать кнопку 'Оплатить'")
    public PaymentPage clickSubmitPaymentButton() {
        submitPaymentButton.shouldBe(visible).click();
        return this;
    }

    // Проверки
    @Step("Проверить, что текущий URL соответствует странице финансов")
    public PaymentPage verifyFinanceUrl() {
        String currentUrl = WebDriverRunner.url();
        assertThat(currentUrl).as("URL должен содержать /finance").contains(FINANCE);
        return this;
    }

    @Step("Проверить, что клик на 'Финансы' открывает страницу финансов")
    public PaymentPage verifyNavigationToFinance() {
        $(byText("История платежей")).shouldBe(visible);
        return this;
    }

    @Step("[UI] Проверка, что форма оплаты открыта")
    public PaymentPage verifyPaymentFormTitle() {
        paymentFormTitle.shouldBe(visible);
        return this;
    }
    @Step("[UI] Проверка, что кнопка Оплатить активна")
    public PaymentPage verifySubmitPaymentButton() {
        submitPaymentButton.scrollTo();
            submitPaymentButton.shouldBe(visible);
            return this;
    }
    public PaymentPage verifySubmitPaymentButton1() {
        submitPaymentButton.shouldHave(text("Оплатить"));
        return this;
    }
}
