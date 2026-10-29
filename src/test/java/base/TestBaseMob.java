package base;

import allure.Attach;
import api.apiClients.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.TestConfig;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import mobile.drivers.AndroidDriverProvider;
import mobile.screen.LoginScreen;
import mobile.screen.MainScreen;
import mobile.screen.ReadingsScreen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.AccountDataHelper;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Base64;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import com.codeborne.selenide.WebDriverRunner;

public class TestBaseMob {

    protected final LoginScreen loginScreen = new LoginScreen();
    protected final MainScreen mainScreen = new MainScreen();
    protected final ReadingsScreen readingsScreen = new ReadingsScreen();

    protected static final ApiClient api = new ApiClient();
    protected final AccountDataHelper accountDataHelper = new AccountDataHelper(api);

    private static final String LISTENER_NAME = "AllureSelenide";

    @BeforeAll
    static void beforeAll() {
        String host = System.getProperty("deviceHost", "emulation");
        System.setProperty("deviceHost", host);
        System.setProperty("env", host);

        // Общие настройки Selenide
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        Configuration.pageLoadTimeout = -1;
        Configuration.browser = AndroidDriverProvider.class.getName();
        Configuration.browserSize = null;
        Configuration.timeout = 10_000;
        Configuration.screenshots = true;
        Configuration.savePageSource = false;

        // RestAssured
        RestAssured.baseURI = TestConfig.getApiUrl();
        RestAssured.basePath = TestConfig.getBasePath();
    }

    @BeforeEach
    void beforeEach() {
        SelenideLogger.addListener(LISTENER_NAME, new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
        open();

        try {
            ((CanRecordScreen) WebDriverRunner.getWebDriver()).startRecordingScreen();
        } catch (Exception e) {
            System.out.println("Не удалось начать запись видео: " + e.getMessage());
        }
    }

    @AfterEach
    void closeApp() {
        try {
            // Получаем видео в формате Base64
            String videoBase64 = ((CanRecordScreen) WebDriverRunner.getWebDriver()).stopRecordingScreen();

            // Создаем временный файл
            File videoFile = new File("build/mobile-test-video.mp4");
            videoFile.getParentFile().mkdirs();
            Files.write(videoFile.toPath(), Base64.getDecoder().decode(videoBase64));

            // Прикрепляем видео в отчет Allure
            Allure.addAttachment("📹 Видео прогона теста", "video/mp4", new FileInputStream(videoFile), "mp4");

        } catch (Exception e) {
            System.out.println("Не удалось сохранить или прикрепить видео: " + e.getMessage());
        }

        boolean isBrowserstack = "browserstack".equals(System.getProperty("deviceHost"));
        String sessionId = isBrowserstack ? Selenide.sessionId().toString() : null;

        try {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
        } catch (Exception e) {
            Attach.attachAsText("Вложения не сняты", e.getMessage());
        } finally {
            try {
                closeWebDriver();
            } catch (Exception ignored) {
                // сессия могла уже упасть — не маскируем ошибку теста
            }
            SelenideLogger.removeListener(LISTENER_NAME);
        }

        if (isBrowserstack && sessionId != null) {
            Attach.addBrowserstackSessionLink(sessionId);
        }
    }
}