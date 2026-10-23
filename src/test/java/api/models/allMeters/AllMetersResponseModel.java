package api.models.allMeters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AllMetersResponseModel(
        Long idDevice,
        String serialNumber,
        String number,
        String deviceTypeTitle,
        String modification,
        List<Meter>meters
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Meter(
            Long idReceivingMethod,
            Long idTariff,
            Long idIndication,
            Integer idReadingStatus,
            Long reading,
            String titleReceiving,
            String titleReadingStatus,
            String titleUnit,
            String tariffTitle,
            String readingDate
    ) {
    }
}
