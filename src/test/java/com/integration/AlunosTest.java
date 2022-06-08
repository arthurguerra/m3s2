package com.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m3s1.dto.AlunoDTO;
import com.m3s1.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlunosTest {

    private ObjectMapper mapper = new ObjectMapper();

    private static String tokenJWT = null;
    private static Integer idAluno = null;

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
    public void criarAluno() throws JsonProcessingException {
        AlunoDTO request = new AlunoDTO("Nome do Aluno");
        String json = mapper.writeValueAsString(request);
        idAluno = given()
                .header("Authorization", "Bearer " + tokenJWT)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/alunos")
                .then()
                .statusCode(201)
                .body("matricula", notNullValue())
                .extract()
                .path("matricula");
    }

    @Test
    @Order(3)
    public void alterarAluno() throws JsonProcessingException {
        AlunoDTO request = new AlunoDTO("Aprendiz");
        String json = mapper.writeValueAsString(request);
        idAluno = given()
                .header("Authorization", "Bearer " + tokenJWT)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put("/alunos/{id}", idAluno)
                .then()
                .statusCode(200)
                .body("matricula", notNullValue())
                .extract()
                .path("matricula");
    }

    @Test
    @Order(4)
    public void listarAlunos() {
        given()
                .when()
                .get("/alunos")
                .then()
                .statusCode(200)
        ;
    }

    @Test
    @Order(5)
    public void listarAlunoPorMatricula() {
        given()
                .when()
                .get("alunos/{id}", idAluno)
                .then()
                .statusCode(200)
                .body("nome", is("Aprendiz"));
    }

    @Test
    @Order(6)
    public void apagarAluno() {
        given()
                .header("Authorization", "Bearer " + tokenJWT)
                .when()
                .delete("/alunos/{id}", idAluno)
                .then()
                .statusCode(204);
    }
}
