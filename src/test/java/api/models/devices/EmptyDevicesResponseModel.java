package api.models.devices;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EmptyDevicesResponseModel(
        String number,
        java.util.List<?> coreDevices  // null вместо List<CoreDevice>
) {
    public boolean hasNoDevices() {
        return coreDevices == null || coreDevices.isEmpty();
    }
}