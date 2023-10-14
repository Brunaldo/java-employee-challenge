package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;
import com.example.rqchallenge.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;
    private final SalaryService salaryService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.of(Optional.of(employeeService.getEmployees()));
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.of(Optional.of(employeeService.getEmployeesByNameSearch(searchString)));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(Long id) {
        return ResponseEntity.of(employeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<BigDecimal> getHighestSalaryOfEmployees() {
        return ResponseEntity.of(salaryService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getTopTenHighestEarningEmployees() {
        return ResponseEntity.of(Optional.of(salaryService.getTop10HighestEarningEmployees()));
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return new ResponseEntity<>(employeeService.createEmployee(employee), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Long> deleteEmployeeById(Long id) {
        return ResponseEntity.of(Optional.of(employeeService.deleteEmployee(id)));
    }

}
