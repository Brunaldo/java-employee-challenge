package com.example.rqchallenge.service;

import com.example.rqchallenge.controller.ResponseBody;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.rest.ApiClientService;
import com.example.rqchallenge.service.rest.RestResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private static final String INPUT_CANNOT_BE_NULL = "Input cannot be null";

    @Value("${external.api.endpoint.employees}")
    private String allEmployeesEndpoint;

    @Value("${external.api.endpoint.employee}")
    private String employeeEndpoint;

    @Value("${external.api.endpoint.employee.create}")
    private String createEmployeeEndpoint;

    @Value("${external.api.endpoint.employee.delete}")
    private String deleteEmployeeEndpoint;

    private final ApiClientService clientService;

    @Override
    public List<Employee> getEmployees() {
        ResponseEntity<ResponseBody<List<Employee>>> response = clientService.exchange(
                allEmployeesEndpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        return RestResponseHandler.handleResponse(response);
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String name) {
        Objects.requireNonNull(name, INPUT_CANNOT_BE_NULL);

        return this.getEmployees().stream()
                .filter(employee -> employee.getEmployeeName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        Objects.requireNonNull(id, INPUT_CANNOT_BE_NULL);

        ResponseEntity<ResponseBody<Employee>> response = clientService.exchange(
                employeeEndpoint + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return Optional.of(RestResponseHandler.handleResponse(response));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        Objects.requireNonNull(employee, INPUT_CANNOT_BE_NULL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> requestEntity = new HttpEntity<>(employee, headers);

        ResponseEntity<ResponseBody<Employee>> response = clientService.exchange(
                createEmployeeEndpoint,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Long id = RestResponseHandler.handleResponse(response).getId();

        return employee.toBuilder()
                .id(id)
                .build();
    }

    @Override
    public Long deleteEmployee(Long id) {
        Objects.requireNonNull(id, INPUT_CANNOT_BE_NULL);

        ResponseEntity<ResponseBody<Long>> response = clientService.exchange(
                deleteEmployeeEndpoint + "/" + id,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return RestResponseHandler.handleResponse(response);
    }

}
