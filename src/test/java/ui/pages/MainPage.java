package ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static config.Pages.HOME;
import static org.assertj.core.api.Assertions.assertThat;

public class MainPage {

    private SelenideElement firstUnifiedAccountCard =
            $x("(//div[@tabindex='0' and .//*[contains(text(),'ЕЛС №')]])[1]");
    private SelenideElement payButton = $x("//*[contains(text(),'Оплатить')]");
    private SelenideElement submitReadingsButton = $x("//*[contains(text(),'Передать показания')]");
    private SelenideElement accountInfoButton = $x("//*[contains(text(),'Информация о счете')]");
    private SelenideElement unlinkAccountButton = $x("//*[contains(text(),'Открепить счет')]");

    // Действия
    @Step("UI: Открыть главную страницу")
    public MainPage openMainPage(){
        open(HOME);
        return this;
    }

    @Step("[UI] Нажать кнопку 'Оплатить'")
    public MainPage clickGetPaymentTabButton() {
        payButton.shouldBe(visible).click();
        return this;
    }

    @Step("[UI] Нажать на первый ЕЛС в списке")
    public MainPage clickFirstUnifiedAccountCard() {
        firstUnifiedAccountCard.shouldBe(visible).click();
        return this;
    }

    // Проверки
    @Step("Проверить, что текущий URL соответствует домашней странице")
    public MainPage verifyHomeUrl() {
        webdriver().shouldHave(urlContaining(HOME));
        return this;
    }

}
