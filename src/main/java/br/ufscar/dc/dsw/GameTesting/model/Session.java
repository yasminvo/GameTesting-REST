package br.ufscar.dc.dsw.GameTesting.model;

import br.ufscar.dc.dsw.GameTesting.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "tester", nullable = false)
    private User tester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "strategy")
    private Strategy strategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto", nullable = false)
    @JsonIgnore
    private Projeto projeto;

    @Column(name = "duration_em_minutos")
    private Integer duration;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "session_status_history", joinColumns = @JoinColumn(name = "session"))
    @Column(name = "status_changed_at")
    private List<LocalDateTime> statusChangedTime = new ArrayList<>();

    public Session() {
    }

    public Session(User tester, Strategy strategy, Projeto projeto, String description, Status initialStatus) {
        this.tester = tester;
        this.strategy = strategy;
        this.projeto = projeto;
        this.description = description;
        this.status = initialStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTester() {
        return tester;
    }

    public void setTester(User tester) {
        this.tester = tester;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<LocalDateTime> getStatusChangedTime() {
        return statusChangedTime;
    }

    public void setStatusChangedTime(List<LocalDateTime> statusChangedTime) {
        this.statusChangedTime = statusChangedTime;
    }
}