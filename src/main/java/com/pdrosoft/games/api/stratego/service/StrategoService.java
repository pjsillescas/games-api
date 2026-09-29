package com.pdrosoft.games.api.stratego.service;

import com.pdrosoft.games.api.model.Player;
import com.pdrosoft.games.api.stratego.dto.ArmySetupDTO;
import com.pdrosoft.games.api.stratego.dto.GameStateDTO;
import com.pdrosoft.games.api.stratego.dto.StrategoMovementDTO;

public interface StrategoService {

	GameStateDTO addSetup(Long gameId, Player player, ArmySetupDTO setupDto);

	GameStateDTO addMovement(Long gameId, Player player, StrategoMovementDTO movementDto);

	GameStateDTO getStatus(Long gameId, Player player);

}
