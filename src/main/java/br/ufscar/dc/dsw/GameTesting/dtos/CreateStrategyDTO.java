package br.ufscar.dc.dsw.GameTesting.dtos;

import java.util.List;

import br.ufscar.dc.dsw.GameTesting.model.Example;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStrategyDTO {
    private Long id;
    private String name;
    private String description;
    private List<ExampleDTO> examples;
    private List<String> tips;

    public static CreateStrategyDTO fromEntity(Strategy strategy) {
        return new CreateStrategyDTO(
                strategy.getId(),
                strategy.getName(),
                strategy.getDescription(),
                ExampleDTO.fromEntity(strategy.getExamples()),
                strategy.getTips()
        );
    }
}
