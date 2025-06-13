package br.ufscar.dc.dsw.GameTesting.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projeto")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    // Relacionamento ManyToMany com User (members)
    @ManyToMany
    @JoinTable(
            name = "projeto_members",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

//    // Relacionamento OneToMany com Session (possui)
//    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Session> sessions = new ArrayList<>();

    public Projeto() {
    }

    public Projeto(String name, String description, LocalDateTime creationDate) {
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

//    public List<Session> getSessions() {
//        return sessions;
//    }
//
//    public void setSessions(List<Session> sessions) {
//        this.sessions = sessions;
//    }


    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}