package com.controller;

import com.DTO.ChangePasswordRequest;
import com.entity.Role;
import com.entity.User;
import com.exception.ErrorResponse;
import com.repo.UserRepository;
import com.service.UserService;
import com.exception.AppError;
import com.DTO.UserDTO;
import com.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "name", user.getName(),
                "phone", user.getPhone(),
                "enabled", user.isEnabled(),
                "roles", roles
        );


        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody UserDTO userDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean usernameChanged = false;

        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
            usernameChanged = true;
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким email уже существует"), HttpStatus.BAD_REQUEST);
            }
            user.setEmail(userDTO.getEmail());
        }

        userRepository.save(user);

        // Формируем информацию о пользователе как в /me
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        Map<String, Object> userInfo = new java.util.HashMap<>(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "name", user.getName(),
                "phone", user.getPhone(),
                "enabled", user.isEnabled(),
                "roles", roles
        ));


        // Если username изменился — генерируем новый токен
        if (usernameChanged) {
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
            String newToken = jwtTokenUtils.generateToken(userDetails);

            // Формируем окончательный ответ с токеном в формате как на скриншоте
            Map<String, Object> response = new HashMap<>();
            response.put("phone", user.getPhone());
            response.put("roles", userInfo.get("roles"));
            response.put("name", user.getName());
            response.put("id", user.getId());
            response.put("enabled", user.isEnabled());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("token", newToken);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }



    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));


        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(true, "Неверно введен старый пароль"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Пароль успешно изменён");
    }
}