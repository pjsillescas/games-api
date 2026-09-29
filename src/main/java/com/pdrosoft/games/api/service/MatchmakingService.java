package com.pdrosoft.games.api.service;

import java.time.Instant;
import java.util.List;

import com.pdrosoft.games.api.dto.GameDTO;
import com.pdrosoft.games.api.dto.GameExtendedDTO;
import com.pdrosoft.games.api.dto.GameInputDTO;
import com.pdrosoft.games.api.dto.PlayerDTO;
import com.pdrosoft.games.api.model.Player;

public interface MatchmakingService {

	List<GameDTO> getGameList(Instant dateFrom);

	PlayerDTO addPlayer(String name, String password);

	GameDTO addGame(Player host, GameInputDTO gameInputDto);

	GameExtendedDTO joinGame(Player guest, Long gameId);

	GameDTO leaveGame(Player player, Long gameId);

	GameExtendedDTO getGame(Player player, Long gameId);

}
