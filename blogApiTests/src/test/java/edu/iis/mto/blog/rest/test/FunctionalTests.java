package edu.iis.mto.blog.rest.test;

import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;
import org.junit.Test;

public class FunctionalTests {

    @BeforeClass
    public static void setup() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.valueOf(8080);
        } else {
            RestAssured.port = Integer.valueOf(port);
        }

        String basePath = System.getProperty("server.base");
        if (basePath == null) {
            basePath = "";
        }
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if (baseHost == null) {
            baseHost = "http://localhost";
        }
        RestAssured.baseURI = baseHost;

    }

    @Test
    public void confirmedUserShouldBeAbleToAddPost() throws Exception {

        JSONObject jsonObject = new JSONObject().put("entry","Test entry");
        RestAssured.given().accept(ContentType.JSON).header("Content-Type","application/json;charset=UTF-8")
                .body(jsonObject.toString()).expect().log().all().statusCode(HttpStatus.SC_CREATED).when()
                .post("/blog/user/1/post");

    }

    @Test
    public void newUserShouldNotBeAbleToAddPost() throws Exception {

        JSONObject jsonObj = new JSONObject().put("firstName","Tom").put("lastName","Lister").put("email", "tom@domain.com");
        int userId = RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString()).expect().log().all().statusCode(HttpStatus.SC_CREATED).when()
                .post("/blog/user").path("id");

        JSONObject jsonObject = new JSONObject().put("entry","Test entry");
        RestAssured.given().accept(ContentType.JSON).header("Content-Type","application/json;charset=UTF-8")
                .body(jsonObject.toString()).expect().log().all().statusCode(HttpStatus.SC_BAD_REQUEST).when()
                .post("/blog/user/"+userId+"/post");
    }

    


}
