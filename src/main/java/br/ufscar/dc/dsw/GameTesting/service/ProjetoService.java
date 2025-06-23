package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.ProjetoDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.UserDTO;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.ProjetoRepository;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
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

    public List<ProjetoDTO> listByTesterIdSorted(Long userId, String sort) {
        Sort sorting;
        switch (sort.toLowerCase()) {
            case "name":
                sorting = Sort.by("name").ascending(); break;
            case "name_desc":
                sorting = Sort.by("name").descending(); break;
            case "creationdate_desc":
                sorting = Sort.by("creationDate").descending(); break;
            case "creationdate":
            default:
                sorting = Sort.by("creationDate").ascending(); break;
        }

        List<Projeto> projetos = projetoRepository.findByMembersId(userId, sorting);

        return projetos.stream()
                .map(ProjetoDTO::fromEntity)
                .collect(Collectors.toList());
    }



    public ProjetoDTO getById(Long id) {
        return projetoRepository.findById(id)
                .map(ProjetoDTO::fromEntity)
                .orElseThrow(() -> new AppException("Projeto não encontrado.", HttpStatus.NOT_FOUND));
    }

    public ProjetoDTO createProjeto(ProjetoDTO projetoDTO) {
        if (projetoDTO.getName() == null || projetoDTO.getName().isBlank()) {
            throw new AppException("Nome do projeto é obrigatório.", HttpStatus.BAD_REQUEST);
        }

        if (projetoDTO.getDescription() == null || projetoDTO.getDescription().isBlank()) {
            throw new AppException("Descrição do projeto é obrigatório.", HttpStatus.BAD_REQUEST);
        }

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

    public ProjetoDTO updateProjeto(Long id, ProjetoDTO projetoDTO) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new AppException("Projeto não encontrado para atualização.", HttpStatus.NOT_FOUND));

        projeto.setName(projetoDTO.getName());
        projeto.setDescription(projetoDTO.getDescription());
        projeto.setMembers(projetoDTO.getMembers()
                .stream()
                .map(UserDTO::toEntity)
                .collect(Collectors.toList()));
        projeto.setCreationDate(LocalDateTime.now());

        Projeto updated = projetoRepository.save(projeto);
        return ProjetoDTO.fromEntity(updated);
    }

    public boolean deleteProjeto(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new AppException("Projeto não encontrado para exclusão.", HttpStatus.NOT_FOUND));

        if (projeto.getSessions() != null && !projeto.getSessions().isEmpty()) {
            throw new AppException("Não é possível deletar projeto. Está associado a alguma sessão.", HttpStatus.BAD_REQUEST);
        }

        projetoRepository.deleteById(id);
        return true;
    }
}
