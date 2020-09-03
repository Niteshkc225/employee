package com.rabo.assignment.employee;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rabo.assignment.employee.model.Employee;
import com.rabo.assignment.employee.model.EmployeeDTO;
import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static io.restassured.RestAssured.given;

public class EmployeeStepDefinitions {

    WireMockServer wireMockServer = new WireMockServer();
    private Response response;
    private static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    private static final String LOGIN_ERROR_MESSAGE = "Invalid employeename/password supplied";
    private static final String LOGOUT_MESSAGE = "Logout successful";
    private static final String ERROR_MESSAGE_EMP_NOT_FOUND = "employee not found";
    private static final String ERROR_MESSAGE_EMP_INVALID  = "Invalid employee supplied";
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

    @And("^error message employee not found should be returned$")
    public void errorMessageEmployeeNotFoundShouldBeReturned() throws Throwable {
        Assert.assertEquals(response.getBody().asString(), ERROR_MESSAGE_EMP_NOT_FOUND);
    }

    @When("^create employee with (.+),(.+),(.+),(.+),(.+),(.+) and (.+)$")
    public void createEmployeeWithAnd(String employeeName, String fullName, String lastName, String email, String password, String phone, String employeeStatus) throws Throwable {

        setUpStubForCreateEmployee();

        JSONObject json = new JSONObject();
        json.put("employeename", employeeName);
        json.put("firstName", fullName);
        json.put("lastName", lastName);
        json.put("email", email);
        json.put("password", password);
        json.put("phone", phone);
        json.put("phone", employeeStatus);

        response = given()
                .header("Content-Type", "application/json")
                .body(json.toString())
                .when()
                .post("/employee");
    }

    @Then("^a success message should be returned for create$")
    public void aSuccessMessageShouldBeReturnedForTheCreate() {
        response.then().statusCode(HttpStatus.SC_CREATED);
        Assert.assertEquals(response.getBody().asString(), "successful operation");
    }

    @Then("^error message should be thrown for invalid body$")
    public void errorMessageShouldBeThrownForInvalidBody()  {
        response.then().statusCode(HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(response.getBody().asString(), ERROR_MESSAGE_EMP_INVALID);
    }

    @When("^update employee name of (.+) with (.+),(.+),(.+),(.+),(.+),(.+) and (.+)$")
    public void updateEmployeeNameOfWithAnd(String employeeName, String newEmployeeName, String fullName, String lastName, String email, String password, String phone, String employeeStatus) throws JSONException {

        setUpStubForUpdateEmployee();

        JSONObject json = new JSONObject();
        json.put("employeename", newEmployeeName);
        json.put("firstName", fullName);
        json.put("lastName", lastName);
        json.put("email", email);
        json.put("password", password);
        json.put("phone", phone);
        json.put("phone", employeeStatus);

        response = given()
                .header("Content-Type", "application/json")
                .body(json.toString())
                .when()
                .put("/employee/" + employeeName);
    }

    @Then("^a success message should be returned for update$")
    public void aSuccessMessageShouldBeReturnedForUpdate() throws Throwable {
        response.then().statusCode(HttpStatus.SC_OK);
        Assert.assertEquals(response.getBody().asString(), "successful operation");
    }

    @When("^update delete employee with name (.+)$")
    public void updateDeleteEmployeeWithName(String employeeName) throws Throwable {

        setUpStubForDeleteEmployee();

        response = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/employee/" + employeeName);
    }

    @Then("^the server should return a success status$")
    public void theServerShouldReturnASuccessStatus() throws Throwable {
        response.then().statusCode(HttpStatus.SC_OK);
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
    }

    public void setUpStubForCreateEmployee() throws JSONException {

        stubFor(post(urlMatching("/employee")).atPriority(1)
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(matchingJsonPath("$.employeename", matching("[a-zA-Z]*")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_CREATED)
                        .withBody("successful operation")));

        stubFor(post(urlMatching("/employee")).atPriority(2)
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(matchingJsonPath("$.employeename", matching("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withBody(ERROR_MESSAGE_EMP_INVALID)));
    }

    public void setUpStubForUpdateEmployee() throws JSONException {

        stubFor(put(urlEqualTo("/employee/" + emp.getEmployeeName())).atPriority(1)
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(matchingJsonPath("$.employeename", matching("[a-zA-Z]*")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("successful operation")));

        stubFor(put(urlEqualTo("/employee/virat123")).atPriority(3)
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(matchingJsonPath("$.employeename", matching("[a-zA-Z]*")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_NOT_FOUND)
                        .withBody(ERROR_MESSAGE_EMP_NOT_FOUND)));

        stubFor(put(urlEqualTo("/employee/" + emp.getEmployeeName())).atPriority(2)
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(matchingJsonPath("$.employeename", matching("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withBody(ERROR_MESSAGE_EMP_INVALID)));
    }

    public void setUpStubForDeleteEmployee() throws JSONException {

        stubFor(delete(urlEqualTo("/employee/" + emp.getEmployeeName())).atPriority(1)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("successful operation")));

        stubFor(delete(urlEqualTo("/employee/virat123")).atPriority(2)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.SC_NOT_FOUND)
                        .withBody(ERROR_MESSAGE_EMP_NOT_FOUND)));
    }
}
