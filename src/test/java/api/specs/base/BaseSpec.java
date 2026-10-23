package api.specs.base;

import io.restassured.specification.RequestSpecification;

import static allure.CustomAllureListener.withCustomTemplate;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;

public class BaseSpec {

    public static RequestSpecification getUnauthRequestSpec() {
        return with()
                .filter(withCustomTemplate())
                .log().all()
                .contentType(JSON)
                .basePath("/api/v0");
    }
}