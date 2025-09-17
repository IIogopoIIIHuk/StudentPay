package com.controller;

import com.DTO.StipendDataDTO;
import com.DTO.StudentDataDTO;
import com.entity.Stipend;
import com.entity.StipendSettings;
import com.entity.User;
import com.service.StudentService;
import com.service.StipendService;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dean")
@PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
public class DeanController {

    private final UserRepository userRepository;
    private final StudentService studentService;
    private final StipendService stipendService;


    @GetMapping("/students")
    public ResponseEntity<List<StudentDataDTO>> getAllStudents() {
        List<User> students = userRepository.findByRoleName("ROLE_USER");
        List<StudentDataDTO> studentData = students.stream()
                .map(StudentDataDTO::fromUserAndDetails)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentData);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentDataDTO> updateStudentDetails(@PathVariable Long id, @RequestBody StudentDataDTO studentDataDTO) {
        StudentDataDTO updatedStudent = studentService.updateStudentDetails(id, studentDataDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @GetMapping("/stipends")
    public ResponseEntity<List<StipendDataDTO>> getAllStipends() {
        List<Stipend> stipends = stipendService.getAllStipends();
        return ResponseEntity.ok(StipendDataDTO.fromEntityList(stipends));
    }

    @GetMapping("/stipend-settings")
    public ResponseEntity<StipendSettings> getStipendSettings() {
        Optional<StipendSettings> settings = stipendService.getStipendSettings();
        return settings.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/stipend-settings-change")
    public ResponseEntity<StipendSettings> updateStipendSettings(@RequestBody StipendSettings settings) {
        StipendSettings updatedSettings = stipendService.updateStipendSettings(settings);
        return ResponseEntity.ok(updatedSettings);
    }

    @PutMapping("/stipends/{id}/amount")
    public ResponseEntity<StipendDataDTO> updateStipendAmount(@PathVariable Long id, @RequestBody Double newAmount) {
        Stipend updatedStipend = stipendService.updateStipendAmount(id, newAmount);
        return ResponseEntity.ok(StipendDataDTO.fromEntity(updatedStipend));
    }


    @GetMapping("/students/search")
    public ResponseEntity<List<StudentDataDTO>> searchStudentsByName(@RequestParam String name) {
        List<StudentDataDTO> foundStudents = studentService.findStudentsByName(name);
        return ResponseEntity.ok(foundStudents);
    }

    @GetMapping("/students/filter")
    public ResponseEntity<List<StudentDataDTO>> filterStudents(@RequestParam(required = false) Boolean hasNoRetakes,
                                                               @RequestParam(required = false) Boolean lessThan11AbsenceHours,
                                                               @RequestParam(required = false) Boolean gpaNotLowerThan5) {
        List<StudentDataDTO> filteredStudents = studentService.filterStudents(hasNoRetakes, lessThan11AbsenceHours, gpaNotLowerThan5);
        return ResponseEntity.ok(filteredStudents);
    }

    @GetMapping("/analytics/stipend-distribution")
    public ResponseEntity<List<Map<String, Object>>> getStipendDistribution() {
        List<User> students = userRepository.findByRoleName("ROLE_USER");
        Map<String, Long> distribution = students.stream()
                .filter(user -> user.getStudentDetails() != null && user.getStudentDetails().getStipendType() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getStudentDetails().getStipendType(),
                        Collectors.counting()
                ));

        long totalStudents = students.size();
        List<Map<String, Object>> result = distribution.entrySet().stream()
                .map(entry -> {
                    long count = entry.getValue();
                    double percentage = (double) count / totalStudents * 100;
                    return Map.<String, Object>of(
                            "stipendType", entry.getKey(),
                            "count", count,
                            "percentage", Math.round(percentage)
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }


    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentsList() {
        return ResponseEntity.ok("Список выплат (заглушка)");
    }

    @GetMapping("/payments/{month}/{year}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok("Детализация выплаты (заглушка)");
    }
}