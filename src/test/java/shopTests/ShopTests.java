package shopTests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class ShopTests {
    private String body;

    @BeforeAll
    static void prepare() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
         Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }

    @Test
    void checkWishListAPI() {
        body = "addtocart_53.EnteredQuantity=1";
        step("Добавляем продукт", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body(body)
                    .log()
                    .all()
                    .when()
                    .post("addproducttocart/details/53/2")
                    .then()
                    .log()
                    .all()
                    .statusCode(200)
                    .body("updatetopwishlistsectionhtml", is("(1)"))
                    .body("message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"));
        });
    }

    @Test
    void checkUsersAddress() {
        String loginPassword = "poseidon@sea.com";

        step("Забираем куки и вставляем в браузер", () -> {
            String authorizationCookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", loginPassword)
                    .formParam("Password", loginPassword)
                    .log()
                    .all()
                    .when()
                    .post("login")
                    .then()
                    .log()
                    .all()
                    .statusCode(302)
                    .extract()
                    .cookie("NOPCOMMERCE.AUTH");

            step("Открываем браузер с самой легковесной страницей сайта", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));

            step("Вставляем куки в браузер", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });
        step("Проверяем данные пользователя", () -> {
            open("/customer/info");
            $("#FirstName").shouldHave(value(loginPassword));
            $("#LastName").shouldHave(value(loginPassword));
            $("#Email").shouldHave(value(loginPassword));
        });
    }
}
