package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.model.Example;
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
public class ExampleDTO {
    private String text;
    private ImageDTO image;

    public static List<ExampleDTO> fromEntity(List<Example> examples) {
        return examples.stream()
                .map(example -> new ExampleDTO(
                        example.getText(),
                        ImageDTO.fromEntity(example.getImage())
                ))
                .toList();
    }
}
