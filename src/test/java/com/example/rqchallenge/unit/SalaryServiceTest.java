package com.example.rqchallenge.unit;


import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.extension.EmployeeExtension;
import com.example.rqchallenge.extension.EmployeeExtension.EmployeesExt;
import com.example.rqchallenge.service.EmployeeService;
import com.example.rqchallenge.service.SalaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith({EmployeeExtension.class, MockitoExtension.class})
public class SalaryServiceTest {

    @Mock
    EmployeeService employeeService;

    @InjectMocks
    SalaryService salaryService;

    @Test
    void getHighestSalaryOfEmployees_returnsHighestSalary(@EmployeesExt(numberOfEmployees = 5) List<Employee> employees) {
        updateSalaries(employees, List.of(new BigDecimal("10000"),
                new BigDecimal("20000"),
                new BigDecimal("30000"),
                new BigDecimal("40000"),
                new BigDecimal("50000"))
        );

        given(employeeService.getEmployees()).willReturn(employees);

        BigDecimal expectedHighestSalary = new BigDecimal("50000");

        assertThat(expectedHighestSalary).isEqualTo(salaryService.getHighestSalaryOfEmployees().get());
    }

    @Test
    void getTop10HighestEarningEmployeeNames_returns10Employees(@EmployeesExt(numberOfEmployees = 13) List<Employee> employees) {
        updateSalaries(employees, List.of(
                new BigDecimal("130000"),
                new BigDecimal("110000"),
                new BigDecimal("120000"),
                new BigDecimal("100000"),
                new BigDecimal("80000"),
                new BigDecimal("90000"),
                new BigDecimal("70000"),
                new BigDecimal("50000"),
                new BigDecimal("60000"),
                new BigDecimal("40000"),
                new BigDecimal("30000"),
                new BigDecimal("20000"),
                new BigDecimal("10000")
                )
        );
        given(employeeService.getEmployees()).willReturn(employees);

        List<Employee> actualEmployees = salaryService.getTop10HighestEarningEmployees();

        assertThat(actualEmployees).hasSize(10)
                .extracting(Employee::getEmployeeSalary)
                .containsExactly(
                        new BigDecimal("130000"),
                        new BigDecimal("120000"),
                        new BigDecimal("110000"),
                        new BigDecimal("100000"),
                        new BigDecimal("90000"),
                        new BigDecimal("80000"),
                        new BigDecimal("70000"),
                        new BigDecimal("60000"),
                        new BigDecimal("50000"),
                        new BigDecimal("40000")
                );
    }

    private void updateSalaries(List<Employee> employees, List<BigDecimal> salaries) {
        if (employees.size() != salaries.size()) {
            throw new IllegalArgumentException("Mismatch between number of employees and salaries.");
        }

        for (int i=0; i<employees.size(); i++) {
            employees.get(i).setEmployeeSalary(salaries.get(i));
        }
    }

}
