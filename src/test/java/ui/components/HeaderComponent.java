package ui.components;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static config.Pages.*;
import static org.assertj.core.api.Assertions.assertThat;

public class HeaderComponent {
    // --- Элементы Header ---
    private final SelenideElement headerLogo = $("[data-testid=header-logo]");
    private final SelenideElement headerNavHome = $("[data-testid=header-nav-home]");
    private final SelenideElement headerNavFinance = $("[data-testid=header-nav-finance]");
    private final SelenideElement headerNavReadings = $("[data-testid=header-nav-readings]");
    private final SelenideElement headerNavServices = $("[data-testid=header-nav-services]");
    private final SelenideElement headerNotificationsIcon = $("[data-testid=header-notifications-icon]");
    private final SelenideElement headerLampIcon = $("[data-testid=header-lamp-icon]");
    private final SelenideElement headerDocumentIcon = $("[data-testid=header-document-icon]");
    private final SelenideElement headerUserAvatar = $("[data-testid=header-user-avatar]");


    // --- Действия ---
    @Step("Нажать на логотип хедера")
    public HeaderComponent clickHeaderLogo() {
        headerLogo.click();
        return this;
    }

    @Step("Нажать в хедере на 'Главная'")
    public HeaderComponent clickHeaderNavHome() {
        headerNavHome.click();
        return this;
    }

    @Step("Нажать в хедере на 'Финансы'")
    public HeaderComponent clickHeaderNavFinance() {
        headerNavFinance.click();
        return this;
    }

    @Step("Нажать в хедере на 'Показания'")
    public HeaderComponent clickHeaderNavReadings() {
        headerNavReadings.click();
        return this;
    }
    @Step("Нажать в хедере на 'Сервисы'")
    public HeaderComponent clickHeaderNavServices() {
        headerNavServices.click();
        return this;
    }
    @Step("Нажать в хедере на иконку 'Уведомления'")
    public HeaderComponent clickHeaderNotificationsIcon() {
        headerNotificationsIcon.click();
        return this;
    }
    @Step("Нажать в хедере на иконку 'Лампочка'")
    public HeaderComponent clickHeaderLampIcon() {
        headerLampIcon.click();
        return this;
    }

    @Step("Нажать в хедере на 'Аватарку'")
    public HeaderComponent clickHeaderUserAvatar() {
        headerUserAvatar.click();
        return this;
    }

    // --- Проверки ---
    @Step("Проверить, что все элементы хедера отображаются")
    public HeaderComponent verifyAllHeaderElementsVisible() {
        headerLogo.shouldBe(visible);
        headerNavHome.shouldBe(visible);
        headerNavFinance.shouldBe(visible);
        headerNavReadings.shouldBe(visible);
        headerNavServices.shouldBe(visible);
        headerNotificationsIcon.shouldBe(visible);
        headerLampIcon.shouldBe(visible);
        headerDocumentIcon.shouldBe(visible);
        headerUserAvatar.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что клик по логотипу ведет на сайт ЧЭСК")
    public HeaderComponent verifyHeaderLogoLink() {
        $(byText("Чувашская")).shouldBe(visible);
        $(byText("энергосбытовая")).shouldBe(visible);
        $(byText("компания")).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что текущий URL соответствует странице сервисов")
    public HeaderComponent verifyServicesUrl() {
        String currentUrl = WebDriverRunner.url();
        assertThat(currentUrl).as("URL должен содержать /services").contains(SERVICES);
        return this;
    }

    @Step("Проверить, что клик на 'Сервисы' открывает страницу сервисов")
    public HeaderComponent verifyNavigationToServices() {
        $$("div, span, p, a, button").findBy(text("Сервисы")).shouldBe(visible);        return this;
    }

    @Step("Проверить, что клик на 'Колокольчик' открывает окно Уведомления")
    public HeaderComponent verifyNotificationsDropdownVisible() {
        $(byText("Уведомления")).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что при клике на лампочку открывается модальное окно тура")
    public HeaderComponent verifyTourModalVisible() {
        $(byText("Добро пожаловать в обучающий тур")).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что при клике на аватарку открывается меню пользователя")
    public HeaderComponent verifyUserMenuDropdownVisible() {
        $(byText("Настройки профиля")).shouldBe(visible);
        $(byText("Выход")).shouldBe(visible);
        return this;
    }
}
