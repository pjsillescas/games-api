package com.pdrosoft.games.api.dto;

import org.jspecify.annotations.NullMarked;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NullMarked
public class UserAuthDTO {
	@NotBlank(message = "username cannot be empty")
	private String username;

	@NotBlank(message = "password cannot be empty")
	private String password;
}
