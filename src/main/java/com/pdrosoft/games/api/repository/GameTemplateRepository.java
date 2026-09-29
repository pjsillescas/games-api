package com.pdrosoft.games.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.pdrosoft.games.api.model.GameTemplate;

public interface GameTemplateRepository
		extends JpaRepository<GameTemplate, Long>, JpaSpecificationExecutor<GameTemplate> {

}
