package base;

import api.apiClients.ApiClient;
import config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import org.junit.jupiter.api.BeforeAll;
import utils.AccountDataHelper;

/**
 * Базовый класс для API-тестов.
 **/
public class ApiTestBase {

    protected static final ApiClient api = new ApiClient();
    protected final AccountDataHelper accountDataHelper = new AccountDataHelper(api);

    @BeforeAll
    public static void setUpApi() {
        RestAssured.baseURI = TestConfig.getApiUrl();
        RestAssured.basePath = TestConfig.getBasePath();

        // Это нужно для обхода rate-limit
        RestAssured.filters((Filter) (requestSpec, responseSpec, ctx) -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return ctx.next(requestSpec, responseSpec);
        });
    }
}
