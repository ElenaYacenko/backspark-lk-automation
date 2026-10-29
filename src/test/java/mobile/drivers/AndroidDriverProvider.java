package mobile.drivers;

import com.codeborne.selenide.WebDriverProvider;
import config.MobileTestConfig;
import io.appium.java_client.android.AndroidDriver;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AndroidDriverProvider implements WebDriverProvider {

    @Override
    public WebDriver createDriver(Capabilities ignored) {
        String host = System.getProperty("deviceHost", "emulation");
        System.setProperty("env", host);
        MobileTestConfig config = ConfigFactory.create(
                MobileTestConfig.class, System.getProperties(), System.getenv());

        MutableCapabilities caps;
        String hub;

        switch (host) {
            case "emulation" -> {
                caps = localCaps(config, false);
                hub = config.appiumUrl();
            }
            case "real" -> {
                caps = localCaps(config, true);
                hub = config.appiumUrl();
            }
            case "browserstack" -> {
                caps = browserstackCaps(config);
                hub = config.browserstackHubUrl();
            }
            case "selenoid" -> {
                caps = selenoidCaps(config);
                hub = System.getProperty("remote", config.selenoidHubUrl());
            }
            default -> throw new IllegalArgumentException(
                    "Android deviceHost: emulation, real, browserstack. Got: " + host);
        }

        return new AndroidDriver(hubUrl(hub), caps);
    }

    // ===== Локальный запуск (эмулятор / реальное устройство) =====
    private static MutableCapabilities localCaps(MobileTestConfig config, boolean realDevice) {
        MutableCapabilities caps = androidCaps(config);

        // Автоматичесткая установка APK
        String apkPath = config.localAppPath();
        File apkFile = new File(apkPath);

        if (apkFile.exists()) {
            // Передаем абсолютный путь. Appium сам установит приложение на девайс!
            caps.setCapability("appium:app", apkFile.getAbsolutePath());
            System.out.println("📱 Appium автоматически установит APK из: " + apkFile.getAbsolutePath());
        } else {
            throw new IllegalStateException(
                    "❌ APK файл не найден по пути: " + apkFile.getAbsolutePath() +
                            "\nПожалуйста, убедитесь, что файл chsk.apk лежит в папке src/test/resources/apk/"
            );
        }

        caps.setCapability("appium:ignoreHiddenApiPolicyError", true);

        String udid = config.udid();
        if (realDevice && (udid == null || udid.isBlank())) {
            throw new IllegalStateException("Set -Dudid= to the USB device (adb devices)");
        }
        if (udid != null && !udid.isBlank()) {
            caps.setCapability("appium:udid", udid);
        }

        if (config.platformVersion() != null && !config.platformVersion().isBlank()) {
            caps.setCapability("appium:platformVersion", config.platformVersion());
        }
        if (config.deviceName() != null && !config.deviceName().isBlank()) {
            caps.setCapability("appium:deviceName", config.deviceName());
        }

        return caps;
    }

    // ===== BrowserStack =====
    private static MutableCapabilities browserstackCaps(MobileTestConfig config) {
        MutableCapabilities caps = androidCaps(config);

        caps.setCapability("appium:app", required("browserstack.app", config.androidApp()));
        caps.setCapability("appium:deviceName", config.androidDeviceName());
        caps.setCapability("appium:platformVersion", config.androidOsVersion());

        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", required("browserstack.user", config.browserstackUser()));
        bstack.put("accessKey", required("browserstack.key", config.browserstackKey()));
        bstack.put("projectName", config.browserstackProjectName());
        bstack.put("buildName", config.browserstackBuildName());
        bstack.put("sessionName", config.browserstackSessionName());
        bstack.put("debug", true);

        caps.setCapability("bstack:options", bstack);
        return caps;
    }

    // для Selenoid
    private static MutableCapabilities selenoidCaps(MobileTestConfig config) {
        MutableCapabilities caps = androidCaps(config);

        caps.setCapability("browserName", "android");

        caps.setCapability("version", config.selenoidAndroidVersion() != null ? config.selenoidAndroidVersion() : "11.0");

        if (config.deviceName() != null && !config.deviceName().isBlank()) {
            caps.setCapability("deviceName", config.deviceName());
        }

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", true);
        selenoidOptions.put("enableLog", true);
        caps.setCapability("selenoid:options", selenoidOptions);

        return caps;
    }

    // ===== Общие Android-капабилити =====
    private static MutableCapabilities androidCaps(MobileTestConfig config) {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName", config.platformName());
        caps.setCapability("appium:automationName", config.automationName());
        caps.setCapability("appium:appPackage", config.appPackage());
        caps.setCapability("appium:appActivity", config.appActivity());
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:noReset", false);
        caps.setCapability("appium:newCommandTimeout", 120);
        return caps;
    }

    // ===== Проверки =====
    private static String localApkPath(MobileTestConfig config) {
        String appName = config.localAppName();
        String baseUrl = config.localAppBaseUrl();

        if (appName == null || appName.isBlank()) {
            return null;
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }
        if (baseUrl.startsWith("http")) {
            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + appName;
        }
        return appName;
    }

    private static String required(String key, String value) {
        if (value == null || value.isBlank() || value.startsWith("${")) {
            throw new IllegalStateException(
                    "Set " + key + " in config/browserstack.properties or -D" + key + "=");
        }
        return value;
    }

    private static java.net.URL hubUrl(String spec) {
        try {
            return URI.create(spec).toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new IllegalStateException("Invalid hub URL: " + spec, e);
        }
    }
}