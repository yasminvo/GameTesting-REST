package br.ufscar.dc.dsw.GameTesting.service;
import br.ufscar.dc.dsw.GameTesting.repository.StrategyRepository;
import br.ufscar.dc.dsw.GameTesting.repository.ExampleRepository; 
import br.ufscar.dc.dsw.GameTesting.repository.ImageRepository; 

import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
import br.ufscar.dc.dsw.GameTesting.model.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.util.List;
import java.util.Optional;

@Service 
@Transactional 
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final ExampleRepository exampleRepository; 
    private final ImageRepository imageRepository; 
    
    @Autowired
    public StrategyService(StrategyRepository strategyRepository, 
                           ExampleRepository exampleRepository, 
                           ImageRepository imageRepository) {

        this.strategyRepository = strategyRepository;
        this.exampleRepository = exampleRepository;
        this.imageRepository = imageRepository;
    }

    /**
     * R5: Cadastro de estratégias (inclui criação e edição).
     * @param strategy
     * @return
     */
    public Strategy save(Strategy strategy) {
        if (strategy.getExamples() != null) {
            for (Example example : strategy.getExamples()) {
                // Garante que cada Example saiba a qual Strategy pertence
                example.setStrategy(strategy);
                // Garante que cada Image saiba a qual Example pertence 
                if (example.getImage() != null) {
                    example.getImage().setExample(example);
                }
            }
        }
        
        return strategyRepository.save(strategy);
    }

    /**
     * R6: Listagem de todas as estratégias.
     * @return lista de todas as strategies cadastradas.
     */
    @Transactional(readOnly = true)
    public List<Strategy> findAll() {
        return strategyRepository.findAll();
    }

    /**
     * Obtém uma Strategy pelo seu ID.
     * @param id O ID da Strategy.
     * @return 
     */
    @Transactional(readOnly = true)
    public Optional<Strategy> findById(Long id) {
        return strategyRepository.findById(id);
    }

    /**
     * Obtém uma Strategy pelo seu nome.
     * @param name O nome da Strategy.
     * @return Um Optional contendo a Strategy se encontrada, ou vazio.
     */
    @Transactional(readOnly = true)
    public Optional<Strategy> findByName(String name) {
        return strategyRepository.findByName(name);
    }

    /**
     * Exclui uma Strategy pelo seu ID.
     * Requer login de administrador
     * @param id O ID da Strategy a ser excluída.
     */
    public void delete(Long id) {
            // Opcional: Adicionar verificação se a Strategy existe antes de tentar deletar
        if (strategyRepository.existsById(id)) {
            strategyRepository.deleteById(id);
        } else {
            // Lançar uma exceção personalizada
            throw new RuntimeException("Strategy com ID " + id + " não encontrada para exclusão.");
        }
    }

    /**
     * Adiciona um Example a uma Strategy existente.
     *
     * @param strategyId O ID da Strategy.
     * @param example O Example a ser adicionado.
     * @return A Strategy atualizada.
     */
    public Strategy addExampleToStrategy(Long strategyId, Example example) {
        Strategy strategy = strategyRepository.findById(strategyId)
                                .orElseThrow(() -> new RuntimeException("Strategy não encontrada"));

        // Garante a referência bidirecional
        example.setStrategy(strategy);
        if (example.getImage() != null) {
            example.getImage().setExample(example);
        }

        strategy.getExamples().add(example);
        return strategyRepository.save(strategy); // Salva a Strategy para persistir o novo Example
    }

}