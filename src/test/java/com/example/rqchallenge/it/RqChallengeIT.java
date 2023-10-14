package com.example.rqchallenge.it;

import com.example.rqchallenge.extension.EmployeeExtension;
import com.example.rqchallenge.extension.EmployeeExtension.EmployeesExt;
import com.example.rqchallenge.model.Employee;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({ EmployeeExtension.class })
class RqChallengeIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        //Keep getting org.springframework.web.client.HttpClientErrorException$TooManyRequests: 429 Too Many Requests: "{<LF>    "message": "Too Many Attempts."<LF>}"
//        goToSleep();
    }

    @Disabled
    @Test
    void getAllEmployees_success_success() {
        ResponseEntity<List<Employee>> response = getAllEmployees();

        List<Employee> body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.size()).isGreaterThan(0);
    }

    @Disabled
    @Test
    void getEmployeesByNameSearch_success() {
        List<Employee> response = getAllEmployees().getBody();

        List<Employee> expectedEmployees = response;

        goToSleep();

        Employee employee = getEmployeeByName(expectedEmployees.get(0).getEmployeeName()).getBody();

        assertThat(employee).isEqualTo(expectedEmployees.get(0));
    }

    @Disabled
    @Test
    void getEmployeeById_success() {
        ResponseEntity<List<Employee>> response = getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        goToSleep();

        List<Employee> employees = response.getBody();

        Employee expectedEmployee = employees.get(0);

        goToSleep();

        ResponseEntity<Employee> actualEmployee = getEmployee(expectedEmployee.getId());

        assertThat(actualEmployee.getBody()).isEqualTo(expectedEmployee);
    }

    @Disabled
    @Test
    void getHighestSalaryOfEmployees_success() {
        ResponseEntity<List<Employee>> response = getAllEmployees();

        BigDecimal expectedHighestSalary = response.getBody().stream()
                .distinct()
                .max(Comparator.comparing(Employee::getEmployeeSalary))
                .map(Employee::getEmployeeSalary)
                .get();

        goToSleep();

        BigDecimal actualHighestSalary = getHighestSalaries().getBody();

        assertThat(actualHighestSalary).isEqualTo(expectedHighestSalary);
    }

    @Disabled
    @Test
    void getTopTenHighestEarningEmployees_success() {
        ResponseEntity<List<Employee>> highestEarning = getHighestEarning();

        assertThat(highestEarning.getBody()).hasSize(10);
    }

    @Disabled
    @Test
    void createEmployee_success(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setId(0L);

        ResponseEntity<Employee> entity = createEmployee(expectedEmployee);

        assertThat(entity.getBody())
                .extracting(Employee::getEmployeeName, Employee::getEmployeeSalary, Employee::getEmployeeAge, Employee::getProfileImage)
                .containsExactly(expectedEmployee.getEmployeeName(), expectedEmployee.getEmployeeSalary(), expectedEmployee.getEmployeeAge(), expectedEmployee.getProfileImage());
    }

    @Disabled
    @Test
    void deleteEmployeeById() {
        long deleteById = RandomSource.create(RandomSource.MT).nextLong();

        ResponseEntity<Long> actualDeletedId = deleteEmployee(deleteById);

        assertThat(deleteById).isEqualTo(actualDeletedId.getBody());
    }

    @Test
    void createEmployee_nullName_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeName(null);

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeName)
                .isEqualTo("Employee name cannot be null");
    }

    @Test
    void createEmployee_nullSalary_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeSalary(null);

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeSalary)
                .isEqualTo("Employee salary cannot be null");
    }

    @Test
    void createEmployee_negativeSalary_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeSalary(new BigDecimal("-5"));

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeSalary)
                .isEqualTo("Employee salary cannot be negative");
    }

    @Test
    void createEmployee_youngerThan18_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeAge((short) 17);

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeAge)
                .isEqualTo("Employees cannot be younger than 18");
    }

    @Test
    void createEmployee_olderThan150_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeAge((short) 151);

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeAge)
                .isEqualTo("Employees cannot be older than 150");
    }

    @Test
    void createEmployee_ageIsNull_returns400(@EmployeesExt(numberOfEmployees = 1, withSalary = true) List<Employee> employees) {
        Employee expectedEmployee = employees.get(0);
        expectedEmployee.setEmployeeAge(null);

        ResponseEntity<FieldError> entity = postWithBody(expectedEmployee, "/employees", new ParameterizedTypeReference<>() {});

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody())
                .extracting(FieldError::getEmployeeAge)
                .isEqualTo("Employee age cannot be null");
    }

    private ResponseEntity<Employee> getEmployeeByName(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return executeRequest("/employees/search/" + name, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    public <T, R> ResponseEntity<R> postWithBody(T entity, String endpoint, ParameterizedTypeReference<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> requestEntity = new HttpEntity<>(entity, headers);

        return executeRequest(endpoint, HttpMethod.POST, requestEntity, responseType);
    }

    private ResponseEntity<Employee> createEmployee(Employee employee) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> requestEntity = new HttpEntity<>(employee, headers);
        return executeRequest("/employees", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<Long> deleteEmployee(long employeeId) {
        return executeRequest("/employees/" + employeeId, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<List<Employee>> getHighestEarning() {
        return executeRequest("/employees/topTenHighestEarningEmployeeNames", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }
    private ResponseEntity<BigDecimal> getHighestSalaries() {
        return executeRequest("/employees/highestSalary", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<Employee> getEmployee(long id) {
        return executeRequest("/employees/" + id, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<List<Employee>> getAllEmployees() {
        return executeRequest("/employees", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    private <T, R> ResponseEntity<T> executeRequest(String endpoint, HttpMethod method,
                                                    HttpEntity<R> httpEntity,
                                                    ParameterizedTypeReference<T> type) {
        return restTemplate.exchange("http://localhost:" + port + endpoint, method, httpEntity, type);
    }

    private static void goToSleep() {
        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
