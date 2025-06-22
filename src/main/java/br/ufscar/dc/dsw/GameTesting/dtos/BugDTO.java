package br.ufscar.dc.dsw.GameTesting.dtos;

import br.ufscar.dc.dsw.GameTesting.model.Bug;

public class BugDTO {
    private Long id;
    private Long sessionId;

    private String titulo;
    private String descricao;

    public BugDTO(){}
    public BugDTO(Long id, String titulo, String descricao, Long sessionId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.sessionId = sessionId;
    }
    public static BugDTO fromEntity(Bug bug) {
        return new BugDTO(
                bug.getId(),
                bug.getTitulo(),
                bug.getDescricao(),
                bug.getSession().getId()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}