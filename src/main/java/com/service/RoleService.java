package com.service;

import com.entity.Role;
import com.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;



    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER") // заменить roleName на "ROLE_USER"
                .orElseThrow(() -> new NoSuchElementException("Role not found in database"));
    }


}