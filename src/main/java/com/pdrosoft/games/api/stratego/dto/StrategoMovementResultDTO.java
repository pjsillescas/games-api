package com.pdrosoft.games.api.stratego.dto;

import com.pdrosoft.games.api.stratego.enums.Rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategoMovementResultDTO {

	private Rank rank;
	private boolean isHost;
}
