package logincouriertest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class LoginHelper {
    @Step("Create courier with login: {login}, password: {password}, firstName: {firstName}")
    public Response createCourier(String login, String password, String firstName) {
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\" }";
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Login courier with login: {login}, password: {password}")
    public Response loginCourier(String login, String password) {
    String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }";
    return RestAssured.given()
            .header("Content-Type", "application/json")
            .body(body)
            .when()
            .post("/api/v1/courier/login");
}

    @Step("Login courier with missing login")
    public Response loginCourierWithMissingLogin(String password) {
        String body = "{ \"password\": \"" + password + "\" }";
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Login courier with missing password")
    public Response loginCourierWithMissingPassword(String login) {
        String body = "{ \"login\": \"" + login + "\" }";
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier/login");
    }
}
