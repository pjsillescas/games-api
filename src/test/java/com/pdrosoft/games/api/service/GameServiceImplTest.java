package com.pdrosoft.games.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pdrosoft.games.api.dto.GameDTO;
import com.pdrosoft.games.api.dto.GameExtendedDTO;
import com.pdrosoft.games.api.dto.GameInputDTO;
import com.pdrosoft.games.api.enums.GamePhase;
import com.pdrosoft.games.api.exception.MatchmakingValidationException;
import com.pdrosoft.games.api.exception.NotFoundException;
import com.pdrosoft.games.api.model.Game;
import com.pdrosoft.games.api.model.GameTemplate;
import com.pdrosoft.games.api.model.Player;
import com.pdrosoft.games.api.repository.GameRepository;
import com.pdrosoft.games.api.repository.GameTemplateRepository;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

	private static final Long GAME_ID = 1L;
	private static final Long GAME_TEMPLATE_ID = 10L;
	private static final Integer PLAYER_ID = 100;
	private static final String PLAYER_USERNAME = "testuser";
	private static final String GUEST_USERNAME = "guest";
	private static final Integer GUEST_ID = 200;
	private static final String JOIN_CODE = "ABCD1234";
	private static final Instant CREATION_DATE = Instant.parse("2020-01-01T00:00:00Z");

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GameTemplateRepository gameTemplateRepository;

	@InjectMocks
	private GameServiceImpl gameService;

	private Player host;
	private Player guest;
	private GameTemplate gameTemplate;
	private Game game;

	@BeforeEach
	void setUp() {
		host = new Player();
		host.setId(PLAYER_ID);
		host.setUserName(PLAYER_USERNAME);
		host.setPassword("password");

		guest = new Player();
		guest.setId(GUEST_ID);
		guest.setUserName(GUEST_USERNAME);
		guest.setPassword("password");

		gameTemplate = new GameTemplate();
		gameTemplate.setId(GAME_TEMPLATE_ID.intValue());
		gameTemplate.setName("Test Template");
		gameTemplate.setMinPlayers(2);
		gameTemplate.setMaxPlayers(4);

		game = new Game();
		game.setId(GAME_ID.intValue());
		game.setName("Test Game");
		game.setJoinCode(JOIN_CODE);
		game.setCreationDate(CREATION_DATE);
		game.setPhase(GamePhase.INIT);
		game.setGameTemplate(gameTemplate);
		game.setPlayers(new ArrayList<>(List.of(host)));
	}

	@Test
	void testGetGameList_WithTemplateIdAndDate() {
		Instant dateFrom = Instant.parse("2019-01-01T00:00:00Z");
		Game game2 = new Game();
		game2.setId(2);
		game2.setName("Game 2");
		game2.setCreationDate(CREATION_DATE.plusSeconds(100));
		game2.setPhase(GamePhase.INIT);
		game2.setGameTemplate(gameTemplate);
		game2.setPlayers(new ArrayList<>(List.of(host, guest)));

		Mockito.when(gameRepository.getGameList(Optional.of(GAME_TEMPLATE_ID), dateFrom))
				.thenReturn(List.of(game2, game));

		List<GameDTO> result = gameService.getGameList(Optional.of(GAME_TEMPLATE_ID), dateFrom);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getId()).isEqualTo(2);
		assertThat(result.get(0).getName()).isEqualTo("Game 2");
		assertThat(result.get(0).getPlayers()).hasSize(2);
		assertThat(result.get(1).getId()).isEqualTo(GAME_ID.intValue());
		Mockito.verify(gameRepository).getGameList(Optional.of(GAME_TEMPLATE_ID), dateFrom);
		Mockito.verifyNoMoreInteractions(gameRepository, gameTemplateRepository);
	}

	@Test
	void testGetGameList_WithoutTemplateId() {
		Instant dateFrom = Instant.parse("2019-01-01T00:00:00Z");
		Mockito.when(gameRepository.getGameList(Optional.empty(), dateFrom)).thenReturn(List.of(game));

		List<GameDTO> result = gameService.getGameList(Optional.empty(), dateFrom);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(GAME_ID.intValue());
		assertThat(result.get(0).getPhase()).isEqualTo(GamePhase.INIT);
		Mockito.verify(gameRepository).getGameList(Optional.empty(), dateFrom);
		Mockito.verifyNoMoreInteractions(gameRepository, gameTemplateRepository);
	}

	@Test
	void testAddGame_WithNameProvided() {
		GameInputDTO input = GameInputDTO.builder().name("My Custom Game").joinCode(JOIN_CODE)
				.gameTemplateId(GAME_TEMPLATE_ID).build();

		Mockito.when(gameTemplateRepository.findById(GAME_TEMPLATE_ID)).thenReturn(Optional.of(gameTemplate));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> {
			Game g = invocation.getArgument(0);
			g.setId(GAME_ID.intValue());
			g.setCreationDate(CREATION_DATE);
			return g;
		});

		GameDTO result = gameService.addGame(host, input);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(GAME_ID.intValue());
		assertThat(result.getName()).isEqualTo("My Custom Game");
		assertThat(result.getPhase()).isEqualTo(GamePhase.INIT);
		assertThat(result.getPlayers()).hasSize(1);

		ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
		Mockito.verify(gameRepository, Mockito.times(2)).save(gameCaptor.capture());
		assertThat(gameCaptor.getAllValues().get(0).getName()).isEqualTo("My Custom Game");
		assertThat(gameCaptor.getAllValues().get(0).getJoinCode()).isEqualTo(JOIN_CODE);
		assertThat(gameCaptor.getAllValues().get(0).getGameTemplate()).isEqualTo(gameTemplate);
		assertThat(gameCaptor.getAllValues().get(1).getPlayers()).contains(host);

		Mockito.verify(gameTemplateRepository).findById(GAME_TEMPLATE_ID);
		Mockito.verifyNoMoreInteractions(gameRepository, gameTemplateRepository);
	}

	@Test
	void testAddGame_WithNullName_UsesDefault() {
		GameInputDTO input = GameInputDTO.builder().name(null).joinCode(JOIN_CODE).gameTemplateId(GAME_TEMPLATE_ID)
				.build();

		Mockito.when(gameTemplateRepository.findById(GAME_TEMPLATE_ID)).thenReturn(Optional.of(gameTemplate));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> {
			Game g = invocation.getArgument(0);
			g.setId(GAME_ID.intValue());
			g.setCreationDate(CREATION_DATE);
			return g;
		});

		GameDTO result = gameService.addGame(host, input);

		assertThat(result.getName()).isEqualTo(PLAYER_USERNAME + "'s game");
		ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
		Mockito.verify(gameRepository, Mockito.times(2)).save(gameCaptor.capture());
		assertThat(gameCaptor.getAllValues().get(0).getName()).isEqualTo(PLAYER_USERNAME + "'s game");
		Mockito.verify(gameTemplateRepository).findById(GAME_TEMPLATE_ID);
	}

	@Test
	void testAddGame_WithEmptyName_UsesDefault() {
		GameInputDTO input = GameInputDTO.builder().name("   ").joinCode(JOIN_CODE).gameTemplateId(GAME_TEMPLATE_ID)
				.build();

		Mockito.when(gameTemplateRepository.findById(GAME_TEMPLATE_ID)).thenReturn(Optional.of(gameTemplate));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> {
			Game g = invocation.getArgument(0);
			g.setId(GAME_ID.intValue());
			g.setCreationDate(CREATION_DATE);
			return g;
		});

		GameDTO result = gameService.addGame(host, input);

		assertThat(result.getName()).isEqualTo(PLAYER_USERNAME + "'s game");
	}

	@Test
	void testAddGame_GameTemplateNotFound() {
		GameInputDTO input = GameInputDTO.builder().name("Game").joinCode(JOIN_CODE).gameTemplateId(999L).build();

		Mockito.when(gameTemplateRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.addGame(host, input)).isInstanceOf(NotFoundException.class)
				.hasMessage("game template 999 not found");

		Mockito.verify(gameTemplateRepository).findById(999L);
		Mockito.verifyNoInteractions(gameRepository);
		Mockito.verifyNoMoreInteractions(gameTemplateRepository);
	}

	@Test
	void testGetGame_Success() {
		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		GameExtendedDTO result = gameService.getGame(guest, GAME_ID);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(GAME_ID.intValue());
		assertThat(result.getJoinCode()).isEqualTo(JOIN_CODE);
		assertThat(result.getPlayers()).hasSize(1);
		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository, gameTemplateRepository);
	}

	@Test
	void testGetGame_NotFound() {
		Mockito.when(gameRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.getGame(guest, 999L)).isInstanceOf(NotFoundException.class)
				.hasMessage("Game 999 does not exist");

		Mockito.verify(gameRepository).findById(999L);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testJoinGame_Success() {
		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GameExtendedDTO result = gameService.joinGame(guest, GAME_ID);

		assertThat(result).isNotNull();
		assertThat(result.getPlayers()).hasSize(2);
		assertThat(result.getPhase()).isEqualTo(GamePhase.INIT);

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verify(gameRepository).save(game);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testJoinGame_GameFull() {
		game.getPlayers().add(getTestPlayer(GUEST_ID + 1, "player2"));
		game.getPlayers().add(getTestPlayer(GUEST_ID + 2, "player3"));
		game.getPlayers().add(getTestPlayer(GUEST_ID + 3, "player4"));
		gameTemplate.setMaxPlayers(4);

		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.joinGame(guest, GAME_ID))
				.isInstanceOf(MatchmakingValidationException.class)
				.hasMessage("This game already has the maximum number of players");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testJoinGame_AlreadyInGame() {
		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.joinGame(host, GAME_ID)).isInstanceOf(MatchmakingValidationException.class)
				.hasMessage("In a game, the host and the guest cannot be the same user");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testJoinGame_NotFound() {
		Mockito.when(gameRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.joinGame(guest, 999L)).isInstanceOf(NotFoundException.class)
				.hasMessage("Game 999 does not exist");

		Mockito.verify(gameRepository).findById(999L);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testStartGame_Success() {
		game.getPlayers().clear();
		game.getPlayers().add(host);
		game.getPlayers().add(guest);
		game.getPlayers().add(getTestPlayer(PLAYER_ID + 1, "other"));
		game.setPhase(GamePhase.INIT);
		gameTemplate.setMinPlayers(2);

		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GameExtendedDTO result = gameService.startGame(host, GAME_ID);

		assertThat(result.getPhase()).isEqualTo(GamePhase.PLAYING);
		assertThat(result.getPlayers()).hasSize(3);

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verify(gameRepository).save(game);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testStartGame_NotInGame() {
		game.getPlayers().clear();
		game.getPlayers().add(guest);

		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.startGame(host, GAME_ID))
				.isInstanceOf(MatchmakingValidationException.class)
				.hasMessage("This player does not participate in this game");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testStartGame_NotEnoughPlayers() {
		gameTemplate.setMinPlayers(2);

		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.startGame(host, GAME_ID))
				.isInstanceOf(MatchmakingValidationException.class)
				.hasMessage("This game does not have the minimum number of players");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testStartGame_WrongPhase() {
		game.getPlayers().clear();
		game.getPlayers().add(host);
		game.getPlayers().add(guest);
		game.getPlayers().add(getTestPlayer(PLAYER_ID + 1, "other"));
		game.setPhase(GamePhase.PLAYING);

		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.startGame(host, GAME_ID))
				.isInstanceOf(MatchmakingValidationException.class).hasMessage("Game is not in INIT phase");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testStartGame_NotFound() {
		Mockito.when(gameRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.startGame(host, 999L)).isInstanceOf(NotFoundException.class)
				.hasMessage("Game 999 does not exist");

		Mockito.verify(gameRepository).findById(999L);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testLeaveGame_Success() {
		game.getPlayers().clear();
		game.getPlayers().add(host);
		game.getPlayers().add(guest);
		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));
		Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GameDTO result = gameService.leaveGame(host, GAME_ID);

		assertThat(result).isNotNull();
		assertThat(result.getPlayers()).hasSize(1);
		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verify(gameRepository).save(game);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testLeaveGame_NotInGame() {
		game.getPlayers().clear();
		game.getPlayers().add(host);
		Mockito.when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

		assertThatThrownBy(() -> gameService.leaveGame(guest, GAME_ID))
				.isInstanceOf(MatchmakingValidationException.class)
				.hasMessageContaining("Player guest is not a member of the game");

		Mockito.verify(gameRepository).findById(GAME_ID);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	@Test
	void testLeaveGame_NotFound() {
		Mockito.when(gameRepository.findById(999L)).thenReturn(Optional.empty());

		GameDTO result = gameService.leaveGame(host, 999L);
		assertThat(result).isNull();
		Mockito.verify(gameRepository).findById(999L);
		Mockito.verifyNoMoreInteractions(gameRepository);
	}

	private static Player getTestPlayer(Integer id, String userName) {
		var player = new Player();
		player.setId(id);
		player.setUserName(userName);
		player.setPassword("password");
		return player;
	}
}
