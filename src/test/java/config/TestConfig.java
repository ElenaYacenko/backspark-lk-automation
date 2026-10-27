package config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

import java.security.Key;

public class TestConfig {

    // 1. Аннотации перенесены СЮДА, на интерфейс!
    @Config.LoadPolicy(Config.LoadType.MERGE)
    @Config.Sources({
            "system:properties",
            "system:env",
            "classpath:config/secret.properties"
    })
    public interface AppConfig extends Config {

        @Key("baseUrl")
        @DefaultValue("https://lknew.ch-sk.ru")
        String getBaseUrl();

        @Key("apiUrl")
        @DefaultValue("https://lknew-bek.ch-sk.ru")
        String getApiUrl();

        @Key("apiDBUrl")
        @DefaultValue("https://lknew")
        String getApiDBUrl();

        @Key("basePath")
        @DefaultValue("/")
        String getBasePath();

        @Key("testUser.phone")
        @DefaultValue("00000000000")
        String getTestUserPhone();

        @Key("testUser.password")
        @DefaultValue("dummy_password")
        String getTestUserPassword();

        @Key("testUser.userId")
        String getTestUserId();

        @Key("accountSingle")
        @DefaultValue("1234567890")
        String getAccountSingle();

        @Key("accountTwoTariff")
        String getAccountTwoTariff();

        @Key("accountThreeTariff")
        String getAccountThreeTariff();

        @Key("user.storage.template")
        @DefaultValue("{}")
        String getUserStorageTemplate();
    }

    // 2. Создаем экземпляр интерфейса
    private static final AppConfig config = ConfigFactory.create(
            AppConfig.class,
            System.getenv(),
            System.getProperties()
    );

    // 3. Методы-обертки
    public static String getBaseUrl() {
        return config.getBaseUrl();
    }
    public static String getApiUrl() {
        return config.getApiUrl();
    }
    public static String getBasePath() {
        return config.getBasePath();
    }
    public static String getTestUserPhone() {
        return config.getTestUserPhone();
    }
    public static String getTestUserPassword() {
        return config.getTestUserPassword();
    }

    public static String getAccountSingle() {
        return config.getAccountSingle();
    }
    public static String getAccountTwoTariff() {
        return config.getAccountTwoTariff();
    }
    public static String getAccountThreeTariff() {
        return config.getAccountThreeTariff();
    }

    public static String getUserStorageTemplate() {
        return config.getUserStorageTemplate();
    }

    // 4. Схемы ответов
    public static final String DEVICES_SUCCESS_SCHEMA = "schemas/devices/devices/devices_response-schema.json";
    public static final String DEVICES_EMPTY_SUCCESS_SCHEMA = "schemas/devices/devices/empty_devices_response_schema.json";
    public static final String METERS_SUCCESS_SCHEMA = "schemas/devices/allMeters/all-meters-response-schema.json";
    public static final String METERS_MISSING_SUCCESS_SCHEMA = "schemas/devices/allMeters/missing-parameters-schema.json";

}