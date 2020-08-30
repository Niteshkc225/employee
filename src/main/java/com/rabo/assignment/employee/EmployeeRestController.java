package com.rabo.assignment.employee;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class EmployeeRestController {

    public static final String APPLICATION_NAME = "employee";

    @GetMapping(path = APPLICATION_NAME)
    public String index(){
        return "Hello, all API tests were successful";
    }

}
