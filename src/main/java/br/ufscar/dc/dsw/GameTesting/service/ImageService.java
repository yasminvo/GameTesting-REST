package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository; 
import br.ufscar.dc.dsw.GameTesting.model.Image; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; 

@Service
@Transactional 
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Salva uma nova Image independentemente(sem associá-la a um Example)
     * @param image 
     * @return 
     */
    public Image saveImage(Image image) {
        if (image.getFilePath() == null || image.getFilePath().trim().isEmpty()) {
            throw new IllegalArgumentException("O caminho do arquivo (filePath) da imagem não pode ser vazio.");
        }
        return imageRepository.save(image);
    }

    /**
     * Busca uma Image pelo seu ID.
     * @param id 
     * @return
     */
    @Transactional(readOnly = true) 
    public Optional<Image> findImageById(Long id) {
        return imageRepository.findById(id);
    }

    /**
     * Exclui uma Image pelo seu ID.
     * @param id
     */
    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Image com ID " + id + " não encontrada para exclusão.");
        }
    }
}