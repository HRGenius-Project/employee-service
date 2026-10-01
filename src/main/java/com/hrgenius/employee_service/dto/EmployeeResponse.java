package com.hrgenius.employee_service.dto;

import com.hrgenius.employee_service.entity.Employee;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private Double salary;
    private LocalDate joiningDate;
    private Employee.Status status;
    private String temporaryPassword;
}