package com.pdrosoft.games.api.dto;

import org.jspecify.annotations.NullMarked;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NullMarked
public class GameTemplateDTO {
	private Integer id;

	private String name;

	private Integer minPlayers;
	private Integer maxPlayers;
}
