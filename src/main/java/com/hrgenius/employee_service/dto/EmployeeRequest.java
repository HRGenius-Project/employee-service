package com.hrgenius.employee_service.dto;

import com.hrgenius.employee_service.entity.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;
    private String department;
    private String designation;
    private Double salary;
    private LocalDate joiningDate;
    private Employee.Status status;
}