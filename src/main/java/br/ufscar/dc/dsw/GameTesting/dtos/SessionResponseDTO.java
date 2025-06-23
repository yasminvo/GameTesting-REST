package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDTO{
    private Long id;
    private UserDTO tester;
    private ProjetoDTO projeto;
    private StrategyResponseDTO strategy;
    private Integer duration;
    private String description;
    private Status status;
    private List<LocalDateTime> statusChangedTime;
    private LocalDateTime startTime;

    public static SessionResponseDTO fromEntity(Session session) {
        return new SessionResponseDTO(
                session.getId(),
                UserDTO.fromEntity(session.getTester()),
                ProjetoDTO.fromEntity(session.getProjeto()),
                session.getStrategy() != null ? StrategyResponseDTO.fromEntity(session.getStrategy()) : null,
                session.getDuration(),
                session.getDescription(),
                session.getStatus(),
                session.getStatusChangedTime(),
                session.getStartTime()
        );
    }


}