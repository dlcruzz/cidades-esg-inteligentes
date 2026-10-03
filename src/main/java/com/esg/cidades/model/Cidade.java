package com.esg.cidades.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cidades")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da cidade é obrigatório")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "O estado (UF) é obrigatório")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotNull(message = "A população é obrigatória")
    @Positive(message = "A população deve ser um número positivo")
    private Long populacao;

    @DecimalMin(value = "0.0", message = "O indicador ambiental deve ser entre 0 e 100")
    @DecimalMax(value = "100.0", message = "O indicador ambiental deve ser entre 0 e 100")
    @Column(name = "indicador_ambiental")
    private Double indicadorAmbiental;

    @DecimalMin(value = "0.0", message = "O indicador social deve ser entre 0 e 100")
    @DecimalMax(value = "100.0", message = "O indicador social deve ser entre 0 e 100")
    @Column(name = "indicador_social")
    private Double indicadorSocial;

    @DecimalMin(value = "0.0", message = "O indicador de governança deve ser entre 0 e 100")
    @DecimalMax(value = "100.0", message = "O indicador de governança deve ser entre 0 e 100")
    @Column(name = "indicador_governanca")
    private Double indicadorGovernanca;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    private void atualizarTimestamp() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public Cidade() {
    }

    public Cidade(String nome, String uf, Long populacao, Double indicadorAmbiental,
                  Double indicadorSocial, Double indicadorGovernanca) {
        this.nome = nome;
        this.uf = uf;
        this.populacao = populacao;
        this.indicadorAmbiental = indicadorAmbiental;
        this.indicadorSocial = indicadorSocial;
        this.indicadorGovernanca = indicadorGovernanca;
    }

    /** Calcula a pontuação ESG geral como média simples dos 3 pilares. */
    @Transient
    public Double getScoreEsg() {
        if (indicadorAmbiental == null || indicadorSocial == null || indicadorGovernanca == null) {
            return null;
        }
        return Math.round(((indicadorAmbiental + indicadorSocial + indicadorGovernanca) / 3.0) * 100.0) / 100.0;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Long getPopulacao() {
        return populacao;
    }

    public void setPopulacao(Long populacao) {
        this.populacao = populacao;
    }

    public Double getIndicadorAmbiental() {
        return indicadorAmbiental;
    }

    public void setIndicadorAmbiental(Double indicadorAmbiental) {
        this.indicadorAmbiental = indicadorAmbiental;
    }

    public Double getIndicadorSocial() {
        return indicadorSocial;
    }

    public void setIndicadorSocial(Double indicadorSocial) {
        this.indicadorSocial = indicadorSocial;
    }

    public Double getIndicadorGovernanca() {
        return indicadorGovernanca;
    }

    public void setIndicadorGovernanca(Double indicadorGovernanca) {
        this.indicadorGovernanca = indicadorGovernanca;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
