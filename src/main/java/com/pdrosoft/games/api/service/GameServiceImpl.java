package com.pdrosoft.games.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdrosoft.games.api.dto.GameDTO;
import com.pdrosoft.games.api.dto.GameExtendedDTO;
import com.pdrosoft.games.api.dto.GameInputDTO;
import com.pdrosoft.games.api.dto.GameTemplateDTO;
import com.pdrosoft.games.api.dto.PlayerDTO;
import com.pdrosoft.games.api.enums.GamePhase;
import com.pdrosoft.games.api.exception.MatchmakingValidationException;
import com.pdrosoft.games.api.exception.NotFoundException;
import com.pdrosoft.games.api.model.Game;
import com.pdrosoft.games.api.model.GameTemplate;
import com.pdrosoft.games.api.model.Player;
import com.pdrosoft.games.api.repository.GameRepository;
import com.pdrosoft.games.api.repository.GameTemplateRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = { @Autowired })
@Service
public class GameServiceImpl implements GameService {

	@NonNull
	private final GameRepository gameRepository;
	@NonNull
	private final GameTemplateRepository gameTemplateRepository;

	private PlayerDTO toPlayerDTO(Player player) {
		return Optional.ofNullable(player).map(x -> PlayerDTO.builder() //
				.id(player.getId()) //
				.username(player.getUserName()) //
				.build()).orElse(null);
	}

	private GameTemplateDTO toGameTemplateDTO(GameTemplate gameTemplate) {
		return GameTemplateDTO.builder() //
				.id(gameTemplate.getId()) //
				.name(gameTemplate.getName()) //
				.minPlayers(gameTemplate.getMinPlayers()) //
				.maxPlayers(gameTemplate.getMaxPlayers()) //
				.build();
	}

	private GameDTO toGameDTO(Game game) {
		return Optional.ofNullable(game).map(x -> GameDTO.builder() //
				.id(game.getId()) //
				.name(game.getName()) //
				.creationDate(game.getCreationDate()) //
				.players(game.getPlayers().stream().map(this::toPlayerDTO).toList()) //
				.phase(game.getPhase()) //
				.gameTemplate(toGameTemplateDTO(game.getGameTemplate())) //
				.build()).orElse(null);
	}

	private GameExtendedDTO toGameExtendedDTO(Game game) {
		return Optional.ofNullable(game).map(x -> GameExtendedDTO.builder() //
				.id(game.getId()) //
				.name(game.getName()) //
				.creationDate(game.getCreationDate()) //
				.joinCode(game.getJoinCode()) //
				.gameTemplate(toGameTemplateDTO(game.getGameTemplate())) //
				.players(game.getPlayers().stream().map(this::toPlayerDTO).toList()) //
				.phase(game.getPhase()) //
				.build()).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GameDTO> getGameList(Optional<Long> gameTemplateId, Instant dateFrom) {
		return gameRepository.getGameList(gameTemplateId, dateFrom).stream().map(this::toGameDTO).toList();
	}

	private String getDefaultGameDescription(Player host) {
		return "%s's game".formatted(host.getUserName());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO addGame(Player host, GameInputDTO gameInputDto) {
		var game = new Game();
		game.setCreationDate(Instant.now());
		game.setName(Optional.ofNullable(StringUtils.trimToNull(gameInputDto.getName()))
				.orElse(getDefaultGameDescription(host)));
		game.setJoinCode(gameInputDto.getJoinCode());

		var gameTemplateId = gameInputDto.getGameTemplateId();
		var gameTemplate = gameTemplateRepository.findById(gameTemplateId) //
				.orElseThrow(() -> new NotFoundException("game template %d not found".formatted(gameTemplateId)));

		game.setGameTemplate(gameTemplate);
		game.setPlayers(new ArrayList<Player>());
		game.setPhase(GamePhase.INIT);
		var savedGame = gameRepository.save(game);

		savedGame.getPlayers().add(host);

		gameRepository.save(savedGame);

		return Optional.of(savedGame).map(this::toGameDTO).orElseThrow();
	}

	private Optional<Game> loadGame(Long gameId) {
		return gameRepository.findById(gameId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameExtendedDTO joinGame(Player guest, Long gameId) {
		var game = loadGame(gameId)
				.orElseThrow(() -> new NotFoundException("Game %d does not exist".formatted(gameId)));

		var maxPlayers = game.getGameTemplate().getMaxPlayers();

		if (game.getPlayers().size() >= maxPlayers) {
			throw new MatchmakingValidationException("This game already has the maximum number of players");
		}

		if (game.getPlayers().contains(guest)) {
			throw new MatchmakingValidationException("In a game, the host and the guest cannot be the same user");
		}

		game.getPlayers().add(guest);

		var savedGame = gameRepository.save(game);

		return Optional.ofNullable(savedGame).map(this::toGameExtendedDTO) //
				.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
	}

	private Game loadParticipatingGame(Player player, Long gameId) {
		var game = loadGame(gameId)
				.orElseThrow(() -> new NotFoundException("Game %d does not exist".formatted(gameId)));

		if (!game.getPlayers().contains(player)) {
			throw new MatchmakingValidationException("This player does not participate in this game");
		}

		return game;
	}

	@Override
	public GameExtendedDTO startGame(Player player, Long gameId) {
		var game = loadParticipatingGame(player, gameId);

		if (game.getPlayers().size() < game.getGameTemplate().getMinPlayers()) {
			throw new MatchmakingValidationException("This game does not have the minimum number of players");
		}

		if (!GamePhase.INIT.equals(game.getPhase())) {
			throw new MatchmakingValidationException("Game is not in INIT phase");
		}

		game.setPhase(GamePhase.PLAYING);

		return Optional.ofNullable(gameRepository.save(game)).map(this::toGameExtendedDTO) //
				.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
	}

	@Override
	public GameExtendedDTO finishGame(Player player, Long gameId) {
		var game = loadParticipatingGame(player, gameId);

		if (!GamePhase.PLAYING.equals(game.getPhase())) {
			throw new MatchmakingValidationException("Game is not in PLAYING phase");
		}

		game.setPhase(GamePhase.FINISHED);

		return Optional.ofNullable(gameRepository.save(game)).map(this::toGameExtendedDTO) //
				.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
	}

	@Override
	public GameExtendedDTO abortGame(Player player, Long gameId) {
		var game = loadParticipatingGame(player, gameId);

		game.setPhase(GamePhase.ABORTED);

		return Optional.ofNullable(gameRepository.save(game)).map(this::toGameExtendedDTO) //
				.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
	}

	@Override
	@Transactional(readOnly = true)
	public GameExtendedDTO getGame(Player guest, Long gameId) {
		return loadGame(gameId).map(this::toGameExtendedDTO)
				.orElseThrow(() -> new NotFoundException("Game %d does not exist".formatted(gameId)));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO leaveGame(Player player, Long gameId) {
		return loadGame(gameId).map(game -> {

			if (game.getPlayers().contains(player)) {
				game.getPlayers().remove(player);
				return Optional.ofNullable(gameRepository.save(game)).map(this::toGameDTO) //
						.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
			}

			throw new MatchmakingValidationException(
					"Player %s is not a member of the game %s".formatted(player.getUserName(), game.getName()));
		}).orElse(null);
	}
}
