package base;

import allure.Attach;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.TestConfig;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ui.components.AccountsSidebarComponent;
import ui.components.HeaderComponent;
import ui.components.UnifiedAccountsSidebarComponent;
import ui.pages.*;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Базовый класс для UI-тестов.
**/
public class UiTestBase extends ApiTestBase {

    static {
        Configuration.baseUrl = TestConfig.getBaseUrl();
    }

    // Page Objects
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final BasePage basePage = new BasePage();
    protected final ProfilePage profilePage = new ProfilePage();
    protected final ModalDialog modalDialog = new ModalDialog();
    protected final PaymentPage paymentPage = new PaymentPage();
    protected final ReadingsPage readingsPage = new ReadingsPage();

    // Components
    protected final HeaderComponent headerComponent = new HeaderComponent();
    protected final AccountsSidebarComponent accountsSidebarComponent = new AccountsSidebarComponent();
    protected final UnifiedAccountsSidebarComponent unifiedAccountsSidebarComponent = new UnifiedAccountsSidebarComponent();

    private static final String LISTENER_NAME = "AllureSelenide";

    @BeforeEach
    void setUpUi() {
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 20000;

        SelenideLogger.addListener(LISTENER_NAME, new AllureSelenide()
                .savePageSource(true)
                .screenshots(true));
    }

    @AfterEach
    void tearDownUi() {
        try {
            if (webdriver().driver().hasWebDriverStarted()) {
                Attach.screenshotAs("Last screenshot");
                Attach.pageSource();
                Attach.browserConsoleLogs();
                Attach.addVideoOrSessionLink();
            }
        } catch (Exception e) {
            Attach.attachAsText("Вложения не сняты", e.getMessage());
        } finally {
            if (webdriver().driver().hasWebDriverStarted()) {
                closeWebDriver();
            }
            SelenideLogger.removeListener(LISTENER_NAME);
        }
    }
}
