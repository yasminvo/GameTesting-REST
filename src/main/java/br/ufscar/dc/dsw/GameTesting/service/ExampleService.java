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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional 
public class ExampleService {

    private final ExampleRepository exampleRepository;
    private final ImageRepository imageRepository; 
    private final StrategyRepository strategyRepository;
    private final MessageSource messageSource;

    @Autowired
    public ExampleService(ExampleRepository exampleRepository, 
                          ImageRepository imageRepository,
                          StrategyRepository strategyRepository,
                          MessageSource messageSource) {
        this.exampleRepository = exampleRepository;
        this.imageRepository = imageRepository;
        this.strategyRepository = strategyRepository;
        this.messageSource = messageSource;
    }


    @Transactional
    public ExampleDTO save(ExampleDTO dto, Long strategyId, Locale locale) {
        Strategy strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException(
                    messageSource.getMessage("strategy.notfound", new Object[]{strategyId}, locale)
                ));

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
    public List<ExampleDTO> findByStrategyId(Long strategyId, Locale locale) {
        if (!strategyRepository.existsById(strategyId)) {
            throw new EntityNotFoundException(
                messageSource.getMessage("strategy.notfound", new Object[]{strategyId}, locale)
            );
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

    public void delete(Long id, Locale locale) {
        if (exampleRepository.existsById(id)) {
            exampleRepository.deleteById(id);
        } else {
            throw new RuntimeException(
                messageSource.getMessage("example.notfound.delete", new Object[]{id}, locale)
            );
        }
    }

}
