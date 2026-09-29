package com.pdrosoft.games.api.stratego.service;

import com.pdrosoft.games.api.stratego.enums.Rank;

public interface RankService {

	int compareRanks(Rank rankAttacker, Rank rankDefender);
}
