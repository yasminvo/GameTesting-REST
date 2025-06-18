package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.model.Example;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private String filePath;
    private String altPath;

    public static ImageDTO fromEntity(Image image) {
        return new ImageDTO(
                image.getFilePath(),
                image.getAltText()
        );}

}
