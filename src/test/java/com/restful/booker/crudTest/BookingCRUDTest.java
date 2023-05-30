package com.restful.booker.crudTest;

import com.restful.booker.model.AuthPojo;
import com.restful.booker.model.BookingPojo;
import com.restful.booker.testbase.TestBaseBooking;
import com.restful.booker.utils.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class BookingCRUDTest extends TestBaseBooking {


    static int newId;
    static String token;


    @Test
    public void test001() {

            RequestSpecification request = RestAssured.given();
            request.header("Content-Type", "application/json");
            AuthPojo authPojo = new AuthPojo();
            authPojo.setUsername("admin");
            authPojo.setPassword("password123");
            Response response = request.body(authPojo)
                    .post("https://restful-booker.herokuapp.com/auth");

            String jsonString = response.asString();
            token = JsonPath.from(jsonString).get("token");
            System.out.println("Token is: " + token);
        }

    @Test
    public void test002(){
        given().log().all()
                .when()
                .get()
                .then()
                .statusCode(200)
                .log().all();

    }

    @Test
    public void test003() {
        HashMap<String, Object> booking = new HashMap<String, Object>();
        booking.put("checkin", "2022-01-01");
        booking.put("checkout", "2022-02-01");

        BookingPojo bookingPojo = new BookingPojo();

        bookingPojo.setFirstname("Zara" + TestUtils.getRandomValue());
        bookingPojo.setLastname("Lee" + TestUtils.getRandomValue());
        bookingPojo.setTotalprice(200);
        bookingPojo.setDepositpaid(false);
        bookingPojo.setBookingdates(booking);
        bookingPojo.setAdditionalneeds("Vegeterian");

        Response responseBody = given().log().all()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(bookingPojo)
                .when()
                .post().then().extract().response();

        responseBody.then().statusCode(200).log().all();

        System.out.println("response printing =" + responseBody.asString());
        newId = responseBody.jsonPath().get("bookingid");
        System.out.println("myID ------> " + newId);

        // Get status code
        int statusCode = responseBody.getStatusCode();
        System.out.println("Status code: " + statusCode);
        Assert.assertEquals(statusCode, 200);
    }

    @Test
    public void test004() {
        given().log().all()
                .header("Accept", "application/json")
                .pathParam("bookingId", newId)
                .when()
                .get("/{bookingId}")
                .then()
                .statusCode(200)
                .log().all();

    }

    @Test
    public void test005() {
        HashMap<String, Object> booking = new HashMap<String, Object>();
        booking.put("checkin", "2022-01-01");
        booking.put("checkout", "2022-02-01");

        BookingPojo bookingPojo = new BookingPojo();
        bookingPojo.setFirstname("Name after");
        bookingPojo.setLastname("put request");
        bookingPojo.setTotalprice(300);
        bookingPojo.setDepositpaid(false);
        bookingPojo.setBookingdates(booking);
        bookingPojo.setAdditionalneeds("Vegetarian Meals");

        RequestSpecification request = RestAssured.given().log().all();
        request.header("Content-Type", "application/json")
                .header("cookie", "token=" + token)
                .log().all();
        //.header("authorization", "bearer" + token)
        Response response = request.body(bookingPojo)
                .put("/" + newId);
        response.then().statusCode(200).log().all();
        System.out.println("put----> " + response.asString());
        // Get status code
        int statusCode = response.getStatusCode();
        System.out.println("Status code: " + statusCode);
        Assert.assertEquals(statusCode, 200);
    }




    @Test
    public void test006() {
        RequestSpecification request = RestAssured.given().log().all();
        request.header("Content-Type", "application/json")
                .header("cookie", "token=" + token)
                .log().all();
        Response response = request.body("{ \"firstname\": \"Patch firstname\" }")
                .patch("/" + newId);
        response.then().statusCode(200).log().all();
        System.out.println("patch----> " + response.asString());

        // Get status line
        String statusLine = response.getStatusLine();
        System.out.println("Status line: " + statusLine);
        Assert.assertEquals(statusLine, "HTTP/1.1 200 OK");

        // Get status code
        int statusCode = response.getStatusCode();
        System.out.println("Status code: " + statusCode);
        Assert.assertEquals(statusCode, 200);

    }

    @Test
    public void test007() {
        Response response = given().log().all()
                .contentType(ContentType.JSON)
                .header("cookie", "token=" + token)
                .when()
                .delete("/" + newId);
                response.then()
                .statusCode(201)
                .log().all();
        // Get status code
        int statusCode = response.getStatusCode();
        System.out.println("Status code: " + statusCode);
        Assert.assertEquals(statusCode, 200);
    }
}