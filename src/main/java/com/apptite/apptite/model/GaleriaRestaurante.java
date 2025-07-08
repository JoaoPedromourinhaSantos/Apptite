package com.apptite.apptite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "galeria_restaurante") // Nome da tabela atualizado
public class GaleriaRestaurante { // Nome da classe atualizado

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false) // Coluna de junção atualizada
    @JsonBackReference // Evita loops de serialização ao retornar a entidade Restaurante
    private Restaurante restaurante; // ATUALIZADO de Estabelecimento

    @Lob
    @Column(name = "dados_imagem", columnDefinition="LONGBLOB")
    private byte[] dadosImagem;

    @Column(name = "tipo_mime_imagem")
    private String tipoMimeImagem;

    @Column(name = "nome_original_imagem")
    private String nomeOriginalImagem;

    @Column(name = "ordem") // Para ordenar as imagens na galeria
    private Integer ordem;

    public GaleriaRestaurante() {}

    // Construtor atualizado
    public GaleriaRestaurante(Restaurante restaurante, byte[] dadosImagem, String tipoMimeImagem, String nomeOriginalImagem, Integer ordem) {
        this.restaurante = restaurante;
        this.dadosImagem = dadosImagem;
        this.tipoMimeImagem = tipoMimeImagem;
        this.nomeOriginalImagem = nomeOriginalImagem;
        this.ordem = ordem;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Restaurante getRestaurante() { // ATUALIZADO
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) { // ATUALIZADO
        this.restaurante = restaurante;
    }

    public byte[] getDadosImagem() {
        return dadosImagem;
    }

    public void setDadosImagem(byte[] dadosImagem) {
        this.dadosImagem = dadosImagem;
    }

    public String getTipoMimeImagem() {
        return tipoMimeImagem;
    }

    public void setTipoMimeImagem(String tipoMimeImagem) {
        this.tipoMimeImagem = tipoMimeImagem;
    }

    public String getNomeOriginalImagem() {
        return nomeOriginalImagem;
    }

    public void setNomeOriginalImagem(String nomeOriginalImagem) {
        this.nomeOriginalImagem = nomeOriginalImagem;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}