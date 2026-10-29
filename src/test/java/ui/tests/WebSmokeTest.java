package ui.tests;

import config.TestConfig;
import io.qameta.allure.*;
import api.models.userProfile.UserProfileResponseModel;
import org.junit.jupiter.api.*;
import base.UiTestBase;
import extensions.WithLogin;
import utils.AuthContext;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Epic("Smoke-тестирование ЛК физических лиц")
@Feature("Авторизация")
@Story("Вход в систему")
@Owner("Elena Yatsenko")
public class WebSmokeTest extends UiTestBase {

    @Test
    @DisplayName("TC-SMOKE-001: Успешная авторизация валидного пользователя")
    @Description("""
            Цель: Проверить работоспособность механизма авторизации
            Дано: Зарегистрированный пользователь с валидными credentials
            ОР: Пользователь авторизован, редирект на главную, токен сохранён
            """)
    @Tags({@Tag("smoke"), @Tag("critical"), @Tag("web")})
    @Severity(SeverityLevel.CRITICAL)
    void testSuccessfulLogin() {

        final String phone = TestConfig.getTestUserPhone();
        final String password = TestConfig.getTestUserPassword();

        step("UI: открытие страницы авторизации, проверка заголовка страницы", () -> {
            loginPage
                    .open()
                    .loginPageShouldBeOpened();
        });
        step("UI: Ввести логин и пароль, нажать кнопку Войти", () -> {
            loginPage
                    .enterPhone(phone)
                    .enterPassword(password)
                    .clickLogin();
        });
        step("UI: Проверка редиректа на главную и отображения элементов хедера'", () -> {
            headerComponent.verifyAllHeaderElementsVisible();
        });
    }

    @Test
    @DisplayName("TC-SMOKE-002: Успешный выход из личного кабинета")
    @Description("""
            Цель: Проверить корректное завершение сессии
            Дано: Авторизованный пользователь
            ОР: Сессия уничтожена, редирект на страницу логина
            """)
    @Tags({@Tag("smoke"), @Tag("critical"), @Tag("web")})
    @Severity(SeverityLevel.CRITICAL)
   // @WithLogin
    void testSuccessfulLogout() {

        basePage.openBlankPageWithExistingUser();
        
        step("UI: Открыть страницу профиля", () -> {
            profilePage.openProfilePage();
        });

        step("UI: Нажать кнопку 'Выйти из аккаунта'", () -> {
            profilePage.clickToSignOut();
        });
        step("UI: Подтвердить выход из аккаунта в модальном окне", () -> {
            profilePage.clickSignOutButton();
        });
      step("UI: Принудительная очистка сессии (эмуляция ответа бэкенда)", () -> {
            // Очищаем все куки, как это делает браузер после успешного logout
            clearBrowserCookies();
            clearBrowserLocalStorage();
            refresh();
        });
        step("UI: Проверка редиректа на welcome страницу", () -> {
            loginPage.mainPageShouldBeOpened();
        });
    }

    @Test
    @DisplayName("TC-SMOKE-003: Загрузка главной страницы и отображение лицевых счетов")
    @Description("""
            Цель: Проверка корректной загрузки главной страницы ЛК и отображения лицевых счетов
            Дано: Авторизованный пользователь с несколькими лицевыми счетами
            Когда: Открывается главная страница после авторизации
            Тогда: 
              - Загружается главная страница
              - Отображается модальное окно с информацией о ФКР
              - После закрытия окна отображаются ЛС и ЕЛС
            """)
    @Tags({@Tag("smoke"), @Tag("critical"), @Tag("ui"), @Tag("main-page")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testMainPageLoadAndAccountsDisplay() {
        step("UI: Открыть главную страницу", () -> {
            mainPage.openMainPage();
        });
        step("UI: Проверить появление модального окна с информацией о ФКР", () -> {
            modalDialog.modalFKROpened();
        });
        step("UI: Закрыть модальное окно с информацией о ФКР", () -> {
            modalDialog.clickCloseButton();
        });
        step("UI: Проверить отображение списка ЛС и ЕЛС", () -> {
            accountsSidebarComponent.verifyPersonalAccounts();
            unifiedAccountsSidebarComponent.verifyUnifiedAccounts();
        });
    }

    @Test
    @DisplayName("TC-SMOKE-004: Инициация оплаты - форма загружена")
    @Description("""
            Цель: Проверить, что форма оплаты корректно загружается.
            Дано: Авторизованный пользователь на главной странице с выбранным лицевым счетом.
            Когда: Пользователь нажимает кнопку 'Оплатить' на главной странице.
            Тогда: Открывается форма оплаты, реквизиты подтянуты, URL /finance.
            Примечание: Реальный платеж не проводится.
            """)
    @Tags({@Tag("smoke"), @Tag("critical"), @Tag("ui"), @Tag("payment")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testPaymentInitiation() {
        step("UI: Открыть главную страницу", () -> {
            mainPage.openMainPage();
            modalDialog
                    .modalFKROpened()
                    .clickCloseButton();
        });
        step("UI: Выбрать первый ЕЛС в списке ЕЛС", () -> {
            mainPage.clickFirstUnifiedAccountCard();
        });
        step("UI: Инициация оплаты: Нажать кнопку 'Оплатить' и перейти на форму оплаты.", () -> {
            mainPage.clickGetPaymentTabButton();
            paymentPage.verifyPaymentFormTitle();
        });
        step("UI: Проверить переход на страницу оплаты", () -> {
            webdriver().shouldHave(
                    urlContaining("/finance"),
                    Duration.ofSeconds(10)
            );
            webdriver().shouldHave(
                    urlContaining("activeTab=3"),
                    Duration.ofSeconds(5)
            );
        });
    }

    @Test
    @Disabled("Временно отключен чтобы не засорять БД")
    @DisplayName("TC-SMOKE-005: Успешная передача показаний счётчика")
    @Description("""
            Цель: Проверить полный цикл передачи показаний
            Дано: Авторизованный пользователь с ПУ, доступным для передачи.
            Когда: Пользователь нажимает кнопку 'передать показания'.
            Тогда: Показание принято, отображается модалка успеха, после закрытия модалки карточка возвращается в исходное состояние.
            """)
    @Tags({@Tag("smoke"), @Tag("critical"), @Tag("ui"), @Tag("readings")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testSuccessfulMeterReadingSubmission() {
        step("UI: Открыть страницу 'Передать показания'", () -> {
            readingsPage.openReadingsPage();
        });
        step("UI: Раскрыть карточку ПУ", () -> {
            readingsPage.clickMeterDeviceToggle();
        });
        step("UI: Ввести показание на 1 больше предыдущего", () -> {
            int previousReading = readingsPage.getLastReading();
            int newReading = previousReading + 1;
            readingsPage.enterReading(String.valueOf(newReading));
        });
        step("UI: Проверить, что разница между новым и предыдущим показанием = 1 кВт·ч", () -> {
            readingsPage.verifyDifference(1);
        });
        step("UI: Нажать кнопку 'Отправить'", () -> {
            readingsPage.clickSubmitReadingsButton();
        });
        step("UI: Проверить модалку успешной передачи", () -> {
            readingsPage
                    .checkSuccessDialogIsVisible()
                    .closeSuccessDialog();
        });
    }

    @Test
    @DisplayName("TC-SMOKE-006: Доступ к странице профиля и сверка имени с API")
    @Description("""
            Цель: Проверить, что имя и фамилия на странице профиля совпадают с API.
            Дано: Авторизованный пользователь.
            Когда: Выполняется переход на /profile и запрос GET /cabinet/user.
            Тогда: Имя и фамилия в заголовке профиля совпадают с данными из API.
            """)
    @Tags({@Tag("smoke"), @Tag("ui"), @Tag("profile"), @Tag("regression")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testProfilePageAccess() {
        UserProfileResponseModel apiProfile = fetchApiProfile();

        step("UI: Открыть страницу профиля", () -> {
            profilePage.openProfilePage();
        });

        step("UI: Сверка имени и фамилии в заголовке с данными API", () -> {
            String actualHeaderName = profilePage.getUserHeaderNameText();
            String expectedHeaderName = apiProfile.firstName() + " " + apiProfile.lastName();

            assertThat(actualHeaderName)
                    .as("Заголовок профиля должен содержать имя и фамилию из API")
                    .isEqualTo(expectedHeaderName);
        });
    }

    @Step("[API] Получить информацию о пользователе GET /cabinet/user")
    private UserProfileResponseModel fetchApiProfile() {
        return api.user.userGetInfo(AuthContext.getToken());
    }
}


