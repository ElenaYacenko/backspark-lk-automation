package api.apiClients;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import api.models.meter.MeterBodyModel;

import static config.Endpoints.METER;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.meter.MeterSpec.errorResponseSpec;
import static api.specs.meter.MeterSpec.successfulSubmissionResponseSpec;

public class MeterApiClient {
    @Step("[API] Передача показаний счетчика")
    public String submitMeterReadings(String token, MeterBodyModel requestBody) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post(METER)
                .then()
                .spec(successfulSubmissionResponseSpec)
                .extract()
                .asString();
    }

    @Step("[API] Попытка передачи невалидных показаний")
    public String submitInvalidReadings(String token, MeterBodyModel requestBody) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post(METER)
                .then()
                .spec(errorResponseSpec)
                .extract()
                .asString();
    }

    @Step("[API] Передача показаний с невалидными данными (сырой JSON)")
    public String submitInvalidReadingsRaw(String token, String rawJsonBody) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(rawJsonBody)
                .when()
                .post(METER)
                .then()
                .spec(errorResponseSpec)
                .extract()
                .asString();
    }
}
