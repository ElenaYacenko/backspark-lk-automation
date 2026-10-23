package mobile.tests;

import base.TestBaseMob;
import config.TestConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import utils.AccountDataHelper;

import static io.qameta.allure.Allure.step;

@Epic("Мобильное приложение")
@Feature("Показания")
@Story("Передача показаний")
@Owner("Elena Yatsenko")
public class MobileReadingsPageTest extends TestBaseMob {

    @BeforeEach
    void setUpReadingsPage() {
        step("MOB: Логинимся в мобильном приложении и переходим на 'Главную' страницу", () -> {
            loginScreen
                    .closeUpdateModalIfPresent()
                    .clickSearch()
                    .enterLogin(TestConfig.getTestUserPhone())
                    .enterPassword(TestConfig.getTestUserPassword())
                    .clickLogin()
                    .closePaymentsConfirmationModal();
            mainScreen.clickSubmitReadingsButtonVisible();
        });
    }

    @Test
    @DisplayName("MOB-READING-001: Отображение страницы 'Передать показания'")
    @Description("""
            Цель: Проверить корректное отображение основных элементов страницы передачи показаний.
            Дано: Авторизованный пользователь, открыта страница 'Передать показания'.
            ОР: Отображаются заголовок страницы 'Показания' и вкладки 'Передать' и 'История'.
            """)
    @Tags({@Tag("regression"), @Tag("mobile")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayReadingsPage() {
        step("MOB: проверяем наличие заголовка страницы 'Показания'", () -> {
            readingsScreen.verifyPageTitle();
        });
        step("MOB: проверяем наличие вкладок 'Передать' и 'История'", () -> {
            readingsScreen
                    .verifySubmitTabVisible()
                    .verifyHistoryTabVisible();
        });
    }

    @Test
    @DisplayName("MOB-READING-002: Отображение деталей ПУ лицевого счёта")
    @Description("""
            Цель: Проверить отображение информации о приборе учёта выбранного лицевого счёта.
            Дано: Авторизованный пользователь, открыта страница 'Передать показания'; данные счёта получены из API.
            ОР: Отображаются номер лицевого счёта, название и модификация ПУ, 
            серийный номер и дата следующей поверки, совпадающие с данными API..
            """)
    @Tags({@Tag("regression"), @Tag("mobile")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayMeterDeviceDetails() {
        AccountDataHelper.ActiveAccountData data = accountDataHelper.getFirstActiveAccountData();

        step("MOB: проверяем номер ЛС в заголовке страницы", () -> {
            readingsScreen
                    .verifyAccountNumberDisplayed(data.accountNumber());
        });
        step("MOB: проверяем  данные прибора учёта", () -> {
            readingsScreen.verifyMeterDeviceTitleDisplayed(data.deviceTitle(), data.modification(), data.serialNumber());
        });
        step("MOB: проверяем дату следующей поверки", () -> {
            readingsScreen.verifyVerificationDateDisplayed(data.formattedDateCheckNext());
        });
    }

    @Test
    @DisplayName("MOB-READING-003: Отображение последнего показания")
    @Description("""
            Цель: Проверить отображение последнего переданного показания и тарифа.
            Дано: Авторизованный пользователь, открыта страница 'Передать показания'; данные счёта получены из API.
            ОР: Отображается секция с названием тарифа и последним показанием, совпадающим с данными API.
            """)
    @Tags({@Tag("regression"), @Tag("mobile")})
    @Severity(SeverityLevel.CRITICAL)
    void shouldDisplayLastReading() {
        AccountDataHelper.ActiveAccountData data = accountDataHelper.getFirstActiveAccountData();

        step("MOB: проверяем номер ЛС в заголовке страницы", () -> {
            readingsScreen
                    .verifyAccountNumberDisplayed(data.accountNumber());
        });
        step("MOB: раскрываем карточку прибора учёта", () -> {
            readingsScreen.clickVerificationDateDisplayed();
        });
        step("MOB: проверяем, что последнее показание совпадает с API", () -> {
            readingsScreen.verifyLastReading(data.lastReading());
        });
    }
}
