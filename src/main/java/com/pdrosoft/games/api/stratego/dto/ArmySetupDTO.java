package com.pdrosoft.games.api.stratego.dto;

import java.util.List;

import com.pdrosoft.games.api.stratego.enums.Rank;
import com.pdrosoft.games.api.stratego.validation.ArmySetupValidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArmySetupDTO {
	@ArmySetupValidation
	private List<List<Rank>> army;
}
