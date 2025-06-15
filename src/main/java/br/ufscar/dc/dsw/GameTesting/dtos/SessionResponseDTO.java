package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.model.Session;

import java.time.LocalDateTime;
import java.util.List;

public record SessionResponseDTO(
        Long id,
        UserDTO tester,
        ProjetoDTO projeto,
        //StrategyDTO strategy,
        Integer duration,
        String description,
        Status status,
        List<LocalDateTime> statusChangedTime
) {

    public static SessionResponseDTO fromEntity(Session session) {
        return new SessionResponseDTO(
                session.getId(),
                UserDTO.fromEntity(session.getTester()),
                ProjetoDTO.fromEntity(session.getProjeto()),
                session.getDuration(),
                session.getDescription(),
                session.getStatus(),
                session.getStatusChangedTime()
        );
    }


}