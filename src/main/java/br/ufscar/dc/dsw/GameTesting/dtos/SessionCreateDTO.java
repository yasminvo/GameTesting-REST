package br.ufscar.dc.dsw.GameTesting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateDTO{
    private Long projectId;
    //        Long strategyId,
    private Integer duration;
    private String description;
    private String testerEmail;
}