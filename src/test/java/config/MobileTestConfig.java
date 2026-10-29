package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/secret.properties",
        "classpath:config/default.properties"
})
public interface MobileTestConfig extends Config{
    // ===== Appium Server =====
    @Key("appiumUrl")
    @DefaultValue("http://127.0.0.1:4723/wd/hub")
    String appiumUrl();

    // ===== Local Android =====
    @DefaultValue("Android")
    String platformName();

    @DefaultValue("")
    String platformVersion();

    @DefaultValue("")
    String deviceName();

    @DefaultValue("")
    String udid();

    @DefaultValue("UiAutomator2")
    String automationName();

    @Key("appPackage")
    @DefaultValue("ru.bokus.chskmobile")
    String appPackage();

    @Key("appActivity")
    @DefaultValue("ru.bokus.chskmobile.MainActivity")
    String appActivity();

    @Key("noReset")
    @DefaultValue("true")
    boolean noReset();

    // ===== BrowserStack =====
    @Key("browserstack.user")
    String browserstackUser();

    @Key("browserstack.key")
    String browserstackKey();

    @Key("browserstack.hubUrl")
    @DefaultValue("https://hub.browserstack.com/wd/hub")
    String browserstackHubUrl();

    @Key("browserstack.projectName")
    @DefaultValue("ChSK Mobile Tests")
    String browserstackProjectName();

    @Key("browserstack.buildName")
    @DefaultValue("chsk-onboarding")
    String browserstackBuildName();

    @Key("browserstack.sessionName")
    @DefaultValue("android-login")
    String browserstackSessionName();

    @Key("android.deviceName")
    @DefaultValue("Google Pixel 7")
    String androidDeviceName();

    @Key("android.osVersion")
    @DefaultValue("13.0")
    String androidOsVersion();

    @Key("android.app")
    @DefaultValue("")
    String androidApp();

    // ===== Local APK =====
    @Key("local.app.path")
    @DefaultValue("src/test/resources/apk/chsk.apk")
    String localAppPath();

    @Key("local.app.name")
    @DefaultValue("chsk.apk")
    String localAppName();

    @Key("local.app.baseUrl")
    @DefaultValue("")
    String localAppBaseUrl();

    @Key("selenoid.hubUrl")
    @DefaultValue("https://user1:1234@selenoid.autotests.cloud/wd/hub") // Подставь реальный URL школы, если он другой
    String selenoidHubUrl();

    @Key("selenoid.android.version")
    @DefaultValue("11.0") // Или "15.0", в зависимости от того, какие образы есть в Selenoid школы
    String selenoidAndroidVersion();
}
