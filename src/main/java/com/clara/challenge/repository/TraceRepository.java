package com.clara.challenge.repository;

import com.clara.challenge.entity.TraceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceRepository extends JpaRepository<TraceEntity, String> {}
