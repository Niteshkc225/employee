Feature: Testing employee REST API
  Users should be able to submit GET POST, PUT and DELETE requests to a web service,
  represented by WireMock

  Scenario Outline: Successful employee login
    When employee login with <employeeName> and <password>
    Then the server should return a success status and success message

    Examples:
    |employeeName   |password|
    |virat          |password|

  Scenario Outline: Error message when unauthorised employee login
    When employee login with <userName> and <password>
    Then the server should return a error message

    Examples:
      |userName      |password|
      |virat@rebo.com|test@123|

  Scenario: Employee logout
    When employee logout
    Then the server should return a success message

  Scenario Outline: Get employee by employee name
    When get employee with name <employeeName>
    Then all the employee details should be retrieved

    Examples:
      |employeeName   |
      |virat          |

  Scenario Outline: Error message when get employee with invalid name
    When get employee with name <employeeName>
    Then not found should be returned

    Examples:
      |employeeName   |
      |virat123       |