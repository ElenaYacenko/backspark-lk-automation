package api.models.devices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DevicesResponseModel(
        String number,
        List<CoreDevice> coreDevices
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CoreDevice(
            Long idMeter,
            String deviceTitle,
            Integer startDay,
            Integer endDay,
            String modification,
            String serialNumber,
            String dateCheck,
            String dateCheckNext,
            String placement,
            String typeLocality,
            String locality,
            String typeStreet,
            String street,
            String typeHouse,
            String house,
            String building,
            String condosTypes,
            String condosNumber,
            List<Meter> meters
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Meter(
            Long idIndication,
            Long idTariff,
            String title,
            BigDecimal cost,
            String titleUnit,
            Long reading,
            String dateReading,
            String currentMonthReadingValue,
            String currentMonthReadingDate
    ) {
    }
}
