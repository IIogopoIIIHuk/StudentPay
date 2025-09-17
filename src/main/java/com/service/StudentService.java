package com.service;

import com.DTO.StudentDataDTO;
import com.entity.StudentDetails;
import com.entity.User;
import com.repo.StudentDetailsRepository;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentDetailsRepository studentDetailsRepository;

    @Transactional
    public StudentDataDTO updateStudentDetails(Long userId, StudentDataDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (dto.getIsBrsmMember() != null) {
            user.setBrsmMember(dto.getIsBrsmMember());
        }
        if (dto.getIsProfkomMember() != null) {
            user.setProfkomMember(dto.getIsProfkomMember());
        }

        StudentDetails details = user.getStudentDetails();
        if (details == null) {
            details = new StudentDetails(user);
        }

        if (dto.getGpa() != null) {
            details.setGpa(dto.getGpa());
        }
        if (dto.getAbsencesHours() != null) {
            details.setAbsencesHours(dto.getAbsencesHours());
        }
        if (dto.getHasRetakes() != null) {
            details.setHasRetakes(dto.getHasRetakes());
        }
        if (dto.getStipendType() != null && !dto.getStipendType().isEmpty()) {
            details.setStipendType(dto.getStipendType());
        }
        if (dto.getBonusAmount() != null) {
            details.setBonusAmount(dto.getBonusAmount());
        }

        boolean hasStipend = true;
        if (details.getHasRetakes()) {
            hasStipend = false;
        }
        if (details.getGpa() < 5.0) {
            hasStipend = false;
        }
        if (details.getAbsencesHours() > 10) {
            hasStipend = false;
        }
        if ("Социальная стипендия".equals(details.getStipendType())) {
            hasStipend = true;
        }
        details.setHasStipend(hasStipend);

        userRepository.save(user);

        return StudentDataDTO.fromUserAndDetails(user);
    }

    public List<StudentDataDTO> findStudentsByName(String name) {
        List<User> users = userRepository.findByNameContaining("ROLE_USER", name);
        return users.stream()
                .map(StudentDataDTO::fromUserAndDetails)
                .collect(Collectors.toList());
    }

    public List<StudentDataDTO> filterStudents(Boolean hasNoRetakes, Boolean lessThan11AbsenceHours, Boolean gpaNotLowerThan5) {
        List<User> users = userRepository.findByRoleName("ROLE_USER");
        return users.stream()
                .filter(user -> {
                    StudentDetails details = user.getStudentDetails();
                    if (details == null) return false;

                    boolean passesFilters = true;
                    if (hasNoRetakes != null && hasNoRetakes) {
                        passesFilters = passesFilters && !details.getHasRetakes();
                    }
                    if (lessThan11AbsenceHours != null && lessThan11AbsenceHours) {
                        passesFilters = passesFilters && details.getAbsencesHours() <= 10;
                    }
                    if (gpaNotLowerThan5 != null && gpaNotLowerThan5) {
                        passesFilters = passesFilters && details.getGpa() >= 5.0;
                    }
                    return passesFilters;
                })
                .map(StudentDataDTO::fromUserAndDetails)
                .collect(Collectors.toList());
    }
}