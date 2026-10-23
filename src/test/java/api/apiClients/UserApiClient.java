package api.apiClients;

import io.qameta.allure.Step;
import api.models.userProfile.UserProfileResponseModel;
import utils.JwtUtils;

import static config.Endpoints.USER;
import static io.restassured.RestAssured.given;
import static api.specs.base.BaseSpec.getUnauthRequestSpec;
import static api.specs.users.UsersSpec.successfullUsersResponseSpec;

public class UserApiClient {

    @Step("[API] Получение информации о профиле пользователя")

    public UserProfileResponseModel userGetInfo(String token) {
        String userId = JwtUtils.extractSub(token);

        return given(getUnauthRequestSpec())
                .header("Authorization", "Bearer " + token)
                .pathParam("id", userId)
                .when()
                .get(USER)
                .then()
                .spec(successfullUsersResponseSpec)
                .extract()
                .as(UserProfileResponseModel.class);
    }
}
