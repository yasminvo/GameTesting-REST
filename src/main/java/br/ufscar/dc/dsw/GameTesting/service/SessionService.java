package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.BugDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionCreateDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionResponseDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionUpdateDTO;
import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.*;
import br.ufscar.dc.dsw.GameTesting.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
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
    private final StrategyRepository strategyRepository;
    private final BugRepository bugRepository;

    public SessionService(SessionRepository sessionRepository,
                          UserRepository userRepository,
                          ProjetoRepository projetoRepository,
                          StrategyRepository strategyRepository,
                          BugRepository bugRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.projetoRepository = projetoRepository;
        this.strategyRepository = strategyRepository;
        this.bugRepository = bugRepository;
    }

    public List<SessionResponseDTO> listAll() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionResponseDTO> listByRole(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return listAll();
        }
        else {
            String email = authentication.getName();
            User tester = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            List<Session> sessions = sessionRepository.findByTester(tester);

            return sessions.stream()
                    .map(SessionResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }
    }


    @Transactional(readOnly = true)
    public List<SessionResponseDTO> findSessionsByProjectId(Long projectId) {
        projetoRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado."));

        List<Session> sessions = sessionRepository.findByProjetoId(projectId);

        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Session createSession(SessionCreateDTO sessionCreateDTO) {
        User tester = userRepository.findByEmail(sessionCreateDTO.getTesterEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Projeto projeto = projetoRepository.findById(sessionCreateDTO.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        Strategy strategy = strategyRepository.findById(sessionCreateDTO.getStrategyId())
                .orElseThrow(() -> new EntityNotFoundException("Strategy not found"));

        Session session = new Session();
        session.setTester(tester);
        session.setProjeto(projeto);
        session.setStrategy(strategy);
        session.setDescription(sessionCreateDTO.getDescription());
        session.setDuration(sessionCreateDTO.getDuration());
        session.setStatus(Status.CREATED);
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public Session startSession(Long sessionId) throws AccessDeniedException {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("A sessão não existe"));

        if (session.getStatus() != Status.CREATED) {
            throw new IllegalStateException("A sessão não pode ser inicializada");
        }
        session.setStatus(Status.IN_EXECUTION);
        session.setStartTime(LocalDateTime.now());
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public Session finalizeSession(Long sessionId) throws AccessDeniedException {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("A sessão não existe"));

        if (session.getStatus() != Status.IN_EXECUTION) {
            throw new IllegalStateException("A sessão não pode ser finalizada!");
        }
        session.setStatus(Status.FINALIZED);
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }


    public SessionResponseDTO findSessionById(Long sessionId)  {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada."));

        return SessionResponseDTO.fromEntity(session);

    }

    public SessionResponseDTO updateSession(Long sessionId, SessionUpdateDTO sessionUpdateDTO, Principal principal) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new EntityNotFoundException("sessão não encontrada"));

        if (session.getStatus() == Status.FINALIZED) {
            if (principal == null) {
                throw new AppException("Usuário precisa estar autenticado", HttpStatus.FORBIDDEN);
            }

            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

            if (!currentUser.getRole().equals(Role.ADMIN)) {
                throw new AppException("Usuário precisa ser ADMIN para editar session FINALIZED", HttpStatus.FORBIDDEN);
            }
        }

        if (sessionUpdateDTO.getDescription() != null) {
            session.setDescription(sessionUpdateDTO.getDescription());
        }

        if (sessionUpdateDTO.getDuration() != null) {
            session.setDuration(sessionUpdateDTO.getDuration());
        }

        if (sessionUpdateDTO.getStrategyId() != null) {
            Strategy newStrategy = strategyRepository.findById(sessionUpdateDTO.getStrategyId())
                    .orElseThrow(() -> new EntityNotFoundException("estratégia não encontrada"));

            session.setStrategy(newStrategy);
        }
        Session sessaoNova = sessionRepository.save(session);

        return SessionResponseDTO.fromEntity(sessaoNova);
    }

    @Scheduled(fixedRate = 50000000)
    @Transactional
    public void autofinalizarSessao() {
        List<Session> runningSessions = sessionRepository.findByStatus(Status.IN_EXECUTION);

        for (Session session : runningSessions) {

            LocalDateTime startTime = session.getStartTime();
            LocalDateTime expectedEndTime = startTime.plusMinutes(session.getDuration());

            if (LocalDateTime.now().isAfter(expectedEndTime)) {
                session.setStatus(Status.FINALIZED);
                session.getStatusChangedTime().add(LocalDateTime.now());
                sessionRepository.save(session);
            }
        }
    }

    public BugDTO reportBug(Long sessionId, BugDTO bugDto) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()->new EntityNotFoundException("Sessão de id " +  sessionId + " não encontrado"));


        if (session.getStatus() != Status.IN_EXECUTION) {
            throw new IllegalStateException("sessão não está em execução");
        }

        Bug bug = new Bug();
        bug.setTitulo(bugDto.getTitulo());
        bug.setDescricao(bugDto.getDescricao());
        bug.setSession(session);
        Bug savedBug = bugRepository.save(bug);

        return BugDTO.fromEntity(savedBug);
    }

    @Transactional(readOnly = true)
    public List<BugDTO> findBugsBySession(Long sessionID) {
        if (!sessionRepository.existsById(sessionID)) {
            throw new EntityNotFoundException("Sessão de id " + sessionID + " não encontrada.");
        }

        List<Bug> bugs = bugRepository.findBySessionId(sessionID);
        return bugs.stream()
                .map(BugDTO::fromEntity)
                .collect(Collectors.toList());
    }

}