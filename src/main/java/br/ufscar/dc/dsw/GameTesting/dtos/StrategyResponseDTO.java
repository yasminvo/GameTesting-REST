package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> tips;
    private List<ExampleDTO> examples;

    public static StrategyResponseDTO fromEntity(Strategy strategy) {
        return new StrategyResponseDTO(
                strategy.getId(),
                strategy.getName(),
                strategy.getDescription(),
                strategy.getTips(),
                ExampleDTO.fromEntity(strategy.getExamples())
        );
    }
}

