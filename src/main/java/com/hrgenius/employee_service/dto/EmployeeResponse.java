package com.hrgenius.employee_service.dto;

import com.hrgenius.employee_service.entity.Employee;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private Double salary;
    private LocalDate joiningDate;
    private Employee.Status status;
}