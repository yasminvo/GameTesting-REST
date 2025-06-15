package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.SessionCreateDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionResponseDTO;
import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.ProjetoRepository;
import br.ufscar.dc.dsw.GameTesting.repository.SessionRepository;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ProjetoRepository projetoRepository;
    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
                          ProjetoRepository projetoRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.projetoRepository = projetoRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionResponseDTO> listAllSorted(String sortField) {
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);
        List<Session> sessions = sessionRepository.findAll(sort);
        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Session> findSessionByIdForUser(Long sessionId, Principal principal) {
        User loggedInUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado."));

        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            return Optional.empty();
        }

        Session session = optionalSession.get();
        boolean isAdmin = Role.ADMIN.equals(loggedInUser.getRole());
        boolean isOwner = session.getTester().getId().equals(loggedInUser.getId());

        if (isOwner || isAdmin) {
            return optionalSession;
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<SessionResponseDTO> findSessionsByProjectForUser(Long projectId, Principal principal) {
        User loggedInUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        boolean isAdmin = Role.ADMIN.equals(loggedInUser.getRole());
        List<Session> sessions;
        if (isAdmin) {
            sessions = sessionRepository.findByProjetoId(projectId);
        } else {
            sessions = sessionRepository.findByProjetoIdAndTester(projectId, loggedInUser);
        }

        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Session createSession(SessionCreateDTO sessionCreateDTO, String email) {
        User tester = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Projeto projeto = projetoRepository.findById(sessionCreateDTO.projectId()).orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Session session = new Session();
        session.setTester(tester);
        session.setProjeto(projeto);
        session.setDescription(sessionCreateDTO.description());
        session.setDuration(sessionCreateDTO.duration());
        session.setStatus(Status.CREATED);
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public Session startSession(Long sessionId, String email) throws AccessDeniedException {
        Session session = findSessionById_User(sessionId, email);

        if (session.getStatus() != Status.CREATED) {
            throw new IllegalStateException("A sessão não pode ser inicializada");
        }
        session.setStatus(Status.IN_EXECUTION);
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public Session finalizeSession(Long sessionId, String email) throws AccessDeniedException {
        Session session = findSessionById_User(sessionId, email);

        if (session.getStatus() != Status.IN_EXECUTION) {
            throw new IllegalStateException("A sessão não pode ser finalizada!");
        }
        session.setStatus(Status.FINALIZED);
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }


    private Session findSessionById_User(Long sessionId, String loggedInUserEmail) throws AccessDeniedException {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada."));

        User loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado."));

        boolean isAdmin = Role.ADMIN.equals(loggedInUser.getRole());
        boolean isOwner = session.getTester().getId().equals(loggedInUser.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para modificar esta sessão.");
        }

        return session;
    }

}