package com.pdrosoft.games.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdrosoft.games.api.dto.PlayerDTO;
import com.pdrosoft.games.api.exception.PlayerExistsException;
import com.pdrosoft.games.api.model.Player;
import com.pdrosoft.games.api.repository.PlayerRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = { @Autowired })
@Service
public class MatchmakingServiceImpl implements MatchmakingService {

	@NonNull
	private final PlayerRepository playerRepository;
	@NonNull
	private final PasswordEncoder passwordEncoder;

	private PlayerDTO toPlayerDTO(Player player) {
		return PlayerDTO.builder().id(player.getId()).username(player.getUserName()).build();

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PlayerDTO addPlayer(String name, String password) {
		var playerOpt = playerRepository.findPlayersByName(name);

		if (playerOpt.isPresent()) {
			throw new PlayerExistsException("player already exists '%s'".formatted(name));
		}

		var player = new Player();
		player.setUserName(name);
		player.setPassword(passwordEncoder.encode(password));

		return Optional.ofNullable(playerRepository.save(player)).map(this::toPlayerDTO).orElseThrow();
	}
}
