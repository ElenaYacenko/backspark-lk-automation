package api.specs.devices;

import config.TestConfig;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class DevicesSpec {
    public static ResponseSpecification successfullDevicesResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            //.expectBody(matchesJsonSchemaInClasspath(TestConfig.DEVICES_SUCCESS_SCHEMA))
            .expectBody("number", notNullValue())
            .expectBody("core_devices", notNullValue())
            .build();

    public static ResponseSpecification successfullEmptyDevicesResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(TestConfig.DEVICES_EMPTY_SUCCESS_SCHEMA))
            .build();

    public static ResponseSpecification validation404FailedSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .build();

    public static ResponseSpecification unauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody("message", notNullValue())
            .build();

    public static ResponseSpecification missingParameterResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .build();

}
