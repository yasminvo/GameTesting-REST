package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional 
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageDTO saveImage(ImageDTO imageDTO) {
        if (imageDTO.getFilePath() == null || imageDTO.getFilePath().trim().isEmpty()) {
            throw new IllegalArgumentException("O caminho do arquivo (filePath) da imagem não pode ser vazio.");
        }
        Image image = imageRepository.save(imageDTO.toEntity());
        return ImageDTO.fromEntity(image);
    }

    @Transactional(readOnly = true)
    public List<Image> findAllImages() {
        List<Image> images = imageRepository.findAll();

        if (images.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma imagem encontrada.");
        }

        return images;
    }

    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Image com ID " + id + " não encontrada para exclusão.");
        }
    }
}