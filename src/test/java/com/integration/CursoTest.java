package com.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m3s1.dto.CursoDTO;
import com.m3s1.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CursoTest {

    private ObjectMapper mapper = new ObjectMapper();

    private static String tokenJWT = null;
    private static String codigoCurso = null;

    @BeforeAll
    public static void preCondicao() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/m3s1-1.0-SNAPSHOT/api";
    }

    @Test
    @Order(1)
    public void autenticacao() throws JsonProcessingException {
        LoginRequest request = new LoginRequest("james@kirk.com", "1234");
        tokenJWT = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/login")
                .then()
                .statusCode(201)
                .extract()
                .asString();
    }

    @Test
    @Order(2)
    public void inserirCurso() throws JsonProcessingException {
        CursoDTO request = new CursoDTO("Web - Java", 45);
        String json = mapper.writeValueAsString(request);
        codigoCurso = given()
                .header("Authorization", "Bearer " + tokenJWT)
                .contentType(ContentType.JSON)
                .body(json)
                .post("/cursos")
                .then()
                .statusCode(201)
                .body("codigo", notNullValue())
                .body("assunto", is(request.getAssunto()))
                .body("duracao", is(request.getDuracao())).extract().path("codigo");
    }
}
