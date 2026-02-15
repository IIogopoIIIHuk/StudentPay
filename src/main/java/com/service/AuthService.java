package com.service;

import com.DTO.JwtRequest;
import com.DTO.JwtResponse;
import com.DTO.RegistrationUserDTO;
import com.DTO.UserDTO;
import com.entity.Role;
import com.entity.User;
import com.exception.AppError;
import com.repo.UserRepository;
import com.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(authRequest.getUsername()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Пользователь не найден"), HttpStatus.NOT_FOUND);
        }

        if (!user.isEnabled()) {
            return new ResponseEntity<>(new AppError(HttpStatus.FORBIDDEN.value(), "Ваш аккаунт ещё не одобрен. Пожалуйста, дождитесь активации."), HttpStatus.FORBIDDEN);
        }

        UserDetails userDetails = loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token, roles));
    }

    public ResponseEntity<?> createNewUser(RegistrationUserDTO registrationUserDTO) {
        if (userService.findByUsername(registrationUserDTO.getUsername()).isPresent() ||
                userService.findByEmail(registrationUserDTO.getEmail()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанными данными уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDTO);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getPhone()));
    }

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}