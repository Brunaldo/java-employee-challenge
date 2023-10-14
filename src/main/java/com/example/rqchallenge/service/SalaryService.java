package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService implements ISalaryService {

    private final EmployeeService employeeService;

    @Override
    public Optional<BigDecimal> getHighestSalaryOfEmployees() {
        return employeeService.getEmployees()
                .stream()
                .distinct()
                .max(Comparator.comparing(Employee::getEmployeeSalary))
                .map(Employee::getEmployeeSalary);
    }

    @Override
    public List<Employee> getTop10HighestEarningEmployees() {
        return employeeService.getEmployees()
                .stream()
                .distinct()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

}
