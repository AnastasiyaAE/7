package couriertest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {
    @BeforeClass
    public static void setupBaseURI() {
    RestAssured.baseURI = ApiConstants.BASE_URI;
}
}
