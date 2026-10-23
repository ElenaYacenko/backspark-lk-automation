package api.models.login;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SuccessfulLoginResponseModel(
        String name,
        String value,
        String maxAge,
        String domain,
        String path,
        boolean secure,
        boolean httpOnly,
        String sameSite
) {}
