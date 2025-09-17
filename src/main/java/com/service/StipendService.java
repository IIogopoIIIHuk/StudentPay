package com.service;

import com.entity.Stipend;
import com.entity.StipendSettings;
import com.repo.StipendRepository;
import com.repo.StipendSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StipendService {
    private final StipendRepository stipendRepository;
    private final StipendSettingsRepository stipendSettingsRepository;

    public List<Stipend> getAllStipends() {
        return stipendRepository.findAll();
    }

    public Optional<StipendSettings> getStipendSettings() {
        return stipendSettingsRepository.findById(1L);
    }

    public StipendSettings updateStipendSettings(StipendSettings settings) {
        StipendSettings existingSettings = stipendSettingsRepository.findById(1L)
                .orElse(new StipendSettings());

        existingSettings.setProfkomDeductionPercent(settings.getProfkomDeductionPercent());
        existingSettings.setBrsmDeductionPercent(settings.getBrsmDeductionPercent());

        return stipendSettingsRepository.save(existingSettings);
    }

    public Stipend updateStipendAmount(Long stipendId, Double newAmount) {
        Stipend stipend = stipendRepository.findById(stipendId)
                .orElseThrow(() -> new RuntimeException("Stipend not found"));
        stipend.setAmount(newAmount);
        return stipendRepository.save(stipend);
    }

    public Optional<Stipend> findByTypeName(String typeName) {
        return stipendRepository.findByTypeName(typeName);
    }
}