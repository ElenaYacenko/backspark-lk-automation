package ui.tests;

import base.UiTestBase;
import extensions.WithLogin;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import utils.AccountDataHelper;

@Epic("Показания")
@Feature("Показания в ЛКК")
@Story("Страница передачи показаний")
@Owner("Elena Yatsenko")

public class ReadingsPageTest extends UiTestBase {
    public static final String READING_PERIOD_INFO = "Передача показаний возможна с 1 по 25 число включительно";
    public static final String READINGS_ERROR_MESSAGE = "Ваши показания меньше предыдущих";

    @BeforeEach
    void setUpReadingsPage() {
        readingsPage
                .openReadingsPage()
                .clickHistoryReadingsTabs()
                .clickSubmitReadingsTabs();
    }

    @Test
    @DisplayName("UI-READING-001:  Отображение страницы 'Передать показания'")
    @Description("""
            Цель: Проверить корректное отображение основных элементов страницы.
            Дано: Авторизованный пользователь.
            ОР: Все элементы страницы отображаются корректно.
            """)
    @Tags({@Tag("smoke"), @Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testDisplayReadingsPage() {
        readingsPage.verifyReadingSubmitAndHistoryButtons();
        readingsPage.verifyAccountsAndUnifiedAccountsSection();
    }

    @Test
    @DisplayName("UI-READING-002: Отображение деталей ПУ лицевого счета")
    @Description("""
            Цель: Проверить отображение информации о ПУ выбранного лицевого счета.
            Дано: Выбран первый активный лицевой счет.
            ОР: Отображается полная информация о счете и ПУ (название, модификация, серийный номер, дата поверки).
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testDisplayAccountDetails() {
        AccountDataHelper.ActiveAccountData data = accountDataHelper.getFirstActiveAccountData();

        readingsPage
                .selectAccountCard(data.accountNumber())
                .verifyAccountNumberIsDisplayed(data.accountNumber());
        readingsPage.verifyMeterDeviceData(data.deviceTitle(), data.modification(), data.serialNumber());
        readingsPage.verifyVerificationDate(data.formattedDateCheckNext());
    }

    @Test
    @DisplayName("UI-READING-003: Отображение последнего показания")
    @Description("""
            Цель: Проверить отображение последнего переданного показания и тарифа.
            Дано: Авторизованный пользователь, выбран первый активный лицевой счет.
            ОР: Отображается секция с названием Тарифа и последним показанием.
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testDisplayLastReading() {
        AccountDataHelper.ActiveAccountData data = accountDataHelper.getFirstActiveAccountData();

        readingsPage.selectAccountCard(data.accountNumber())
                .verifyAccountNumberIsDisplayed(data.accountNumber());
        readingsPage.clickMeterDeviceToggle();
        readingsPage.verifyTariffTitle(data.tariffTitle());
        readingsPage.verifyLastReading(data.lastReading());
    }

    @Test
    @Disabled("Временно отключен чтобы не засорять БД")
    @DisplayName("UI-READING-004: Успешная передача показаний")
    @Description("""
            Цель: Проверить успешную передачу новых показаний.
            Дано: Авторизованный пользователь, выбран лицевой счет, текущее показание N.
            ОР: Показания успешно передаются, отображается модальное окно с сообщением об успехе, разница рассчитана корректно.
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.CRITICAL)
    @WithLogin
    void testSubmitReadingsSuccessfully() {

        readingsPage.clickMeterDeviceToggle();

        int tariffZoneCount = readingsPage.getTariffZoneCount();

        for (int i = 0; i < tariffZoneCount; i++) {
            final int idx = i;
            int previousReading = readingsPage.getLastReadingByZoneIndex(idx);
            int newReading = previousReading + 5;
            readingsPage.enterReadingByZoneIndex(idx, String.valueOf(newReading));
        }

        for (int i = 0; i < tariffZoneCount; i++) {
            final int idx = i;
            readingsPage.verifyDifferenceByZoneIndex(idx, 5);
        }

        readingsPage.clickSubmitReadingsButton();
        readingsPage.checkSuccessDialogIsVisible()
                .closeSuccessDialog();
    }

    @Test
    @DisplayName("UI-READING-005: Отображение периода передачи показаний")
    @Description("""
            Цель: Проверить отображение информации о периоде передачи показаний.
            Дано: Авторизованный пользователь, текущая дата в периоде 1–25 число.
            ОР:  Отображается сообщение "Сейчас время передачи показаний".
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void testDisplayTransmissionPeriod() {

        readingsPage.clickMeterDeviceToggle();
        readingsPage.verifyReadingPeriodInfo(READING_PERIOD_INFO);
    }

    @Test
    @DisplayName("UI-READING-006: Валидация: показания меньше предыдущих")
    @Description("""
            Цель:  Проверить валидацию при вводе показания меньше предыдущего.
            Дано:  Авторизованный пользователь, последнее показание N.
            ОР: Отображается ошибка "Ваши показания меньше предыдущих".
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void testReadingLessThanPrevious() {

        readingsPage.clickMeterDeviceToggle();
        int previousReading = readingsPage.getLastReading();
        int newReading = previousReading - 5;
        readingsPage.enterReading(String.valueOf(newReading));
        readingsPage.verifyTariffZoneError(READINGS_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("UI-READING-007: Валидация: показания равны предыдущим")
    @Description("""
            Цель: Проверить валидацию при вводе показания, равного предыдущему.
            Дано: Авторизованный пользователь, последнее учтённое показание N.
            ОР: Отображается "Разница: 0 кВтч", поле не подсвечивается.
            Примечание: Тест поддерживает 1/2/3 тарифные зоны.
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void testReadingEqualToPrevious() {

        readingsPage.clickMeterDeviceToggle();

        int tariffZoneCount = readingsPage.getTariffZoneCount();

        for (int i = 0; i < tariffZoneCount; i++) {
            final int idx = i;
            int previousReading = readingsPage.getLastReadingByZoneIndex(idx);
            readingsPage.enterReadingByZoneIndex(idx, String.valueOf(previousReading));
        }

        for (int i = 0; i < tariffZoneCount; i++) {
            final int idx = i;
            readingsPage.verifyDifferenceByZoneIndex(idx, 0);
        }

        readingsPage.clickSubmitReadingsButton();
        readingsPage.checkSuccessDialogIsVisible()
                .closeSuccessDialog();
    }

    @ParameterizedTest(name = "Ввод ''{0}'' → в поле остаётся ''{1}''")
    @DisplayName("UI-READING-008: Валидация: санитайзинг ввода показаний (макс. 8 цифр, без букв и знаков)")
    @Description("""
            Цель: Проверить, что поле ввода показаний корректно обрабатывает невалидные символы.
            Правила: 
            1. Допускаются только цифры.
            2. Точки, запятые, минусы, буквы и спецсимволы игнорируются/удаляются.
            3. Максимальная длина значения — 8 символов (излишек обрезается).
            Дано: Авторизованный пользователь, выбрана карточка ПУ.
            ОР: В поле ввода остаётся только корректная строка из цифр длиной не более 8 символов.
            """)
    @Tags({@Tag("regression"), @Tag("validation"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    @CsvSource({
            "'99999999',         '99999999'",   // Максимально допустимое (8 цифр)
            "'123.58',           '12358'",      // Точка игнорируется
            "'12,6',             '126'",        // Запятая игнорируется (твой главный кейс)
            "'-123',             '123'",        // Отрицательные значения
            "'12abc34',          '1234'",       // Проверяем, что чистит не только по краям
            "'abc',              ''",           // Только буквы -> пустая строка
            "'123456789',        '12345678'"    // 9 цифр -> жесткая обрезка до 8
    })
    void testReadingInputSanitization(String input, String expected) {

        readingsPage.clickMeterDeviceToggle();
        readingsPage.enterReading(input);
        readingsPage.verifyReadingInputValue(expected);
    }

    @Test
    @DisplayName("UI-READING-009: Валидация: пустое поле при отправке")
    @Description("""
            Цель: Проверить валидацию при попытке отправить пустое поле.
            Дано: Авторизованный пользователь, поле ввода показаний пустое.
            ОР:Отображается ошибка.
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void testEmptyReadingSubmission() {

        readingsPage.clickMeterDeviceToggle();
        readingsPage.clickSubmitReadingsButton();
        readingsPage.verifyReadingInputHasErrorBorder();
    }


    @Test
    @DisplayName("UI-READING-010: Переключение между лицевыми счетами")
    @Description("""
            Цель: Проверить корректное переключение между разными лицевыми счетами.
            Дано: Авторизованный пользователь с несколькими активными счетами.
            ОР: При выборе другого счета отображаются его данные, при возврате — данные первого счета.
            """)
    @Tags({@Tag("regression"), @Tag("ui")})
    @Severity(SeverityLevel.NORMAL)
    @WithLogin
    void testSwitchBetweenAccounts() {
        AccountDataHelper.TwoAccountsData accounts = accountDataHelper.getTwoActiveAccountData();

        readingsPage.selectAccountCard(accounts.first().accountNumber());
        readingsPage.verifyAccountNumberIsDisplayed(accounts.first().accountNumber());
        readingsPage.waitForDeviceDataToLoad();
        readingsPage.verifyMeterDeviceData(
                accounts.first().deviceTitle(),
                accounts.first().modification(),
                accounts.first().serialNumber()
        );

        readingsPage.selectAccountCard(accounts.second().accountNumber());
        readingsPage.verifyAccountNumberIsDisplayed(accounts.second().accountNumber());
        readingsPage.waitForDeviceDataToLoad();
        readingsPage.verifyMeterDeviceData(
                accounts.second().deviceTitle(),
                accounts.second().modification(),
                accounts.second().serialNumber()
        );

        readingsPage.selectAccountCard(accounts.first().accountNumber());
        readingsPage.verifyAccountNumberIsDisplayed(accounts.first().accountNumber());
        readingsPage.waitForDeviceDataToLoad();
        readingsPage.verifyMeterDeviceData(
                accounts.first().deviceTitle(),
                accounts.first().modification(),
                accounts.first().serialNumber()
        );
    }
}
