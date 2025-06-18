package br.ufscar.dc.dsw.GameTesting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import para evitar loop de serialização

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String altText;

    public Image() {
    }

    public Image(String filePath, String altText) {
        this.filePath = filePath;
        this.altText = altText;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}