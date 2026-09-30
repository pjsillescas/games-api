package com.pdrosoft.games.api.service;

import com.pdrosoft.games.api.dto.PlayerDTO;

public interface MatchmakingService {

	PlayerDTO addPlayer(String name, String password);
}
