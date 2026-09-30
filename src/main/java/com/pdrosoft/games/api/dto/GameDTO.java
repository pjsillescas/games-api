package com.pdrosoft.games.api.dto;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.pdrosoft.games.api.enums.GamePhase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NullMarked
public class GameDTO {
	private Integer id;

	private String name;

	private Instant creationDate;

	private List<PlayerDTO> players;

	private GamePhase phase;

	private GameTemplateDTO gameTemplate;

}
