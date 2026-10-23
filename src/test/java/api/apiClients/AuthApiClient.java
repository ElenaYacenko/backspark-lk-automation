package api.apiClients;

import io.qameta.allure.Step;
import api.models.login.LoginBodyModel;

import static config.Endpoints.LOGIN;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.login.LoginSpec.successfullLoginResponseSpec;

public class AuthApiClient {

    @Step("[API] Авторизация и получение токена")
    public String loginAndGetToken(LoginBodyModel loginBody) {
        return given(getUnauthRequestSpec())
                .body(loginBody)
                .when()
                .post(LOGIN)
                .then()
                .spec(successfullLoginResponseSpec)
                .extract()
                .path("value");
    }
}
