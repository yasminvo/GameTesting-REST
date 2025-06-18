package br.ufscar.dc.dsw.GameTesting.dtos;

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
        if (image == null) {
            return null;
        }
        return new ImageDTO(
                image.getFilePath(),
                image.getAltText()
        );}

    public Image toEntity() {
        Image image = new Image();
        image.setFilePath(this.filePath);
        image.setAltText(this.altPath);
        return image;
    }

}
