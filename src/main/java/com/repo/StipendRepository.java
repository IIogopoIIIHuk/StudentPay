package com.repo;

import com.entity.Stipend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StipendRepository extends JpaRepository<Stipend, Long> {
    Optional<Stipend> findByTypeName(String typeName);
}