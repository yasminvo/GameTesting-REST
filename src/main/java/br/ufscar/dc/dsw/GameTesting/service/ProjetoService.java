package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.ProjetoDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.UserDTO;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.ProjetoRepository;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    private final UserRepository userRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UserRepository userRepository) {
        this.projetoRepository = projetoRepository;
        this.userRepository = userRepository;
    }

    public List<ProjetoDTO> listAllSorted(String sort) {
        List<Projeto> projetos;

        switch (sort.toLowerCase()) {
            case "name":
                projetos = projetoRepository.findAll(Sort.by("name").ascending());
                break;
            case "name_desc":
                projetos = projetoRepository.findAll(Sort.by("name").descending());
                break;
            case "creationdate_desc":
                projetos = projetoRepository.findAll(Sort.by("creationDate").descending());
                break;
            case "creationdate":
            default:
                projetos = projetoRepository.findAll(Sort.by("creationDate").ascending());
                break;
        }

        return projetos.stream()
                .map(ProjetoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<ProjetoDTO> getById(Long id) {
        return projetoRepository.findById(id)
                .map(ProjetoDTO::fromEntity);
    }

    public ProjetoDTO createProjeto(ProjetoDTO projetoDTO) {
        Projeto projeto = new Projeto();
        projeto.setName(projetoDTO.getName());
        projeto.setDescription(projetoDTO.getDescription());
        projeto.setCreationDate(LocalDateTime.now());

        List<Long> memberIds = projetoDTO.getMembers().stream()
                .map(UserDTO::getId)
                .collect(Collectors.toList());

        List<User> fullMembers = userRepository.findAllById(memberIds);

        projeto.setMembers(fullMembers);

        Projeto saved = projetoRepository.save(projeto);
        return ProjetoDTO.fromEntity(saved);
    }

    public Optional<ProjetoDTO> updateProjeto(Long id, ProjetoDTO projetoDTO) {
        Optional<Projeto> projetoOpt = projetoRepository.findById(id);

        if (projetoOpt.isEmpty()) {
            return Optional.empty();
        }

        Projeto projeto = projetoOpt.get();
        projeto.setName(projetoDTO.getName());
        projeto.setDescription(projetoDTO.getDescription());
        projeto.setMembers(projetoDTO.getMembers()
                .stream()
                .map(userDTO -> userDTO.toEntity())
                .collect(Collectors.toList()));
        projeto.setCreationDate(LocalDateTime.now());

        Projeto updated = projetoRepository.save(projeto);
        return Optional.of(ProjetoDTO.fromEntity(updated));
    }

    public boolean deleteProjeto(Long id) {
        Optional<Projeto> projetoOpt = projetoRepository.findById(id);
        if (projetoOpt.isEmpty()) {
            return false;
        }
        projetoRepository.deleteById(id);
        return true;
    }
}
