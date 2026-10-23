package api.models.userProfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Модель ответа API профиля пользователя
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserProfileResponseModel(
        Integer accessDenialCounter,
        Integer accessDeniedTimes,
        String birthdate,
        String dateBlocked,
        String dateCreated,
        Boolean deleted,
        String email,
        Boolean emailConfirmed,
        String fatherName,
        String firstName,
        Integer gender,
        Long id,
        Long idOld,
        Long idPerson,
        Integer idUserType,
        String inn,
        Boolean isActive,
        Boolean isBlocked,
        String lastName,
        String phone,
        Boolean sendInfoEmail,
        Boolean sendInfoSms,
        String snils,
        String timeLastAuth,
        Boolean twoFactorEnabled
) {}