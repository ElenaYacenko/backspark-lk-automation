package api.models.userAccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersAccountResponseModel(
        Long idUser,
        List<Account> accounts
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Account(
            Long idUnifiedAccount,
            String unifiedNumber,
            Integer accountStatus,
            Boolean isUnified,
            Boolean activeAutopayment,
            List<Core> core,
            @JsonProperty("ownerStatus")
            OwnerStatus ownerStatus
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Core(
            @JsonProperty("id_PA")
            Integer idPA,
            String number,
            Integer idStatus,
            String dateClose,
            Integer idOrg,
            String titleOrg,
            String logoOrg,
            String address
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OwnerStatus(
            Boolean verificationStatus,
            Integer writePermission
    ) {}
}