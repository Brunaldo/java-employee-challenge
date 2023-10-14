package com.example.rqchallenge.unit;

import com.example.rqchallenge.controller.ResponseBody;
import com.example.rqchallenge.extension.EmployeeExtension;
import com.example.rqchallenge.extension.EmployeeExtension.EmployeesExt;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.rest.ApiClientService;
import com.example.rqchallenge.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({ EmployeeExtension.class, MockitoExtension.class })
public class EmployeeServiceTest {

    private static final String INPUT_CANNOT_BE_NULL = "Input cannot be null";

    private static final String ROSS_GELLER = "Ross Geller";
    private static final String MONICA_BING = "Monica Bing";
    private static final String CHANDLER_BING = "Chandler Bing";
    private static final String PRINCESS_CONSUELA_BANANA_HAMMOCK = "Princess Consuela Banana Hammock";
    private static final String RACHEL_GREEN = "Rachel Green";
    private static final String EMPLOYEES_ENDPOINT = "/employees";
    private static final String EMPLOYEE_ENDPOINT = "/employee";
    private static final String CREATE = "/create";
    private static final String DELETE = "/delete";

    @Mock
    ApiClientService clientService;
    @InjectMocks
    EmployeeService service;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "allEmployeesEndpoint", EMPLOYEES_ENDPOINT);
        ReflectionTestUtils.setField(service, "employeeEndpoint", EMPLOYEE_ENDPOINT);
        ReflectionTestUtils.setField(service, "createEmployeeEndpoint", CREATE);
        ReflectionTestUtils.setField(service, "deleteEmployeeEndpoint", DELETE);
    }

    @Test
    void getAllEmployees(@EmployeesExt(numberOfEmployees = 100, withSalary = true) List<Employee> employees) {
        //given
        mockEmployeesResponse(employees, EMPLOYEES_ENDPOINT);

        //when
        List<Employee> actualEmployees = service.getEmployees();

        //then
        assertThat(actualEmployees).hasSize(100);
    }

    @Test
    void getEmployeesByNameSearch_multipleMatch(@EmployeesExt(numberOfEmployees = 5, withSalary = true) List<Employee> employees) {
        //given
        updateNames(employees, List.of(ROSS_GELLER, MONICA_BING, CHANDLER_BING, PRINCESS_CONSUELA_BANANA_HAMMOCK, RACHEL_GREEN));
        mockEmployeesResponse(employees, EMPLOYEES_ENDPOINT);

        //when
        List<Employee> actualEmployees = service.getEmployeesByNameSearch("Bing");

        //then
        assertThat(actualEmployees).hasSize(2)
                .extracting(Employee::getEmployeeName)
                .containsExactly(MONICA_BING, CHANDLER_BING);
    }

    @Test
    void getEmployeesByNameSearch_nullInput_throwsException() {
        assertThatThrownBy(() -> service.getEmployeesByNameSearch(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(INPUT_CANNOT_BE_NULL);
    }

    @Test
    void getEmployeesByNameSearch_singleMatch(@EmployeesExt(numberOfEmployees = 5, withSalary = true) List<Employee> employees) {
        //given
        updateNames(employees, List.of(ROSS_GELLER, MONICA_BING, CHANDLER_BING, PRINCESS_CONSUELA_BANANA_HAMMOCK, RACHEL_GREEN));
        mockEmployeesResponse(employees, EMPLOYEES_ENDPOINT);

        //when
        List<Employee> actualEmployees = service.getEmployeesByNameSearch("Princess");

        //then
        assertThat(actualEmployees).hasSize(1)
                .extracting(Employee::getEmployeeName)
                .containsExactly(PRINCESS_CONSUELA_BANANA_HAMMOCK);
    }

    @Test
    void getEmployeesByNameSearch_noMatch(@EmployeesExt(numberOfEmployees = 5, withSalary = true) List<Employee> employees) {
        //given
        mockEmployeesResponse(employees, EMPLOYEES_ENDPOINT);

        //when
        List<Employee> actualEmployees = service.getEmployeesByNameSearch("random-name");

        //then
        assertThat(actualEmployees).isEmpty();
    }

    @Test
    void getEmployeeById_existingEmployee(@EmployeesExt(numberOfEmployees = 5, withSalary = true) List<Employee> employees) {
        updateIds(employees, List.of(1L, 2L, 3L, 4L, 5L));

        Employee expectedEmployee = employees.stream()
                .filter(employee -> employee.getId() == 3L)
                .findFirst()
                .get();

        mockEmployeesResponse(expectedEmployee);

        Optional<Employee> actualEmployee = service.getEmployeeById(3L);

        assertThat(actualEmployee.get()).isEqualTo(actualEmployee.get());
    }

    @Test
    void getEmployeeById_nullInput_throwsException() {
        assertThatThrownBy(() -> service.getEmployeeById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(INPUT_CANNOT_BE_NULL);
    }


    @Test
    void createEmployee(@EmployeesExt List<Employee> employees) {
        Employee employee = employees.get(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> requestEntity = new HttpEntity<>(employee, headers);

        ResponseBody<Employee> mockResponse = new ResponseBody<>("", employee, "");
        ResponseEntity<ResponseBody<Employee>> expectedResponse = new ResponseEntity<>(mockResponse, HttpStatus.CREATED);

        Mockito.<ResponseEntity<ResponseBody<Employee>>>when(clientService.exchange(CREATE, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {}))
                .thenReturn(expectedResponse);

        Employee actualEmployee = service.createEmployee(employee);

        assertThat(actualEmployee).isEqualTo(employee);
    }

    @Test
    void deleteEmployee_nullInput_throwsException() {
        assertThatThrownBy(() -> service.deleteEmployee(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(INPUT_CANNOT_BE_NULL);
    }

    @Test
    void createEmployee_nullInput_throwsException() {
        assertThatThrownBy(() -> service.createEmployee(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(INPUT_CANNOT_BE_NULL);
    }

    private void updateIds(List<Employee> employees, List<Long> ids) {
        if (employees.size() != ids.size()) {
            throw new IllegalArgumentException("Mismatch between number of employees and ids.");
        }

        for (int i=0; i<employees.size(); i++) {
            employees.get(i).setId(ids.get(i));
        }
    }

    private void updateNames(List<Employee> employees, List<String> names) {
        if (employees.size() != names.size()) {
            throw new IllegalArgumentException("Mismatch between number of employees and names.");
        }

        for (int i=0; i<employees.size(); i++) {
            employees.get(i).setEmployeeName(names.get(i));
        }
    }

    private void mockEmployeesResponse(List<Employee> employees, String endpoint) {
        ResponseBody<List<Employee>> mockResponse = new ResponseBody<>("", employees, "");
        ResponseEntity<ResponseBody<List<Employee>>> expectedResponse = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        Mockito.<ResponseEntity<ResponseBody<List<Employee>>>>when(clientService.exchange(endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}))
                .thenReturn(expectedResponse);
    }

    private void mockEmployeesResponse(Employee employee) {
        ResponseBody<Employee> mockResponse = new ResponseBody<>("", employee, "");
        ResponseEntity<ResponseBody<Employee>> expectedResponse = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        Mockito.<ResponseEntity<ResponseBody<Employee>>>when(clientService.exchange(EMPLOYEE_ENDPOINT + "/" + employee.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {}))
                .thenReturn(expectedResponse);
    }

}
