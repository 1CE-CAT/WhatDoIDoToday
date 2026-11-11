package ru.maks.NauJava;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskRestControllerApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void searchByUser_Authorized_ReturnsOk() {
        given()
            .auth().basic("admin", "admin123")
            .contentType(ContentType.JSON)
        .when()
            .get("/api/tasks/search-by-user?userName=admin")
        .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }

    @Test
    void searchByUser_Unauthorized_Returns401() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/tasks/search-by-user?userName=admin")
        .then()
            .statusCode(401);
    }
}