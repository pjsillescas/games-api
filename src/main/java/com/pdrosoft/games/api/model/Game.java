package com.pdrosoft.games.api.model;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;

import com.pdrosoft.games.api.stratego.enums.GamePhase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(name = "join_code", nullable = false)
	private String joinCode;

	@Column(name = "creation_date", nullable = false)
	@JdbcType(TimestampJdbcType.class)
	private Instant creationDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private GamePhase phase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_template", nullable = false)
	private GameTemplate gameTemplate;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "game_player", //
			joinColumns = @JoinColumn(name = "game_id"), //
			inverseJoinColumns = @JoinColumn(name = "player_id"))
	private List<Player> players;
}
