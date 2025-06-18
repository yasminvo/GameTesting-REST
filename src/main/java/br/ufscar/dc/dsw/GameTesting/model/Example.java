package br.ufscar.dc.dsw.GameTesting.model;

import jakarta.persistence.CascadeType; // Import para CascadeType
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference; // Import para serialização JSON

@Entity
@Table(name = "example")
public class Example {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id", referencedColumnName = "id", unique = true)
    private Image image;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    @JsonBackReference
    private Strategy strategy;

    public Example() {
    }

    public Example(String text, Image image, Strategy strategy) {
        this.text = text;
        this.image = image;
        this.strategy = strategy;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }

    @Override
    public String toString() {
        return "Example{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}