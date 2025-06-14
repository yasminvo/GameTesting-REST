package br.ufscar.dc.dsw.GameTesting.model;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    // Relacionamento OneToMany com Session (são criadas por)
//    @OneToMany(mappedBy = "tester", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Session> createdSessions;

    // Relacionamento ManyToMany com Projeto (membros)
    @ManyToMany(mappedBy = "members")
    @JsonBackReference(value = "projeto-members")
    private List<Projeto> projects;

    public User() {
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

//    public List<Session> getCreatedSessions() {
//        return createdSessions;
//    }
//
//    public void setCreatedSessions(List<Session> createdSessions) {
//        this.createdSessions = createdSessions;
//    }

    public List<Projeto> getProjects() {
        return projects;
    }

    public void setProjects(List<Projeto> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}