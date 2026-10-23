package ui.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import base.TestBase;
import extensions.WithLogin;

import static io.qameta.allure.Allure.step;

@Epic("Личный кабинет")
@Feature("Шапка приложения (Header)")
@Story("Навигация по основному меню и интерактивные элементы")
@Owner("Elena Yatsenko")
public class HeaderAndNavigationTest extends TestBase {

    @BeforeEach
    void setUpHeaderTests() {
        step("UI: Открываем главную страницу и закрываем приветственную модалку (если есть)", () -> {
            mainPage.openMainPage();
            modalDialog.modalFKROpened();
            modalDialog.clickCloseButton();
        });
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
        step("UI: Проверяем отображение всех элементов хедера", () -> {
            headerComponent.verifyAllHeaderElementsVisible();
        });
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
        step("UI: Кликаем на Логотип", () -> {
            headerComponent.clickHeaderLogo();
        });
        step("UI: Проверить открытие сайта 'ЧЭСК'", () -> {
            headerComponent.verifyHeaderLogoLink();
        });
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
        step("UI: Открываем страницу передачи показаний", () -> {
            readingsPage.openReadingsPage();
        });
        step("UI: Кликаем на Главная", () -> {
            headerComponent.clickHeaderNavHome();
        });
        step("UI: Проверяем переход на главную страницу и отображение элементов хедера", () -> {
            mainPage.verifyHomeUrl();
            headerComponent.verifyAllHeaderElementsVisible();
        });
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
        step("UI: Кликаем на Финансы", () -> {
            headerComponent.clickHeaderNavFinance();
        });
        step("UI: Проверяем переход на страницу финансы", () -> {
            paymentPage
                    .verifyFinanceUrl()
                    .verifyNavigationToFinance();
        });
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
        step("UI: Кликаем на 'Показания' в хедере", () -> {
            headerComponent.clickHeaderNavReadings();
        });
        step("UI: проверяем URL страницы 'Показания'", () -> {
            readingsPage.verifyPageUrl();
        });
        step("UI: Проверяем наличие кнопок 'Передать показания' и 'История показаний'", () -> {
            readingsPage.verifyReadingSubmitAndHistoryButtons();
        });
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
        step("UI: Кликаем на Сервисы", () -> {
            headerComponent.clickHeaderNavServices();
        });
        step("UI: Проверяем переход на страницу сервисов", () -> {
            headerComponent
                    .verifyServicesUrl()
                    .verifyNavigationToServices();
        });
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
        step("UI: Кликаем на иконку 'Колокольчик'", () -> {
            headerComponent.clickHeaderNotificationsIcon();
        });
        step("UI: Проверяем открытое окно с уведомлениями", () -> {
            headerComponent.verifyNotificationsDropdownVisible();
        });
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
        step("UI: Кликаем на иконку 'Лампочка'", () -> {
            headerComponent.clickHeaderLampIcon();
        });
        step("UI: Проверяем открытое приветственное окно обучающего тура", () -> {
            headerComponent.verifyTourModalVisible();
        });
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
        step("UI: Кликаем на иконку 'Аватарка'", () -> {
            headerComponent.clickHeaderUserAvatar();
        });
        step("UI: Проверяем открытое меню пользователя с кнопками 'Настройки меню' и 'Выход'", () -> {
            headerComponent.verifyUserMenuDropdownVisible();
        });
    }
}
