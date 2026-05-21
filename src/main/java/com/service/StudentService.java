package com.service;

import com.DTO.AnalyticsResponseDTO;
import com.DTO.StipendTypeStatDTO;
import com.DTO.StudentDataDTO;
import com.entity.Role;
import com.entity.StudentDetails;
import com.entity.User;
import com.repo.RoleRepository;
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
    private final RoleRepository roleRepository;

    @Transactional
    public StudentDataDTO updateStudentDetails(Long userId, StudentDataDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 1. Обновляем роли, если они переданы в запросе
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            List<Role> newRoles = dto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Роль " + roleName + " не найдена в БД")))
                    .collect(Collectors.toList());
            user.setRoles(newRoles);
        }

        // 2. Обновляем базовые данные аккаунта
        if (dto.getIsBrsmMember() != null) {
            user.setBrsmMember(dto.getIsBrsmMember());
        }
        if (dto.getIsProfkomMember() != null) {
            user.setProfkomMember(dto.getIsProfkomMember());
        }

        // 3. Проверяем: остался/стал ли пользователь студентом после обновления ролей?
        boolean isStudent = user.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getName()));

        if (isStudent) {
            // Если это студент — работаем с его учебной карточкой
            StudentDetails details = user.getStudentDetails();
            if (details == null) {
                details = new StudentDetails(user);
                user.setStudentDetails(details);
            }

            if (dto.getGpa() != null) details.setGpa(dto.getGpa());
            if (dto.getAbsencesHours() != null) details.setAbsencesHours(dto.getAbsencesHours());
            if (dto.getHasRetakes() != null) details.setHasRetakes(dto.getHasRetakes());
            if (dto.getStipendType() != null && !dto.getStipendType().isEmpty()) {
                details.setStipendType(dto.getStipendType());
            }
            if (dto.getBonusAmount() != null) details.setBonusAmount(dto.getBonusAmount());

            // 4. Полностью безопасный расчет стипендии (с защитой от null)
            boolean hasStipend = true;

            if (details.getHasRetakes() != null && details.getHasRetakes()) {
                hasStipend = false;
            }
            if (details.getGpa() != null && details.getGpa() < 5.0) {
                hasStipend = false;
            }
            if (details.getAbsencesHours() != null && details.getAbsencesHours() > 10) {
                hasStipend = false;
            }
            if ("Социальная стипендия".equals(details.getStipendType())) {
                hasStipend = true;
            }

            details.setHasStipend(hasStipend);
        } else {
            // Если пользователь НЕ студент (например, перевели в бухгалтера ROLE_ACCOUNTANT),
            // мы просто игнорируем расчеты стипендии.
            // При желании старые детали можно удалить, но безопаснее просто оставить их в покое.
        }

        userRepository.save(user);

        return StudentDataDTO.fromUserAndDetails(user);
    }

    public StudentDataDTO getStudentById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getName())))
                .map(StudentDataDTO::fromUserAndDetails)
                .orElseThrow(() -> new RuntimeException("Student not found"));
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

    public AnalyticsResponseDTO getStipendAnalytics() {
        List<User> students = userRepository.findByRoleName("ROLE_USER");

        List<StudentDetails> studentsWithStipend = students.stream()
                .map(User::getStudentDetails)
                .filter(details -> details != null && details.getHasStipend())
                .toList();

        int totalCount = studentsWithStipend.size();

        List<StipendTypeStatDTO> stats = studentsWithStipend.stream()
                .collect(Collectors.groupingBy(StudentDetails::getStipendType, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    double percent = (totalCount > 0) ? (entry.getValue() * 100.0 / totalCount) : 0.0;
                    percent = Math.round(percent * 100.0) / 100.0;
                    return new StipendTypeStatDTO(entry.getKey(), entry.getValue(), percent);
                })
                .collect(Collectors.toList());

        return AnalyticsResponseDTO.builder()
                .statistics(stats)
                .totalStudentsWithStipend(totalCount)
                .build();
    }
}