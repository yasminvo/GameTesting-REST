package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.repository.ExampleRepository;
import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository; 
import br.ufscar.dc.dsw.GameTesting.repository.StrategyRepository; 
import br.ufscar.dc.dsw.GameTesting.model.Image;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
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

    /**
     * Salva um novo Example.
     * Se o Example contém uma Image, ela também será persistida em cascata.
     * O Example deve ser associado a uma Strategy existente.
     *
     * @param example 
     * @param strategyId 
     * @return 
     */
//    public Example save(Example example, Long strategyId) {
//        Strategy strategy = strategyRepository.findById(strategyId)
//                .orElseThrow(() -> new RuntimeException("Strategy com ID " + strategyId + " não encontrada."));
//
//        example.setStrategy(strategy);
//        if (example.getImage() != null) {
//            example.getImage().setExample(example);
//        }
//        return exampleRepository.save(example);
//    }

    /**
     * Busca um Example pelo seu ID.
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Example> findById(Long id) {
        return exampleRepository.findById(id);
    }

    /**
     * Lista todos os Examples.
     * @return Uma lista de todos os Examples.
     */
    @Transactional(readOnly = true)
    public List<Example> findAll() {
        return exampleRepository.findAll();
    }

    /**
     * Lista todos os Examples associados a uma Strategy específica.
     * @param strategyId 
     * @return 
     */
    @Transactional(readOnly = true)
    public List<Example> findByStrategyId(Long strategyId) {
        return exampleRepository.findByStrategyId(strategyId);
    }

    /**
     * Exclui um Example pelo seu ID.
     * @param id O ID do Example a ser excluído.
     */
    public void delete(Long id) {
        if (exampleRepository.existsById(id)) {
            exampleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Example com ID " + id + " não encontrado para exclusão.");
        }
    }
    
    
    /**
     * Salva uma nova Image independentemente.
     * @param image 
     * @return
     */
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Busca uma Image pelo seu ID.
     * @param id O ID da Image.
     * @return Um Optional contendo a Image se encontrada, ou vazio.
     */
    @Transactional(readOnly = true)
    public Optional<Image> findImageById(Long id) {
        return imageRepository.findById(id);
    }

    /**
     * Exclui uma Image pelo seu ID.
     * @param id O ID da Image a ser excluída.
     */
    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Image com ID " + id + " não encontrada para exclusão.");
        }
    }
}