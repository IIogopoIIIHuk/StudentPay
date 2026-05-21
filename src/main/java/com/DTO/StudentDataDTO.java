package com.DTO;

import com.entity.User;
import com.entity.Role;
import com.entity.StudentDetails;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudentDataDTO {

    private Long id;
    private String name;
    private String login;
    private String phone;
    private String email;
    private Double gpa;
    private Integer absencesHours;
    private Boolean hasRetakes;
    private Double bonusAmount;
    private String stipendType;
    private Boolean hasStipend;
    private Boolean isBrsmMember;
    private Boolean isProfkomMember;
    private List<String> roles;

    public static StudentDataDTO fromUserAndDetails(User user) {
        if (user.getStudentDetails() == null) {
            return null;
        }

        StudentDataDTO dto = new StudentDataDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLogin(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setGpa(user.getStudentDetails().getGpa());
        dto.setAbsencesHours(user.getStudentDetails().getAbsencesHours());
        dto.setHasRetakes(user.getStudentDetails().getHasRetakes());
        dto.setBonusAmount(user.getStudentDetails().getBonusAmount());
        dto.setStipendType(user.getStudentDetails().getStipendType());
        dto.setHasStipend(user.getStudentDetails().getHasStipend());
        dto.setIsBrsmMember(user.isBrsmMember());
        dto.setIsProfkomMember(user.isProfkomMember());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream().map(Role::getName).toList());
        }

        return dto;
    }
}