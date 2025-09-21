package com.controller;

import com.DTO.StudentDataDTO;
import com.entity.User;
import com.repo.UserRepository;
import com.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final UserRepository userRepository;
    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_DEAN_EMPLOYEE', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<List<StudentDataDTO>> getAllStudents() {
        List<User> students = userRepository.findByRoleName("ROLE_USER");
        List<StudentDataDTO> studentData = students.stream()
                .map(StudentDataDTO::fromUserAndDetails)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentData);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_DEAN_EMPLOYEE', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<List<StudentDataDTO>> searchStudentsByName(@RequestParam String name) {
        List<StudentDataDTO> foundStudents = studentService.findStudentsByName(name);
        return ResponseEntity.ok(foundStudents);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ROLE_DEAN_EMPLOYEE', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<List<StudentDataDTO>> filterStudents(@RequestParam(required = false) Boolean hasNoRetakes,
                                                               @RequestParam(required = false) Boolean lessThan11AbsenceHours,
                                                               @RequestParam(required = false) Boolean gpaNotLowerThan5) {
        List<StudentDataDTO> filteredStudents = studentService.filterStudents(hasNoRetakes, lessThan11AbsenceHours, gpaNotLowerThan5);
        return ResponseEntity.ok(filteredStudents);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<StudentDataDTO> updateStudentDetails(@PathVariable Long id, @RequestBody StudentDataDTO studentDataDTO) {
        StudentDataDTO updatedStudent = studentService.updateStudentDetails(id, studentDataDTO);
        return ResponseEntity.ok(updatedStudent);
    }
}