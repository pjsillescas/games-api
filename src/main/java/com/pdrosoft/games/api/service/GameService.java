package com.pdrosoft.games.api.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.pdrosoft.games.api.dto.GameDTO;
import com.pdrosoft.games.api.dto.GameExtendedDTO;
import com.pdrosoft.games.api.dto.GameInputDTO;
import com.pdrosoft.games.api.model.Player;

public interface GameService {
	List<GameDTO> getGameList(Optional<Long> gameTemplateId, Instant dateFrom);

	GameDTO addGame(Player host, GameInputDTO gameInputDto);

	GameExtendedDTO joinGame(Player guest, Long gameId);

	GameDTO leaveGame(Player leavingPlayer, Long gameId);

	GameExtendedDTO getGame(Player guest, Long gameId);

	GameExtendedDTO startGame(Player player, Long gameId);

	GameExtendedDTO finishGame(Player player, Long gameId);

	GameExtendedDTO abortGame(Player player, Long gameId);
}
