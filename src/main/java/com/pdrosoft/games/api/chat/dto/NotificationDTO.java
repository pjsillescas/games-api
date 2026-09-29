package com.pdrosoft.games.api.chat.dto;

import com.pdrosoft.games.api.stratego.enums.GamePhase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

	private GamePhase gamePhase;
	private String message;
}
