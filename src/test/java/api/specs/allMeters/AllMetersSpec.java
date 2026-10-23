package api.specs.allMeters;

import config.TestConfig;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AllMetersSpec {
    public static ResponseSpecification successfulAllMetersResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(TestConfig.METERS_SUCCESS_SCHEMA))
            .build();

    public static ResponseSpecification missingParametersResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(TestConfig.METERS_MISSING_SUCCESS_SCHEMA))
            .expectBody("success", equalTo(false))
            .expectBody("error", notNullValue())
            .build();

    public static ResponseSpecification error404ResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .build();
}
