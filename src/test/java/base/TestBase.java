package base;

import allure.Attach;
import api.apiClients.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.TestConfig;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import mobile.drivers.AndroidDriverProvider;
import mobile.screen.LoginScreen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ui.components.AccountsSidebarComponent;
import ui.components.HeaderComponent;
import ui.components.UnifiedAccountsSidebarComponent;
import ui.pages.*;
import utils.AccountDataHelper;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.webdriver;

public class TestBase {
    protected static final ApiClient api = new ApiClient();

    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final BasePage basePage = new BasePage();
    protected final ProfilePage profilePage = new ProfilePage();
    protected final ModalDialog modalDialog = new ModalDialog();
    protected final PaymentPage paymentPage = new PaymentPage();
    protected final ReadingsPage readingsPage = new ReadingsPage();

    protected final HeaderComponent headerComponent = new HeaderComponent();
    protected final AccountsSidebarComponent accountsSidebarComponent = new AccountsSidebarComponent();
    protected final UnifiedAccountsSidebarComponent unifiedAccountsSidebarComponent = new UnifiedAccountsSidebarComponent();

    protected final AccountDataHelper accountDataHelper = new AccountDataHelper(api);

    private static final long API_DELAY_MS = 2000;

    @BeforeAll
    public static void setUp() {
        // Настройка Selenide
        Configuration.baseUrl = TestConfig.getBaseUrl();
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        // Настройка RestAssured
        RestAssured.baseURI = TestConfig.getApiUrl();
        RestAssured.basePath = TestConfig.getBasePath();

        RestAssured.filters((Filter) (requestSpec, responseSpec, ctx) -> {
            try {
                Thread.sleep(API_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return ctx.next(requestSpec, responseSpec);
        });

    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().savePageSource(true).screenshots(true));
    }

    @AfterEach
    void tearDown() {
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
            SelenideLogger.removeListener("AllureSelenide");
        }
    }
}