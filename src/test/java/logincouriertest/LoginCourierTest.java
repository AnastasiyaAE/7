package logincouriertest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import couriertest.ApiConstants;
import couriertest.CourierHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Epic("Courier Management")
@Feature("Courier Login")
public class LoginCourierTest {

    private Gson gson; // Создаем экземпляр Gson

    private int courierId = -1; // Переменная для хранения ID курьера
    private couriertest.CourierHelper courierHelper = new CourierHelper(); // Экземпляр вспомогательного класса
    private LoginHelper loginHelper = new LoginHelper(); // Экземпляр LoginHelper
    private String login;
    private String password;

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста, если ID был получен
        if (courierId != -1) {
            courierHelper.deleteCourier(courierId);  // Удаление курьера
        }
    }

    // С вынесенным URI в отдельный класс
    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConstants.BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create();
        login = "ivanov";
        password = "1234";
    }

    // Курьер может авторизоваться
    // Для авторизации нужно передать все обязательные поля
    // Успешный запрос возвращает id
    @Test
    @DisplayName("Courier can be created and login")
    @Step("Create courier and verify login")
    public void testCourierCanBeCreatedAndLogin() {

        Response createResponse = loginHelper.createCourier(login, password, "ivan");
        assertThat(createResponse.getStatusCode(), is(201));

        System.out.println("Курьер успешно создан. Код ответа: " + createResponse.getStatusCode());
        // Данные для авторизации
        Response loginResponse = loginHelper.loginCourier(login, password);
        assertThat(loginResponse.getStatusCode(), is(200));
        // Проверяем, что ответ содержит id
        assertThat(loginResponse.jsonPath().get("id"), is(notNullValue()));
        // Выводим код ответа и тело ответа в формате JSON на экран
        System.out.println("Код ответа на авторизацию: " + loginResponse.getStatusCode());
        System.out.println("Тело ответа на авторизацию: " + loginResponse.asString());
        // Авторизуемся, чтобы получить ID курьера
        courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера
        // Проверяем ID курьера получен корректно
        assertThat(courierId, is(not(-1))); // Что ID не -1
    }

    // Тест что система вернёт ошибку, если неправильно указать логин
    @Test
    @DisplayName("Login with wrong login should fail")
    @Step("Test courier login with wrong login")
    public void testWithWrongLoginCourier() {
        loginHelper.createCourier(login, password, "ivan");

        Response loginResponse = loginHelper.loginCourier("wrongUser", password);

        // Проверяем код ответа и сообщение
        assertThat(loginResponse.getStatusCode(), is(404));
        assertThat(loginResponse.jsonPath().getString("message"), is("Учетная запись не найдена"));
        // Вывод на экран для неверных учетных данных
        System.out.println("Wrong login and password:");
        System.out.println("Response Code: " + loginResponse.getStatusCode());
        System.out.println("Response Body: " + loginResponse.asString());

        // Тест: Тест что система вернёт ошибку, если неправильно указать пароль
        @Test
        @DisplayName("Login with wrong password should fail")
        @Step("Test courier login with wrong password")
        public void testWithWrongPasswordCourier () {
            loginHelper.createCourier(login, password, "ivan");
            Response loginResponse = loginHelper.loginCourier(login, "wrongPassword");

            // Проверяем код ответа и сообщение
            assertThat(loginResponse.getStatusCode(), is(404));
            assertThat(loginResponse.jsonPath().getString("message"), is("Учетная запись не найдена"));
            // Вывод на экран для неверного пароля
            System.out.println("Wrong password:");
            System.out.println("Response Code: " + loginResponse.getStatusCode());
            System.out.println("Response Body: " + loginResponse.asString());
            // Авторизуемся, чтобы получить ID курьера
            courierId = courierHelper.getCourierId(login, password); // Сохраняем ID курьера
            // Проверяем что ID курьера получен корректно
            assertThat(courierId, is(not(-1))); // Что ID не -1
        }

        // Тест если какого-то поля нет, запрос возвращает ошибку
        // *Тест не проходит: при отсутствии поля password = баг*
        @Test
        @DisplayName("Missing required fields returns error")
        @Step("Test missing login during courier login")
        public void testMissingLoginFieldsCourier () {
            loginHelper.createCourier(login, password, "ivan");
            Response responseWithoutLogin = loginHelper.loginCourierWithMissingLogin();
                        // Ожидаемое сообщение об ошибке
            String expectedMessage = "Недостаточно данных для входа";
            // Проверяем код ответа и сообщение
            assertThat(responseWithoutLogin.getStatusCode(), is(400));
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие логина. Код ответа: " + responseWithoutLogin.getStatusCode());
        }
        // Тест 2: Отсутствует поле "password"
        @Test
        @DisplayName("Missing password field returns error")
        @Step("Test missing password during courier login")
        public void testMissingPasswordFieldCourier () {
            loginHelper.createCourier(login, password, "ivan");
            Response responseWithoutPassword = loginHelper.loginCourierWithMissingPassword();
            // Ожидаемое сообщение об ошибке
            String expectedMessage = "Недостаточно данных для входа";
            // Проверяем код ответа и сообщение
            assertThat(responseWithoutPassword.getStatusCode(), is(400));
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedMessage));
            System.out.println("Тест на отсутствие пароля. Код ответа: " + responseWithoutPassword.getStatusCode());
        }
    }

}
