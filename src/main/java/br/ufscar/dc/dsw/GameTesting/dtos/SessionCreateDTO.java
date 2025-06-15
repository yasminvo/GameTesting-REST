package br.ufscar.dc.dsw.GameTesting.dtos;

public record SessionCreateDTO(
        Long projectId,
        //Long strategyId,
        Integer duration,
        String description
) {
}