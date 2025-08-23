package couriertest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Courier Management")
@Feature("Courier Creation")
public class CourierTest {

    private Gson gson;

    private int courierId = -1;
    private CourierHelper courierHelper = new CourierHelper();

    @After
    public void tearDown() {

        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);
        }
    }


    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConstants.BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // Создание курьера
    @Test
    @Story("Create a new courier")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a new courier is possible and returns the correct response")
    public void testCreateCourierIsPossible() {
        String login = "ivanov";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"ivan\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierHelper.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
    }

    //Нельзя создать двух одинаковых курьеров
    @Test
    @Story("Prevent duplicate courier creation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a courier with the same login returns an error")
    public void testErrorCreateTheSameCourier() {
        String login = "ivanov"; // Логин
        String password = "1234"; // Пароль
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"ivan\" }";
        Response firstResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        assertThat(firstResponse.getStatusCode(), is(201));
        System.out.println("Курьер успешно создан. Код ответа: " + firstResponse.getStatusCode());
        Response secondResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(secondResponse, gson);
        assertThat(secondResponse.getStatusCode(), is(409));
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondResponse.jsonPath().getString("message"), is(expectedMessage));
        courierId = courierHelper.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
    }

    // Чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    @Story("Validate required fields for courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that all required fields are present when creating a courier")
    public void testCreateCourierWithAllRequiredFields() {

        String login = "ivanov";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"ivan\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierHelper.getCourierId(login, password);

        assertThat(courierId, is(not(-1)));
    }
    // Запрос возвращает правильный код ответа
    @Test
    @Story("Validate status code 201 for successful courier creation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that creating a new courier returns status code 201")
    public void testCreateCourierCode201() {
        String login = "ivanov";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"ivan\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        System.out.println("Код ответа: " + response.getStatusCode());
        assertThat(response.getStatusCode(), is(201));
        courierId = courierHelper.getCourierId(login, password);
        assertThat(courierId, is(not(-1)));
    }

    // Успешный запрос  возвращает ok: true
    @Test
    @Story("Validate 'ok: true' for successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a successful courier creation returns 'ok: true' in the response")
    public void testCreateCourierOkTrue() {
        String login = "ivanov";
        String password = "1234";
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"ivan\" }";
        // Отправляем POST запрос
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierHelper.printResponse(response, gson);
        assertThat(response.jsonPath().get("ok"), is(true));
        courierId = courierHelper.getCourierId(login, password);

        assertThat(courierId, is(not(-1)));
    }

    // Одного из полей нет, запрос возвращает ошибку

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a login returns an error")
    public void testCreateCourierWithoutLogin() {
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"ivan\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .when()
                .post("/api/v1/courier");

        courierHelper.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле login");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Если одного из полей нет, запрос возвращает ошибку

    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a password returns an error")
    public void testCreateCourierWithoutPassword() {
        String login = "ivanov" + System.currentTimeMillis();
        String bodyWithoutPassword = "{ \"login\": \"" + login + "\", \"firstName\": \"saske\" }";

        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutPassword)
                .when()
                .post("/api/v1/courier");

        courierHelper.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле password");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Пропущено поле firstName
    // <Баг
    @Test
    @Story("Validate error for missing required fields in courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a courier without a first name returns an error")
    public void testCreateCourierWithoutFirstName() {
        String login = "ivanov" + System.currentTimeMillis();
        String bodyWithoutFirstName = "{ \"login\": \"" + login + "\", \"password\": \"1234\" }";
        // Сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutFirstName)
                .when()
                .post("/api/v1/courier");

        courierHelper.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле firstName");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}
