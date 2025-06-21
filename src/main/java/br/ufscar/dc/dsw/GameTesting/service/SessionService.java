package br.ufscar.dc.dsw.GameTesting.service;

import br.ufscar.dc.dsw.GameTesting.dtos.SessionCreateDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionResponseDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionUpdateDTO;
import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.ProjetoRepository;
import br.ufscar.dc.dsw.GameTesting.repository.SessionRepository;
import br.ufscar.dc.dsw.GameTesting.repository.StrategyRepository;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ProjetoRepository projetoRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;

    public SessionService(SessionRepository sessionRepository,
                          UserRepository userRepository,
                          ProjetoRepository projetoRepository,
                          StrategyRepository strategyRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.projetoRepository = projetoRepository;
        this.strategyRepository = strategyRepository;
    }

    public List<SessionResponseDTO> listAll() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
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

    public SessionResponseDTO updateSession(Long sessionId, SessionUpdateDTO sessionUpdateDTO) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new EntityNotFoundException("sessão não encontrada"));

        if (session.getStatus() == Status.FINALIZED) {
            throw new IllegalStateException("sessão já finalizada");
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

}