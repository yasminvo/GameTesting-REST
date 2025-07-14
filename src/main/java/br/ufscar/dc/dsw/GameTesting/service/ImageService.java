package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional 
public class ImageService {

    private final ImageRepository imageRepository;
    private final MessageSource messageSource;

    @Autowired
    public ImageService(ImageRepository imageRepository, MessageSource messageSource) {
        this.imageRepository = imageRepository;
        this.messageSource = messageSource;
    }

    public ImageDTO saveImage(ImageDTO imageDTO, Locale locale) {
        if (imageDTO.getFilePath() == null || imageDTO.getFilePath().trim().isEmpty()) {
            String msg = messageSource.getMessage("image.filepath.empty", null, locale);
            throw new IllegalArgumentException(msg);
        }
        Image image = imageRepository.save(imageDTO.toEntity());
        return ImageDTO.fromEntity(image);
    }

    @Transactional(readOnly = true)
    public List<Image> findAllImages(Locale locale) {
        List<Image> images = imageRepository.findAll();

        if (images.isEmpty()) {
            String msg = messageSource.getMessage("image.notfound.any", null, locale);
            throw new EntityNotFoundException(msg);
        }

        return images;
    }

    public void deleteImage(Long id, Locale locale) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            String msg = messageSource.getMessage("image.notfound.delete", new Object[]{id}, locale);
            throw new RuntimeException(msg);
        }
    }
}
