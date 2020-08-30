package com.rabo.assignment.employee;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:com/rabo/assignment/employee/employee.feature",
        plugin = {
                "com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:target/test-reports/employee-report.html"
        })
public class EmployeeApplicationTests {

}


