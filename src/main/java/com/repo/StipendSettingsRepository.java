package com.repo;

import com.entity.StipendSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StipendSettingsRepository extends JpaRepository<StipendSettings, Long> {
}