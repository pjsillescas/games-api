package com.pdrosoft.games.api.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.pdrosoft.games.api.model.Game;

import jakarta.persistence.criteria.Predicate;

public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
	
	public default List<Game> getGameList(Optional<Long> gameTemplateId, Instant dateFrom) {
	    return findAll((root, query, builder) -> {
	        query.orderBy(builder.desc(root.get("creationDate")));
	        
	        var clauses = new ArrayList<Predicate>();
	        
	        clauses.add(builder.lessThan(//
            		builder.size(root.get("players")), //
            		root.get("gameTemplate").get("maxPlayers")));
	        clauses.add(builder.greaterThan(root.get("creationDate"), dateFrom));
	        
	        gameTemplateId.map(id -> builder.equal(root.get("gameTemplate").get("id"), id)).ifPresent(clauses::add);

	        return builder.and(clauses);
	    });
	}
}
