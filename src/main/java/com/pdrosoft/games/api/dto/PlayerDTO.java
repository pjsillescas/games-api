package com.pdrosoft.games.api.dto;

import org.jspecify.annotations.NullMarked;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NullMarked
public class PlayerDTO {
	private Integer id;

	private String username;
}
