package com.pdrosoft.games.api.dto;

import org.jspecify.annotations.NullMarked;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NullMarked
public class GameInputDTO {
	@NotBlank
	private String joinCode;
	private String name;
	
	@Positive
	@NotNull
	private Long gameTemplateId;
}
