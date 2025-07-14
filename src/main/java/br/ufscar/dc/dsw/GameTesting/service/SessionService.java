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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ProjetoRepository projetoRepository;
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;
    private final BugRepository bugRepository;
    private final MessageSource messageSource;

    public SessionService(SessionRepository sessionRepository,
                          UserRepository userRepository,
                          ProjetoRepository projetoRepository,
                          StrategyRepository strategyRepository,
                          BugRepository bugRepository,
                          MessageSource messageSource) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.projetoRepository = projetoRepository;
        this.strategyRepository = strategyRepository;
        this.bugRepository = bugRepository;
        this.messageSource = messageSource;
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
        } else {
            String email = authentication.getName();
            User tester = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("user.notfound", null, Locale.getDefault())));
            List<Session> sessions = sessionRepository.findByTester(tester);

            return sessions.stream()
                    .map(SessionResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<SessionResponseDTO> findSessionsByProjectId(Long projectId, Locale locale) {
        projetoRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("projeto.notfound", null, locale)));

        List<Session> sessions = sessionRepository.findByProjetoId(projectId);

        return sessions.stream()
                .map(SessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Session createSession(SessionCreateDTO sessionCreateDTO, Locale locale) {
        User tester = userRepository.findByEmail(sessionCreateDTO.getTesterEmail())
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("user.notfound", null, locale)));
        Projeto projeto = projetoRepository.findById(sessionCreateDTO.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("projeto.notfound", null, locale)));
        Strategy strategy = strategyRepository.findById(sessionCreateDTO.getStrategyId())
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("strategy.notfound", null, locale)));

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

    public Session startSession(Long sessionId, Locale locale) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(messageSource.getMessage("session.notexists", null, locale)));

        if (session.getStatus() != Status.CREATED) {
            throw new IllegalStateException(messageSource.getMessage("session.cannot_start", null, locale));
        }
        session.setStatus(Status.IN_EXECUTION);
        session.setStartTime(LocalDateTime.now());
        session.getStatusChangedTime().add(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public Session finalizeSession(Long sessionId, Locale locale) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(messageSource.getMessage("session.notexists", null, locale)));

        if (session.getStatus() != Status.IN_EXECUTION) {
            throw new IllegalStateException(messageSource.getMessage("session.cannot_finalize", null, locale));
        }
        session.setStatus(Status.FINALIZED);
        session.getStatusChangedTime().add(LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTimeExpected = session.getStartTime().plusMinutes(session.getDuration());

        if (now.isBefore(endTimeExpected)) {
            throw new IllegalStateException(
                    messageSource.getMessage("session.too_early_to_finalize", null, locale));
        }

        return sessionRepository.save(session);
    }

    public SessionResponseDTO findSessionById(Long sessionId, Locale locale) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("session.notfound", null, locale)));

        return SessionResponseDTO.fromEntity(session);
    }

    public SessionResponseDTO updateSession(Long sessionId, SessionUpdateDTO sessionUpdateDTO, Principal principal, Locale locale) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("session.notfound", null, locale)));

        if (session.getStatus() == Status.FINALIZED) {
            if (principal == null) {
                throw new AppException(messageSource.getMessage("user.must_authenticate", null, locale), HttpStatus.FORBIDDEN);
            }

            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("user.notfound", null, locale)));

            if (!currentUser.getRole().equals(Role.ADMIN)) {
                throw new AppException(messageSource.getMessage("user.must_be_admin", null, locale), HttpStatus.FORBIDDEN);
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
                    .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("strategy.notfound", null, locale)));

            session.setStrategy(newStrategy);
        }
        Session updatedSession = sessionRepository.save(session);

        return SessionResponseDTO.fromEntity(updatedSession);
    }

    public void deleteSession(Long id, Locale locale) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("session.notfound_with_id", new Object[]{id}, locale)));

        if (session.getStatus() == Status.IN_EXECUTION) {
            throw new IllegalStateException(messageSource.getMessage("session.not_in_execution", null, locale));
        }

        sessionRepository.delete(session);
    }

    public BugDTO reportBug(Long sessionId, BugDTO bugDto, Locale locale) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("session.notfound_with_id", new Object[]{sessionId}, locale)));

        if (session.getStatus() != Status.IN_EXECUTION) {
            throw new IllegalStateException(messageSource.getMessage("session.not_in_execution", null, locale));
        }

        Bug bug = new Bug();
        bug.setTitulo(bugDto.getTitulo());
        bug.setDescricao(bugDto.getDescricao());
        bug.setSession(session);
        Bug savedBug = bugRepository.save(bug);

        return BugDTO.fromEntity(savedBug);
    }

    @Transactional(readOnly = true)
    public List<BugDTO> findBugsBySession(Long sessionID, Locale locale) {
        if (!sessionRepository.existsById(sessionID)) {
            throw new EntityNotFoundException(messageSource.getMessage("session.notfound_with_id", new Object[]{sessionID}, locale));
        }

        List<Bug> bugs = bugRepository.findBySessionId(sessionID);
        return bugs.stream()
                .map(BugDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
