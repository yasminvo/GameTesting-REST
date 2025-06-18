package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.ExampleDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.repository.ExampleRepository;
import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository; 
import br.ufscar.dc.dsw.GameTesting.repository.StrategyRepository; 
import br.ufscar.dc.dsw.GameTesting.model.Image;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional 
public class ExampleService {

    private final ExampleRepository exampleRepository;
    private final ImageRepository imageRepository; 
    private final StrategyRepository strategyRepository; 

    @Autowired
    public ExampleService(ExampleRepository exampleRepository, 
                          ImageRepository imageRepository,
                          StrategyRepository strategyRepository) {
        this.exampleRepository = exampleRepository;
        this.imageRepository = imageRepository;
        this.strategyRepository = strategyRepository;
    }


    @Transactional
    public ExampleDTO save(ExampleDTO dto, Long strategyId) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException("Strategy com ID " + strategyId + " não encontrada."));

        Example example = new Example();
        example.setText(dto.getText());
        example.setStrategy(strategy);

        if (dto.getImage() != null) {
            Image image = new Image();
            image.setFilePath(dto.getImage().getFilePath());
            image.setAltText(dto.getImage().getAltPath());
            example.setImage(image);
        }

        Example exampleResponse = exampleRepository.save(example);
        return ExampleDTO.fromEntity(exampleResponse);

    }


    @Transactional(readOnly = true)
    public Optional<Example> findById(Long id) {
        return exampleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Example> findAll() {
        return exampleRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<ExampleDTO> findByStrategyId(Long strategyId) {
        if (!strategyRepository.existsById(strategyId)) {
            throw new EntityNotFoundException("Strategy com ID " + strategyId + " não encontrada.");
        }

        List<Example> examples = exampleRepository.findByStrategyId(strategyId);

        return examples.stream()
                .map(example -> ExampleDTO.builder()
                        .id(example.getId())
                        .text(example.getText())
                        .image(ImageDTO.fromEntity(example.getImage()))
                        .build())
                .toList();
    }

    public void delete(Long id) {
        if (exampleRepository.existsById(id)) {
            exampleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Example com ID " + id + " não encontrado para exclusão.");
        }
    }



}