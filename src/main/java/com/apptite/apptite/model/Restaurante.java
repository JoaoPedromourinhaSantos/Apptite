package com.apptite.apptite.model;

// ... imports ...
import com.fasterxml.jackson.annotation.JsonManagedReference; // Adicione se não estiver
import jakarta.persistence.*; // Adicione se não estiver
import java.util.ArrayList; // Adicione se não estiver
import java.util.HashSet;
import java.util.List; // Adicione se não estiver
import java.util.Set;


@Entity
@Table(name = "Restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRestaurante", unique = true)
    private Integer idRestaurante; // Mantendo o nome do campo como na sua entidade Cliente

    @Column(name = "cnpj", length = 20, unique = true)
    private String cnpj;

    @Column(name = "nomeRestaurante", length = 50) // Campo com sufixo
    private String nomeRestaurante;

    @Column(name = "emailRestaurante", length = 50, unique = true) // Campo com sufixo
    private String emailRestaurante;

    @Column(name = "senhaRestaurante", length = 255) // Campo com sufixo, aumentado para hash
    private String senhaRestaurante;

    @Column(name = "telefoneRestaurante", length = 20, unique = true) // Campo com sufixo
    private String telefoneRestaurante;

    @Column(name = "descricaoRestaurante", length = 255) // Campo com sufixo
    private String descricaoRestaurante;

    @Column(name = "faixaPreco")
    private String faixaPreco;

    @Lob // Informa ao JPA para tratar como um objeto grande (BLOB/LONGBLOB no banco)
    @Column(name = "fotoCardapio")
    private byte[] fotoCardapio; // Mudamos de String para byte[]

    @Column(name = "fotoCardapioMimeType") // Novo campo para o tipo da imagem
    private String fotoCardapioMimeType;

    @Column(name = "avaliacaoGeral")
    private float avaliacaoGeral;

    @Column(name = "ruaEndereco", length = 50) // Mantendo como no original Estabelecimento
    private String ruaEndereco;

    @Column(name = "bairroEndereco", length = 50) // Mantendo como no original Estabelecimento
    private String bairroEndereco;

    @Column(name = "numeroEndereco", length = 50) // Mantendo como no original Estabelecimento
    private String numeroEndereco;

    @Column(name = "cidadeEndereco", length = 50) // Mantendo como no original Estabelecimento
    private String cidadeEndereco;

    @Column(name = "estadoEndereco", length = 50) // Mantendo como no original Estabelecimento
    private String estadoEndereco;
    

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<GaleriaRestaurante> imagensGaleria = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
    name = "restaurante_filtros",
    joinColumns = @JoinColumn(name = "restaurante_id"),
    inverseJoinColumns = @JoinColumn(name = "filtro_id")
    )
    private Set<Filtro> filtros = new HashSet<>();

    public Restaurante() {}

    // Construtor principal para o serviço
    public Restaurante(String nomeRestaurante, String cnpj, String emailRestaurante, String senhaRestaurante, String ruaEndereco, String numeroEndereco, String bairroEndereco, String cidadeEndereco, String estadoEndereco) {
        this.nomeRestaurante = nomeRestaurante;
        this.cnpj = cnpj;
        this.emailRestaurante = emailRestaurante;
        this.senhaRestaurante = senhaRestaurante; // Será hasheada pelo serviço
        this.ruaEndereco = ruaEndereco;
        this.numeroEndereco = numeroEndereco;
        this.bairroEndereco = bairroEndereco;
        this.cidadeEndereco = cidadeEndereco;
        this.estadoEndereco = estadoEndereco;
    }

    // Getters e Setters
    public Integer getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Integer idRestaurante) { this.idRestaurante = idRestaurante; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNomeRestaurante() 
        { return nomeRestaurante;       
    }
    public void setNomeRestaurante(String nomeRestaurante) 
        { this.nomeRestaurante = nomeRestaurante; 
    }

    public String getEmailRestaurante() 
        { return emailRestaurante; 
    }
    public void setEmailRestaurante(String emailRestaurante)   
        { this.emailRestaurante = emailRestaurante; 
    }

    public String getSenhaRestaurante() 
        { return senhaRestaurante; 
    }
    public void setSenhaRestaurante(String senhaRestaurante) 
        { this.senhaRestaurante = senhaRestaurante; 
    }

    public String getTelefoneRestaurante() 
        { return telefoneRestaurante; 
    }
    public void setTelefoneRestaurante(String telefoneRestaurante) 
        { this.telefoneRestaurante = telefoneRestaurante; 
    }

    public String getDescricaoRestaurante() 
        { return descricaoRestaurante; 
    }
    public void setDescricaoRestaurante(String descricaoRestaurante) 
        { this.descricaoRestaurante = descricaoRestaurante; 
    }

    public String getFaixaPreco() 
        { return faixaPreco; 
    }
    public void setFaixaPreco(String faixaPreco) 
        { this.faixaPreco = faixaPreco; 
    }

    public byte[] getFotoCardapio() {
        return fotoCardapio;
    }

    public void setFotoCardapio(byte[] fotoCardapio) {
        this.fotoCardapio = fotoCardapio;
    }

    public String getFotoCardapioMimeType() {
        return fotoCardapioMimeType;
    }

    public void setFotoCardapioMimeType(String fotoCardapioMimeType) {
        this.fotoCardapioMimeType = fotoCardapioMimeType;
    }

    public float getAvaliacaoGeral() 
        { return avaliacaoGeral; 
    }
    public void setAvaliacaoGeral(float avaliacaoGeral) 
        { this.avaliacaoGeral = avaliacaoGeral; 
    }

    public String getRuaEndereco() 
        { return ruaEndereco; 
    }
    public void setRuaEndereco(String ruaEndereco) 
        { this.ruaEndereco = ruaEndereco; 
    }

    public String getBairroEndereco()
        { return bairroEndereco; 
    }
    public void setBairroEndereco(String bairroEndereco) 
        { this.bairroEndereco = bairroEndereco; 
    }

    public String getNumeroEndereco() 
        { return numeroEndereco; 
    }
    public void setNumeroEndereco(String numeroEndereco) 
        { this.numeroEndereco = numeroEndereco; 
    }

    public String getCidadeEndereco()
        { return cidadeEndereco; 
    }
    public void setCidadeEndereco(String cidadeEndereco) 
        { this.cidadeEndereco = cidadeEndereco; 
    }

    public String getEstadoEndereco() 
        { return estadoEndereco; 
    }
    public void setEstadoEndereco(String estadoEndereco) 
        { this.estadoEndereco = estadoEndereco; 
    }

    public List<GaleriaRestaurante> getImagensGaleria() {
        return imagensGaleria;
    }

    public void setImagensGaleria(List<GaleriaRestaurante> imagensGaleria) {
        this.imagensGaleria.clear();
        if (imagensGaleria != null) {
            this.imagensGaleria.addAll(imagensGaleria);
        }
    }

    public void addImagemGaleria(GaleriaRestaurante imagem) {
        this.imagensGaleria.add(imagem);
        imagem.setRestaurante(this);
    }

    public void removeImagemGaleria(GaleriaRestaurante imagem) {
        this.imagensGaleria.remove(imagem);
        imagem.setRestaurante(null);
    }

    public Set<Filtro> getFiltros() {
    return filtros;
    }

    public void setFiltros(Set<Filtro> filtros) {
        this.filtros = filtros;
    }
}