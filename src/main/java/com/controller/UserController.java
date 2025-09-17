package com.controller;

import com.DTO.UserDTO;
import com.entity.Role;
import com.entity.User;
import com.repo.RoleRepository;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            userDTO.setPhone(user.getPhone());
            userDTO.setEnabled(user.isEnabled());
            userDTO.setBrsmMember(user.isBrsmMember());
            userDTO.setProfkomMember(user.isProfkomMember());

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            userDTO.setRoles(roles);

            return userDTO;
        }).toList();
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/editRole/{id}")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<?> editUserRole(@PathVariable Long id, @RequestParam String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        user.setRoles(new ArrayList<>(List.of(role)));

        userRepository.save(user);

        return ResponseEntity.ok(toUserDTO(user));
    }

    @PutMapping("/editStudent/{id}")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<?> editStudentDetails(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.isEnabled() != user.isEnabled()) {
            user.setEnabled(userDTO.isEnabled());
        }
        user.setBrsmMember(userDTO.isBrsmMember());
        user.setProfkomMember(userDTO.isProfkomMember());

        userRepository.save(user);

        return ResponseEntity.ok(toUserDTO(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        userRepository.delete(user);

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isEnabled(),
                roles
        );

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id, @RequestParam boolean enable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enable);
        userRepository.save(user);

        return ResponseEntity.ok(toUserDTO(user));
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setBrsmMember(user.isBrsmMember());
        userDTO.setProfkomMember(user.isProfkomMember());
        userDTO.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        return userDTO;
    }
}