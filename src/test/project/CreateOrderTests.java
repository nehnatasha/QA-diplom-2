package test.project;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import project.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTests {

    private UserClient userClient;

    User user;
    Order order;
    String accessToken;

    @Before
    public void init() {

        UserCredentials userCredentials = new UserCredentials();
        userClient = new UserClient();
        user = User.getCorrectUser();
        order = new Order();
        userClient.userRegistration(user);
        accessToken = UserCredentials.getUserAccessToken(user);
    }

    @After
    public void delete() {

        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {

        Response response = Order.createOrderWithAuth(Fillings.getRandomBurger(), accessToken);
        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {

        Response response = Order.createOrderWithOutAuth(Fillings.getRandomBurger());
        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингридиентов")
    public void createOrderWithoutAuthAndWithoutIngredientsTest() {

        Response response = Order.createOrderWithOutAuth(Fillings.getEmptyBurger());
        response.then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с некорректным ингридиентом")
    public void createOrderWithIncorrectIngredientsTest() {

        Response response = Order.createOrderWithOutAuth(Fillings.getIncorrectBurger());
        response.then()
                .assertThat()
                .statusCode(500);
    }
}
