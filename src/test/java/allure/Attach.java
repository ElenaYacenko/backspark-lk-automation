package allure;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.sessionId;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class Attach {

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] screenshotAs(String attachName) {
        if (isApiOnly()) {
            return new byte[0];
        }
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source", type = "text/html")
    public static byte[] pageSource() {
        if (isApiOnly()) {
            return new byte[0];
        }
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "{attachName}", type = "text/plain")
    public static String attachAsText(String attachName, String message) {
        return message;
    }

   /** Browser console logs. Веб — работает, Android/BrowserStack — недоступно, API — WebDriver нет. */
    public static void browserConsoleLogs() {
        if (isApiOnly() || isMobile()) {
            return;
        }
        try {
            attachAsText(
                    "Browser console logs",
                    String.join("\n", Selenide.getWebDriverLogs(BROWSER))
            );
        } catch (Exception ignored) {
                   }
    }

    /**
     * Универсальный метод: BrowserStack → ссылка на сессию, Selenoid → встроенное видео, API → ничего.
     */
    public static void addVideoOrSessionLink() {
        if (isApiOnly()) {
            return;
        }
        if (isBrowserstack()) {
            addBrowserstackSessionLink(sessionId().toString());
        } else {
            addSelenoidVideo(sessionId().toString());
        }
    }

    @Attachment(value = "BrowserStack session", type = "text/html", fileExtension = ".html")
    public static String addBrowserstackSessionLink(String sessionId) {
        String url = "https://app-automate.browserstack.com/dashboard/v2/builds/sessions/" + sessionId;
        return "<html><body><a href='" + url + "' target='_blank'>"
                + "Открыть сессию в BrowserStack: " + sessionId
                + "</a></body></html>";
    }

    @Attachment(value = "Video", type = "text/html", fileExtension = ".html")
    public static String addSelenoidVideo(String sessionId) {
        String videoUrl = "https://selenoid.autotests.cloud/video/" + sessionId + ".mp4";
        return "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                + videoUrl
                + "' type='video/mp4'></video></body></html>";
    }

    private static boolean isApiOnly() {
        return !WebDriverRunner.hasWebDriverStarted();
    }

    private static boolean isMobile() {
        return System.getProperty("deviceHost") != null;
    }

    private static boolean isBrowserstack() {
        return "browserstack".equals(System.getProperty("deviceHost"));
    }
}