package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {

    List<Employee> getEmployees();

    List<Employee> getEmployeesByNameSearch(String name);

    Optional<Employee> getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Long deleteEmployee(Long id);

}
