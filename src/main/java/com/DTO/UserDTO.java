package com.DTO;

import com.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String phone;
    private boolean enabled;
    private List<String> roles;
    private List<String> subjects;

    public UserDTO(Long id, String username, String email, String name, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public UserDTO(Long id, String username, String email, String name, String phone, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
    }

    public UserDTO(Long id, String username, String email, String name, String phone, boolean enabled, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
        this.roles = roles;
    }

    public UserDTO(Long id, String username, String email, String name, String phone, boolean enabled, List<String> roles, List<String> subjects) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
        this.roles = roles;
        this.subjects = subjects;
    }

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName()).toList());
        return dto;
    }
}