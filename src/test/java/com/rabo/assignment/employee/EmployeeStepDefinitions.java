package com.rabo.assignment.employee;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rabo.assignment.employee.model.Employee;
import com.rabo.assignment.employee.model.EmployeeDTO;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static io.restassured.RestAssured.given;

public class EmployeeStepDefinitions {

    WireMockServer wireMockServer = new WireMockServer();
    private Response response;
    private static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    private static final String LOGIN_ERROR_MESSAGE = "Invalid employeename/password supplied";
    private static final String LOGOUT_MESSAGE = "Logout successful";
    private EmployeeDTO emp;
    private String expiryDate;

    @Before
    public void setup(){

        Employee employee = new Employee(1, "virat", "Virat", "Kohli", "virat@rabo.nl", "password", "+31 6445552200", 1);
        emp = new EmployeeDTO(employee);

        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
        expiryDate = String.valueOf(LocalDateTime.now().plusDays(1));
    }

    @After
    public void teardown(){
        wireMockServer.stop();
    }

    @When("^employee login with (.+) and (.+)$")
    public void employeeLoginWithAnd(String employeeName, String password) {

        setUpStub();
        //extract the response
        response = given()
                .queryParam("employeename", employeeName)
                .queryParam("password", password)
                .when()
                .get("/employee/login");
    }

    @Then("^the server should return a success status and success message$")
    public void theServerShouldReturnASuccessStatusAndSuccessMessage() {
        response.then().statusCode(HttpStatus.SC_OK);
        Assert.assertEquals(response.getBody().asString(), LOGIN_SUCCESS_MESSAGE);

        //Assert headers
        Assert.assertEquals(response.header("authorization"), "Bearer eyJhbGci");
        Assert.assertEquals(response.header("X-Rate-Limit"), "10");
        Assert.assertEquals(response.header("X-Expires-After"), expiryDate);
    }

    @Then("^the server should return a error message$")
    public void theServerShouldReturnAErrorMessage() {
        response.then().statusCode(HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(response.getBody().asString(), LOGIN_ERROR_MESSAGE);
    }

    @When("^employee logout$")
    public void employeeLogout() {
        setUpStub();
        response = given()
                .when()
                .get("/employee/logout");
    }

    @When("^get employee with name (.+)$")
    public void getEmployeeWithName(String employeeName) throws JSONException {
        setUpStubForGetEmployee();
        response = given()
                .when()
                .get("/employee/" + employeeName );

    }

    @Then("^the server should return a success message$")
    public void theServerShouldReturnASuccessMessage() {
        response.then().statusCode(HttpStatus.SC_OK);
        Assert.assertEquals(response.getBody().asString(), LOGOUT_MESSAGE);
    }

    @Then("^all the employee details should be retrieved$")
    public void allTheEmployeeDetailsShouldBeRetrieved() {
        response.then().statusCode(HttpStatus.SC_OK);
        Assert.assertEquals(response.jsonPath().getJsonObject("employeename"), emp.getEmployeeName());
        Assert.assertEquals(response.jsonPath().getJsonObject("firstName"), emp.getFirstName());
        Assert.assertEquals(response.jsonPath().getJsonObject("lastName"), emp.getLastName());
        Assert.assertEquals(response.jsonPath().getJsonObject("email"), emp.getEmail());
    }

    @Then("^not found should be returned$")
    public void notFoundShouldBeReturned() {
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }


    //Stubs
    public void setUpStub(){
        stubFor(get(urlPathMatching("/employee/login"))
                .withQueryParam("employeename", matching((emp.getEmployeeName())))
                .withQueryParam("password", matching(emp.getPassword()))
                .willReturn(aResponse()
                        .withHeader("authorization", "Bearer eyJhbGci")
                        .withHeader("X-Rate-Limit", "10")
                        .withHeader("X-Expires-After", expiryDate)
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(LOGIN_SUCCESS_MESSAGE)));

        stubFor(get(urlPathMatching("/employee/login"))
                .withQueryParam("employeename", notMatching(emp.getEmployeeName()))
                .withQueryParam("password", notMatching(emp.getPassword()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withBody(LOGIN_ERROR_MESSAGE)));

        stubFor(get(urlPathMatching("/employee/logout"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(LOGOUT_MESSAGE)));
    }

    public void setUpStubForGetEmployee() throws JSONException {

        JSONObject json = new JSONObject();
        json.put("employeename", emp.getEmployeeName());
        json.put("firstName", emp.getFirstName());
        json.put("lastName", emp.getLastName());
        json.put("email", emp.getEmail());

        stubFor(get(urlPathMatching("/employee/" + emp.getEmployeeName()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(json.toString())));

//        .withRequestBody(matchingJsonPath("$.name"))
    }
}
