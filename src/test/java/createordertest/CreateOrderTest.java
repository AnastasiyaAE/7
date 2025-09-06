package createordertest;


import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;


@RunWith(Parameterized.class)
public class CreateOrderTest {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private String deliveryDate;
    private String comment;
    private String[] color;
    private int rentTime;

    String orderId;

    // Ручка удаления заказа и получение id заказа = баг
    /*@After
    public void tearDown() {
        // Удаление заказа по ID
        OrderClient.deleteOrder(orderId);
    }*/

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation,
                           String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { "Petrov", "Petr", "Moscow, Lesnaya", "55", "+7 123 333 33 33", 3, "2026-08-08", "Позвонить", new String[] { "GRAY" } },
                { "Petrov", "Petr", "Moscow, Lesnaya", "55", "+7 123 333 33 33", 3, "2026-07-08", "Позвонить", new String[] { "GRAY", "BLACK" } },
                { "Petrov", "Petr", "Moscow, Lesnaya", "55", "+7 123 333 33 33", 3, "2026-08-08", "Позвонить", new String[] { } },
                { "Petrov", "Petr", "Moscow, Lesnaya", "55", "+7 123 333 33 33", 3, "2026-08-08", "Позвонить", new String[] { "BLACK" } },
        };
    }

    // Тест на создание заказа самоката разных цветов
    // Тест не проходит: не удается получить id заказа и удаление заказа по ручкам из документации
    // Тест также не проходит если удалять заказ по трек-номеру
    @Test
    @DisplayName("Creating an order with different colors")
    public void createOrderParameterizedColorScooterTest() {

        OrderCreate orderCreate = new OrderCreate(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);
        Response createResponse = OrderClient.createNewOrder(orderCreate);
        OrderClient.comparingSuccessfulOrderSet(createResponse, SC_CREATED);
        //orderId = OrderClient.getOrderId(createResponse);
        //Response deleteResponse = OrderClient.deleteOrder(orderId);
        //OrderClient.comparingSuccessfulOrderCancel(deleteResponse, SC_OK);
    }
}


