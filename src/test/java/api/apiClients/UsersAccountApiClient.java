package api.apiClients;

import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import api.models.userAccount.UsersAccountResponseModel;
import utils.JwtUtils;

import java.util.List;

import static config.Endpoints.USER_ACCOUNT;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.users.UsersSpec.successfullUsersResponseSpec;

public class UsersAccountApiClient {
    @Step("[API] Получение информации о ЛС пользователя")

    public List<UsersAccountResponseModel> accountGetInfo(String token) {
        String userId = JwtUtils.extractSub(token);

        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .queryParam("id_user", userId)
                .when()
                .get(USER_ACCOUNT)
                .then()
                .spec(successfullUsersResponseSpec)
                .extract()
                .as(new TypeRef<List<UsersAccountResponseModel>>() {
                });
    }
}
