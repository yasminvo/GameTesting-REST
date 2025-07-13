package br.ufscar.dc.dsw.GameTesting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private List<UserDTO> members = new ArrayList<>();
//    private List<Long> memberIds = new ArrayList<>();


//    private List<SessionResponseDTO> sessions;

    public static ProjetoDTO fromEntity(br.ufscar.dc.dsw.GameTesting.model.Projeto projeto) {
        ProjetoDTO dto = new ProjetoDTO();
        dto.setId(projeto.getId());
        dto.setName(projeto.getName());
        dto.setDescription(projeto.getDescription());
        dto.setCreationDate(projeto.getCreationDate());
        if (projeto.getMembers() != null) {
            dto.setMembers(projeto.getMembers().stream()
                    .map(UserDTO::fromEntity)
                    .toList());
        }
//        if (projeto.getSessions() != null) {
//            dto.setSessions(projeto.getSessions().stream()
//                    .map(SessionResponseDTO::fromEntity)
//                    .toList());
//        }
        return dto;
    }

    public br.ufscar.dc.dsw.GameTesting.model.Projeto toEntity() {
        br.ufscar.dc.dsw.GameTesting.model.Projeto projeto = new br.ufscar.dc.dsw.GameTesting.model.Projeto();
        projeto.setId(this.id);
        projeto.setName(this.name);
        projeto.setDescription(this.description);
        projeto.setCreationDate(this.creationDate != null ? this.creationDate : java.time.LocalDateTime.now());
        if (this.members != null) {
            projeto.setMembers(this.members.stream()
                    .map(UserDTO::toEntity)
                    .toList());
        }
//        if (this.sessions != null) {
//            projeto.setSessions(this.sessions.stream()
//                    .map(SessionResponseDTO::toEntity)
//                    .toList());
//        }

        return projeto;
    }
}
