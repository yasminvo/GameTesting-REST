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
    private Long id;
    private String text;
    private ImageDTO image;

    public static List<ExampleDTO> fromEntity(List<Example> examples) {
        return examples.stream()
                .map(example -> new ExampleDTO(
                        example.getId(),
                        example.getText(),
                        ImageDTO.fromEntity(example.getImage())
                ))
                .toList();
    }

    public static ExampleDTO fromEntity(Example example) {
        ImageDTO imageDTO = null;
        if (example.getImage() != null) {
            imageDTO = ImageDTO.fromEntity(example.getImage());
        }

        return new ExampleDTO(
                example.getId(),
                example.getText(),
                imageDTO
        );
    }

}
