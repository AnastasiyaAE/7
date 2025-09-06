package logincouriertest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class LoginHelper {
    @Step("Create courier with login: {login}, password: {password}, firstName: {firstName}")
    public Response createCourier(String login, String password, String firstName) {
        CourierCreate courier = new CourierCreate(login, password, firstName);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Login courier with login: {login}, password: {password}")
    public Response loginCourier(String login, String password) {
        Authorization authorization = new Authorization(login, password);
    return RestAssured.given()
            .header("Content-Type", "application/json")
            .body(authorization)
            .when()
            .post("/api/v1/courier/login");
}

    @Step("Login courier with missing login")
    public Response loginCourierWithMissingLogin(String password) {
        Authorization authorization = new Authorization(null, password);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(authorization)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Login courier with missing password")
    public Response loginCourierWithMissingPassword(String login) {
        Authorization authorization = new Authorization(login, null);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(authorization)
                .when()
                .post("/api/v1/courier/login");
    }
}
