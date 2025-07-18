package br.ufscar.dc.dsw.GameTesting.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import para serialização JSON

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "strategy")
public class Strategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Example> examples = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "strategy_tips", joinColumns = @JoinColumn(name = "strategy_id"))
    @Column(name = "tip")
    private List<String> tips = new ArrayList<>();

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Session> sessions = new ArrayList<>();

    public Strategy() {
    }

    public Strategy(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Example> getExamples() { return examples; }
    public void setExamples(List<Example> examples) { this.examples = examples; }
    public List<String> getTips() { return tips; }
    public void setTips(List<String> tips) { this.tips = tips; }
    public List<Session> getSession() { return sessions; }
    public void setSession(List<Session> sessions) { this.sessions = sessions; }

    @Override
    public String toString() {
        return "Strategy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}