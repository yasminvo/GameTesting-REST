package br.ufscar.dc.dsw.GameTesting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SessionUpdateDTO {
    private Long strategyId;
    private Integer duration;
    private String description;

}