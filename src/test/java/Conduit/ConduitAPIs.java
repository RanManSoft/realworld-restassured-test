package Conduit;

import io.cucumber.java.en.Then;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class ConduitAPIs extends TestBase{
    private static void loginAPI(String userName, String requestBody) {
        // @formatter:off
        given()
                .body(requestBody)
                .contentType("application/json")
                .log().all().
        when()
                .post("/users/login").
        then()
                .log().all()
                .statusCode(200)
                .body("user.username", equalTo(userName))
                .body(matchesJsonSchemaInClasspath("templates/responses/signin_schema.json"));
        // @formatter:on
    }
    public ConduitAPIs() {
        RestAssured.baseURI = baseURL;
    }
    @Then("Sign up with UserName:{string} and Email:{string} and Password:{string} failed")
    public void sign_up_with_user_name_and_email_and_password_failed(String userName, String email, String password) {
        Map<String, String> testData = new HashMap<>();
        testData.put("UserName",userName);
        testData.put("Email",email);
        testData.put("Password",password);
        String requestBody = generateRequestBody(testData, "signup.json");

        // @formatter:off
        given()
                .body(requestBody)
                .contentType("application/json")
                .log().all().
        when()
                .post("/users").
        then()
                .log().all()
                .statusCode(201);
        // @formatter:on
    }

    @Then("Sign in with user {string} successful")
    public void sign_in_with_user_successful(String userName) {
        Map<String, String> testData = getUserwithName(userName);
        String requestBody = generateRequestBody(testData, "signin.json");

        loginAPI(userName, requestBody);
    }


}
