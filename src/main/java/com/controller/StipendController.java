package com.controller;

import com.DTO.StipendDataDTO;
import com.DTO.StipendSettingsDTO;
import com.entity.Stipend;
import com.entity.StipendSettings;
import com.repo.StipendSettingsRepository;
import com.service.StipendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stipends")
public class StipendController {

    private final StipendService stipendService;
    private final StipendSettingsRepository stipendSettingsRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<List<StipendDataDTO>> getAllStipends() {
        List<Stipend> stipends = stipendService.getAllStipends();
        List<StipendDataDTO> stipendDTOs = stipends.stream()
                .map(StipendDataDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stipendDTOs);
    }
    @PutMapping("/{id}/amount")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<StipendDataDTO> updateStipendAmount(@PathVariable Long id, @RequestBody Double amount) {
        return stipendService.updateStipendAmount(id, amount)
                .map(StipendDataDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<StipendSettingsDTO> getStipendSettings() {
        return stipendService.getStipendSettings()
                .map(StipendSettingsDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<StipendSettingsDTO> updateStipendSettings(@RequestBody StipendSettingsDTO settingsDTO) {
        StipendSettings settings = new StipendSettings();
        settings.setProfkomDeductionPercent(settingsDTO.getProfkomDeductionPercent());
        settings.setBrsmDeductionPercent(settingsDTO.getBrsmDeductionPercent());

        StipendSettings savedSettings = stipendService.updateStipendSettings(settings);
        return ResponseEntity.ok(StipendSettingsDTO.fromEntity(savedSettings));
    }
}