package api.apiClients;

public class ApiClient {
    public final AuthApiClient auth = new AuthApiClient();
    public final UserApiClient user = new UserApiClient();
    public final DevicesApiClient devices = new DevicesApiClient();
    public final AllMetersApiClient allMeters = new AllMetersApiClient();
    public final MeterApiClient meter = new MeterApiClient();
    public final UsersAccountApiClient usersAccount = new UsersAccountApiClient();



}
