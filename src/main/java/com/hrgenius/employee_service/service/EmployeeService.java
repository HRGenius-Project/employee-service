package com.hrgenius.employee_service.service;

import com.hrgenius.employee_service.client.AuthServiceClient;
import com.hrgenius.employee_service.dto.EmployeeRequest;
import com.hrgenius.employee_service.dto.EmployeeResponse;
import com.hrgenius.employee_service.dto.InternalCreateUserRequest;
import com.hrgenius.employee_service.entity.Employee;
import com.hrgenius.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthServiceClient authServiceClient;

    @Value("${internal.api-key:HRGeniusSecretKey2026}")
    private String internalApiKey;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee already exists with email: " + request.getEmail());
        }

        String employeeCode = generateNextEmployeeCode();
        String temporaryPassword = generateTemporaryPassword();

        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .joiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now())
                .status(request.getStatus() != null ? request.getStatus() : Employee.Status.ONBOARDING)
                .build();

        Employee saved = employeeRepository.save(employee);

        // Internal call to Auth Service via Feign
        InternalCreateUserRequest userRequest = InternalCreateUserRequest.builder()
                .username(employeeCode)
                .email(saved.getEmail())
                .temporaryPassword(temporaryPassword)
                .roles(Set.of("EMPLOYEE"))
                .build();

        authServiceClient.createInternalUser(internalApiKey, userRequest);

        EmployeeResponse response = mapToResponse(saved);
        response.setTemporaryPassword(temporaryPassword);
        return response;
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        if (request.getJoiningDate() != null) {
            employee.setJoiningDate(request.getJoiningDate());
        }
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private String generateNextEmployeeCode() {
        return employeeRepository.findTopByOrderByEmployeeCodeDesc()
                .map(emp -> {
                    String lastCode = emp.getEmployeeCode();
                    int numericPart = Integer.parseInt(lastCode.replace("EMP", ""));
                    return String.format("EMP%04d", numericPart + 1);
                })
                .orElse("EMP0001");
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("Tmp@");
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .department(emp.getDepartment())
                .designation(emp.getDesignation())
                .salary(emp.getSalary())
                .joiningDate(emp.getJoiningDate())
                .status(emp.getStatus())
                .build();
    }
}