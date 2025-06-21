package br.ufscar.dc.dsw.GameTesting.service;
import br.ufscar.dc.dsw.GameTesting.dtos.CreateStrategyDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.StrategyResponseDTO;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.repository.StrategyRepository;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service 
@Transactional 
public class StrategyService {

    private final StrategyRepository strategyRepository;

    @Autowired
    public StrategyService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    public Strategy save(CreateStrategyDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new AppException("O nome da estratégia é obrigatório.", HttpStatus.BAD_REQUEST);
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new AppException("A descrição da estratégia é obrigatório.", HttpStatus.BAD_REQUEST);
        }

        Strategy strategy = new Strategy();
        strategy.setName(dto.getName());
        strategy.setDescription(dto.getDescription());
        strategy.setTips(dto.getTips());

        List<Example> examples = new ArrayList<>();

        if (dto.getExamples() != null) {
            examples = dto.getExamples().stream().map(exampleDTO -> {
                Example example = new Example();
                example.setText(exampleDTO.getText());

                ImageDTO imgDto = exampleDTO.getImage();
                if (imgDto != null) {
                    Image image = new Image();
                    image.setFilePath(imgDto.getFilePath());
                    image.setAltText(imgDto.getAltPath());
                    example.setImage(image);
                }

                example.setStrategy(strategy);
                return example;
            }).toList();
        }

        strategy.setExamples(examples);

        return strategyRepository.save(strategy);
    }


    @Transactional(readOnly = true)
    public List<StrategyResponseDTO> findAll() {
        List<Strategy> strategies = strategyRepository.findAll();

        return strategies.stream()
                .map(StrategyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StrategyResponseDTO findById(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new AppException("Estratégia com ID " + id + " não encontrada.", HttpStatus.NOT_FOUND));
        return StrategyResponseDTO.fromEntity(strategy);
    }

    @Transactional
    public StrategyResponseDTO update(Long id, CreateStrategyDTO dto) {
        Strategy existing = strategyRepository.findById(id)
                .orElseThrow(() -> new AppException("Estratégia com ID " + id + " não encontrada para atualização.", HttpStatus.NOT_FOUND));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setTips(dto.getTips());

        existing.getExamples().clear();

        if (dto.getExamples() != null) {
            List<Example> newExamples = dto.getExamples().stream().map(exampleDTO -> {
                Example example = new Example();
                example.setText(exampleDTO.getText());

                if (exampleDTO.getImage() != null) {
                    Image img = new Image();
                    img.setFilePath(exampleDTO.getImage().getFilePath());
                    img.setAltText(exampleDTO.getImage().getAltPath());
                    example.setImage(img);
                }

                example.setStrategy(existing);
                return example;
            }).toList();

            existing.getExamples().addAll(newExamples);
        }

        Strategy saved = strategyRepository.save(existing);
        return StrategyResponseDTO.fromEntity(saved);
    }


    @Transactional(readOnly = true)
    public Optional<Strategy> findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new AppException("Nome da estratégia não pode ser vazio para busca.", HttpStatus.BAD_REQUEST);
        }
        return strategyRepository.findByName(name);
    }

    public void delete(Long id) {
        if (!strategyRepository.existsById(id)) {
            throw new AppException("Estratégia com ID " + id + " não encontrada para exclusão.", HttpStatus.NOT_FOUND);
        }
        strategyRepository.deleteById(id);
    }


}