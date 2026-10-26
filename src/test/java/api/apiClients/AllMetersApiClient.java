package api.apiClients;

import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import api.models.allMeters.AllMetersResponseModel;
import api.models.allMeters.ErrorResponseModel;

import java.util.List;

import static config.Endpoints.ALLMETERS;
import static config.Endpoints.ALLMETERS_NOT_PARAM;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.allMeters.AllMetersSpec.*;

public class AllMetersApiClient {

    private static final String DATE_START = "2026-04-01";
    private static final String DATE_END = "2026-11-01";

    @Step("[API] Получение истории показаний ПУ за период с startDate по endDate")
    public List<AllMetersResponseModel> getDeviceReadingsHistory(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .queryParam("startDate", DATE_START)
                .queryParam("endDate", DATE_END)
                .when()
                .get(ALLMETERS)
                .then()
                .spec(successfulAllMetersResponseSpec)
                .extract()
                .as(new TypeRef<List<AllMetersResponseModel>>() {
                });
    }

    @Step("[API] Получение истории показаний ПУ с startDate меньше чем endDate")
    public List<AllMetersResponseModel> getAllMetersHistoryWithStartDateBeforeEndDate(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .queryParam("startDate", DATE_END)
                .queryParam("endDate", DATE_START)
                .when()
                .get(ALLMETERS)
                .then()
                .spec(successfulAllMetersResponseSpec)
                .extract()
                .as(new TypeRef<List<AllMetersResponseModel>>() {
                });
    }

    @Step("[API] Запрос истории показаний без обязательных параметров дат")
    public ErrorResponseModel getDeviceReadingsHistoryWithoutDates(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .when()
                .get(ALLMETERS)
                .then()
                .spec(missingParametersResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("[API] Запрос истории показаний без параметра DATE_START")
    public ErrorResponseModel getDeviceReadingsHistoryWithoutDATE_START(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .queryParam("endDate", DATE_END)
                .when()
                .get(ALLMETERS)
                .then()
                .spec(missingParametersResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("[API] Запрос истории показаний без параметра DATE_END")
    public ErrorResponseModel getDeviceReadingsHistoryWithoutEmptyDate(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .queryParam("startDate", "")
                .queryParam("endDate", "")
                .when()
                .get(ALLMETERS)
                .then()
                .spec(missingParametersResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("[API] Запрос истории показаний с пустыми параметра дат")
    public ErrorResponseModel getDeviceReadingsHistoryWithoutDATE_END(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .queryParam("endDate", DATE_START)
                .when()
                .get(ALLMETERS)
                .then()
                .spec(missingParametersResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("[API] Запрос истории показаний без параметра idPa")
    public Response getDeviceReadingsHistoryWithoutIdPa(String token) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .queryParam("startDate", DATE_START)
                .queryParam("endDate", DATE_END)
                .when()
                .get(ALLMETERS_NOT_PARAM)
                .then()
                .spec(error404ResponseSpec)
                .extract()
                .response();
    }
}
