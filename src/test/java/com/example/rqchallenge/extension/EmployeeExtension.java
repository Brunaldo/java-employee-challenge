package com.example.rqchallenge.extension;

import com.example.rqchallenge.model.Employee;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.internal.SeedFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class EmployeeExtension implements ParameterResolver {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface EmployeesExt {
        int numberOfEmployees() default 3;

        boolean withSalary() default false;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(EmployeesExt.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.isAnnotated(EmployeesExt.class)) {
            return generateEmployees(parameterContext);
        }
        return Collections.emptyList();
    }

    private static List<Employee> generateEmployees(ParameterContext parameterContext) {
        int numOfEmployees = parameterContext.findAnnotation(EmployeesExt.class).get().numberOfEmployees();
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < numOfEmployees; i++) {
            Employee employee = generateEmployee(parameterContext);
            employees.add(employee);
        }
        return employees;
    }

    private static Employee generateEmployee(ParameterContext parameterContext) {
        UniformRandomProvider urp = RandomSource.MT.create(SeedFactory.createLong());
        return Employee.builder()
                .id(urp.nextLong())
                .employeeName(RandomStringUtils.randomAlphabetic(10))
                .employeeAge(generateRandomShort(urp))
                .profileImage(RandomStringUtils.randomAlphanumeric(20))
                .employeeSalary(parameterContext.findAnnotation(EmployeesExt.class).get().withSalary() ? generateRandomBigDecimal() : null)
                .build();
    }

    public static short generateRandomShort(UniformRandomProvider urp) {
        return (short) urp.nextInt(150);
    }

    private static BigDecimal generateRandomBigDecimal() {
        UniformRandomProvider urp = RandomSource.MT.create(SeedFactory.createLong());

        BigDecimal lowerBound = new BigDecimal("30000");
        BigDecimal upperBound = new BigDecimal("500000");
        int precision = 2;

        BigDecimal range = upperBound.subtract(lowerBound);
        BigDecimal scaled = range.multiply(BigDecimal.valueOf(urp.nextDouble())).setScale(precision, RoundingMode.HALF_UP);
        return scaled.add(lowerBound);
    }

}
