package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/employees")
public interface IEmployeeController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

    @GetMapping(value = "/search/{searchString}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Employee> getEmployeeById(@PathVariable Long id);

    @GetMapping(value = "/highestSalary", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<BigDecimal> getHighestSalaryOfEmployees();

    @GetMapping(value = "/topTenHighestEarningEmployeeNames", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Employee>> getTopTenHighestEarningEmployees();

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Employee> createEmployee(@RequestBody Employee employee);

    @DeleteMapping("/{id}")
    ResponseEntity<Long> deleteEmployeeById(@PathVariable Long id);

}
