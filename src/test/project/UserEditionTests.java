package test.project;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import project.User;
import project.UserClient;
import project.UserCredentials;

import static org.hamcrest.Matchers.equalTo;

public class UserEditionTests {

    UserClient userClient;
    UserCredentials userCredentials;
    User user, newUser;
    String accessToken;

    @Before
    public void init() {

        userClient = new UserClient();
        userCredentials = new UserCredentials();
        user = User.getCorrectUser();
        newUser = User.getCorrectUser();
        userClient.userRegistrationAndLogin(user);
        accessToken = UserCredentials.getUserAccessToken(user);
    }

    @After
    public void delete() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Изменение данных курьера")
    public void editUserWithAuthTest() {

        Response response = userClient.userEdit(newUser, accessToken);
        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUser.email))
                .body("user.name", equalTo(newUser.name));
    }

    @Test
    @DisplayName("Изменение данных курьера без авторизации")
    public void editUserWithoutAuthTest() {

        Response response = userClient.userEditWithOutAuth(newUser);
        response.then()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
