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

  Scenario Outline: Verify create Employee
    When create employee with <employeeName>,<fullName>,<lastName>,<email>,<password>,<phone> and <employeeStatus>
    Then a success message should be returned for create

    Examples:
      |employeeName|fullName|lastName|email        |password|phone     |employeeStatus|
      |MarkGill    |Mark    |Gill    |mark@rebo.nl |password|062222222 |0             |

  Scenario Outline: Verify error message when create Employee with invalid name
    When create employee with <employeeName>,<fullName>,<lastName>,<email>,<password>,<phone> and <employeeStatus>
    Then error message should be thrown for invalid body

    Examples:
      |employeeName|fullName|lastName|email        |password|phone     |employeeStatus|
      |MarkGill?/  |Mark    |Gill    |mark@rebo.nl |password|062222222 |0             |

  Scenario Outline: Verify Update Employee
    When update employee name of <employeeName> with <newEmployeeName>,<fullName>,<lastName>,<email>,<password>,<phone> and <employeeStatus>
    Then a success message should be returned for update

    Examples:
      |employeeName|newEmployeeName|fullName|lastName|email        |password|phone     |employeeStatus|
      |virat       |MarkGill       |Mark    |Gill    |mark@rebo.nl |password|062222222 |0             |

  Scenario Outline: Verify error message when update Employee with invalid name
    When update employee name of <employeeName> with <newEmployeeName>,<fullName>,<lastName>,<email>,<password>,<phone> and <employeeStatus>
    Then error message should be thrown for invalid body

    Examples:
      |employeeName|newEmployeeName|fullName|lastName|email        |password|phone     |employeeStatus|
      |virat       |MarkGill?/     |Mark    |Gill    |mark@rebo.nl |password|062222222 |0             |

  Scenario Outline: Verify error message when update Employee which does not exist
    When update employee name of <employeeName> with <newEmployeeName>,<fullName>,<lastName>,<email>,<password>,<phone> and <employeeStatus>
    Then not found should be returned
    And error message employee not found should be returned

    Examples:
      |employeeName|newEmployeeName|fullName|lastName|email        |password|phone     |employeeStatus|
      |virat123     |MarkGill      |Mark    |Gill    |mark@rebo.nl |password|062222222 |0             |

  Scenario Outline: Verify Delete Employee
    When update delete employee with name <employeeName>
    Then the server should return a success status

    Examples:
    |employeeName|
    |virat       |

  Scenario Outline: Verify error message when delete an employee who does not exist
    When update delete employee with name <employeeName>
    Then not found should be returned
    And error message employee not found should be returned

    Examples:
      |employeeName|
      |virat123    |

