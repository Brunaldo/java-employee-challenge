package com.example.rqchallenge.it;

public class FieldError {
    private String employeeName;
    private String employeeSalary;
    private String employeeAge;

    public FieldError() {
    }

    public FieldError(String employeeName, String employeeSalary, String employeeAge) {
        this.employeeName = employeeName;
        this.employeeSalary = employeeSalary;
        this.employeeAge = employeeAge;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeSalary() {
        return employeeSalary;
    }

    public void setEmployeeSalary(String employeeSalary) {
        this.employeeSalary = employeeSalary;
    }

    public String getEmployeeAge() {
        return employeeAge;
    }

    public void setEmployeeAge(String employeeAge) {
        this.employeeAge = employeeAge;
    }

    @Override
    public String toString() {
        return "FieldError{" +
                "employeeName='" + employeeName + '\'' +
                ", employeeSalary='" + employeeSalary + '\'' +
                ", employeeAge='" + employeeAge + '\'' +
                '}';
    }
}
