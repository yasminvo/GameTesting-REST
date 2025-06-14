package br.ufscar.dc.dsw.GameTesting.dtos;


import br.ufscar.dc.dsw.GameTesting.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;

    public static UserDTO fromEntity(br.ufscar.dc.dsw.GameTesting.model.User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public br.ufscar.dc.dsw.GameTesting.model.User toEntity() {
        br.ufscar.dc.dsw.GameTesting.model.User user = new br.ufscar.dc.dsw.GameTesting.model.User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setRole(this.role);
        return user;
    }

}
