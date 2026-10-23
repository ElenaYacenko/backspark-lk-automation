package api.models.allMeters;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * Универсальная модель для ответов API с ошибкой.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorResponseModel(
        Boolean success,
        String error
) {}