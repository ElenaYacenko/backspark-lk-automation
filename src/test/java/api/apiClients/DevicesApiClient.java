package api.apiClients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import api.models.devices.DevicesResponseModel;

import static config.Endpoints.DEVICES;
import static config.Endpoints.DEVICES_NOT_PARAM;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.devices.DevicesSpec.*;

public class DevicesApiClient {
    @Step("[API] Получение информации о ПУ пользователя")
    public DevicesResponseModel userDevicesInfo(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .when()
                .get(DEVICES)
                .then()
                .spec(successfullDevicesResponseSpec)
                .extract()
                .as(DevicesResponseModel.class);
    }

    @Step("[API] Запрос ПУ для абонента без приборов учёта")
    public DevicesResponseModel userEmptyDevicesInfo(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .when()
                .get(DEVICES)
                .then()
                .spec(successfullEmptyDevicesResponseSpec)
                .extract()
                .as(DevicesResponseModel.class);
    }

    @Step("[API] Запрос ПУ для ЛС, не принадлежащего абоненту")
    public Response accountNotInLKK(String token, int idPa) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id_pa", idPa)
                .when()
                .get(DEVICES)
                .then()
                .spec(unauthorizedResponseSpec)
                .extract()
                .response();
    }

    @Step("[API] Попытка получить информацию без параметра id_pa")
    public Response errorMissingParameter(String token) {
        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .get(DEVICES_NOT_PARAM)
                .then()
                .spec(missingParameterResponseSpec)
                .extract()
                .response();
    }
}
