package ui.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import base.UiTestBase;
import extensions.WithLogin;

@Epic("Личный кабинет")
@Feature("Шапка приложения (Header)")
@Story("Навигация по основному меню и интерактивные элементы")
@Owner("Elena Yatsenko")
public class HeaderAndNavigationTest extends UiTestBase {

    @BeforeEach
    void setUpHeaderTests() {
        mainPage.openMainPage();
        modalDialog.modalFKROpened();
        modalDialog.clickCloseButton();
    }

    @Test
    @DisplayName("UI-HEADER-001: Отображение элементов шапки на главной странице")
    @Description("""
            Цель: Проверить отображение всех элементов шапки сайта.
            Дано: Авторизованный пользователь находится на главной странице.
            ОР: Все элементы хедера (логотип, навигация, иконки) отображаются корректно и видны.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldDisplayAllHeaderElementsOnMainPage() {
        headerComponent.verifyAllHeaderElementsVisible();
    }

    @Test
    @DisplayName("UI-HEADER-002: Переход на сайт ЧЭСК по клику на логотип")
    @Description("""
            Цель: Проверить переход на внешний сайт ЧЭСК при клике на логотип.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Происходит переход (или открытие новой вкладки) на сайт ЧЭСК.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingLogo() {
        headerComponent.clickHeaderLogo();
        headerComponent.verifyHeaderLogoLink();
    }

    @Test
    @DisplayName("UI-HEADER-003: Переход на главную страницу по клику на кнопку 'Главная'")
    @Description("""
            Цель: Проверить переход на главную страницу при клике на кнопку 'Главная'.
            Дано: Авторизованный пользователь находится на странице "Показания".
            ОР: Происходит переход на главную страницу, URL и элементы главной страницы отображаются корректно.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToHomeWhenClickingHomeButton() {
        readingsPage.openReadingsPage();
        headerComponent.clickHeaderNavHome();
        mainPage.verifyHomeUrl();
        headerComponent.verifyAllHeaderElementsVisible();
    }

    @Test
    @DisplayName("UI-HEADER-004: Переход на страницу 'Финансы' по клику на кнопку")
    @Description("""
            Цель: Проверить переход на страницу "Финансы" при клике на соответствующую кнопку.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Происходит переход на страницу "Финансы", URL и заголовок страницы отображаются корректно.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingFinance() {
        headerComponent.clickHeaderNavFinance();
        paymentPage
                .verifyFinanceUrl()
                .verifyNavigationToFinance();
    }

    @Test
    @DisplayName("UI-HEADER-005: Переход на страницу 'Показания' по клику на кнопку")
    @Description("""
            Цель: Проверить переход на страницу "Показания" при клике на соответствующую кнопку.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Происходит переход на страницу "Показания", отображаются кнопки "Передать показания" и "История показаний".
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingReadings() {
        headerComponent.clickHeaderNavReadings();
        readingsPage.verifyPageUrl();
        readingsPage.verifyReadingSubmitAndHistoryButtons();
    }

    @Test
    @DisplayName("UI-HEADER-006: Переход на страницу 'Сервисы' по клику на кнопку")
    @Description("""
            Цель: Проверить переход на страницу "Сервисы" при клике на соответствующую кнопку.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Происходит переход на страницу "Сервисы", URL и заголовок страницы отображаются корректно.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingServices() {
        headerComponent.clickHeaderNavServices();
        headerComponent
                .verifyServicesUrl()
                .verifyNavigationToServices();
    }

    @Test
    @DisplayName("UI-HEADER-007: Открытие окна уведомлений по клику на иконку 'Колокольчик'")
    @Description("""
            Цель: Проверить открытие выпадающего списка уведомлений при клике на иконку.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Открывается выпадающее окно (dropdown) с заголовком "Уведомления".
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingNotifications() {
        headerComponent.clickHeaderNotificationsIcon();
        headerComponent.verifyNotificationsDropdownVisible();
    }

    @Test
    @DisplayName("UI-HEADER-008: Открытие окна обучающего тура по клику на иконку 'Лампочка'")
    @Description("""
            Цель: Проверить открытие модального окна обучающего тура при клике на иконку.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Открывается модальное окно с приветствием обучающего тура.
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldOpenTourModalWhenClickingLampIcon() {
        headerComponent.clickHeaderLampIcon();
        headerComponent.verifyTourModalVisible();
    }

    @Test
    @DisplayName("UI-HEADER-009: Открытие меню пользователя при клике на 'Аватарку'")
    @Description("""
            Цель: Проверить открытие меню пользователя при клике на аватар.
            Дано: Авторизованный пользователь на главной странице, модальное окно ФКР закрыто.
            ОР: Открывается выпадающее меню с пунктами "Настройки профиля" и "Выход".
            """)
    @Tags({@Tag("regression"), @Tag("positive"), @Tag("header")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void shouldNavigateToCheskSiteWhenClickingUserAvatar() {
        headerComponent.clickHeaderUserAvatar();
        headerComponent.verifyUserMenuDropdownVisible();
    }
}
