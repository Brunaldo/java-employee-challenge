package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ISalaryService {

    Optional<BigDecimal> getHighestSalaryOfEmployees();

    List<Employee> getTop10HighestEarningEmployees();

}
