package base;

import allure.Attach;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.TestConfig;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;
import ui.components.AccountsSidebarComponent;
import ui.components.HeaderComponent;
import ui.components.UnifiedAccountsSidebarComponent;
import ui.pages.*;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Базовый класс для UI-тестов.
**/
public class UiTestBase extends ApiTestBase {

    static {
        Configuration.baseUrl = System.getProperty("url", TestConfig.getBaseUrl());
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
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
       // Configuration.browserVersion = System.getProperty("browserVersion", "156.0");
        Configuration.timeout = 10000;

        String remote = System.getProperty("remote");
        if (remote != null && !remote.isEmpty()) {
            Configuration.remote = remote;
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", true,
                    "enableLog", true
            ));
            Configuration.browserCapabilities = capabilities;
        }

        // Логирование для проверки
        System.out.println("URL: " + Configuration.baseUrl);
        System.out.println("Browser: " + Configuration.browser);
        System.out.println("Browser size: " + Configuration.browserSize);
        System.out.println("Remote: " + Configuration.remote);

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
