package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Employee name cannot be null")
    @JsonProperty("employee_name")
    private String employeeName;

    @NotNull(message = "Employee salary cannot be null")
    @Min(value = 0, message = "Employee salary cannot be negative")
    @JsonProperty("employee_salary")
    private BigDecimal employeeSalary;

    @NotNull(message = "Employee age cannot be null")
    @Min(value = 18, message = "Employees cannot be younger than 18")
    @Max(value = 150, message = "Employees cannot be older than 150")
    @JsonProperty("employee_age")
    private Short employeeAge;

    @JsonProperty("profile_image")
    private String profileImage;

}
