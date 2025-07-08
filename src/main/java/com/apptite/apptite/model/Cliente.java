package com.apptite.apptite.model;

// Manteremos o import do CriarClienteRequestDTO para o construtor, se você decidir mantê-lo.
// import com.apptite.apptite.dtos.CriarClienteRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cliente") // Nome da tabela atualizado
public class Cliente { // Classe renomeada

    // Removido 'public static final String TABLE_NAME' pois @Table(name="Cliente") já define.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente", unique = true) // Coluna do ID atualizada
    private Integer idCliente; // Nome do campo simplificado

    @Column(name = "nome", length = 50)
    private String nomeCliente;

    @Column(name = "email", length = 50, unique = true)
    private String emailCliente;

    @Column(name = "senha", length = 255) // Aumentado para senhas hasheadas
    private String senhaCliente;

    @Column(name = "telefone", length = 20, unique = true)
    private String telefoneCliente;

    @Column(name = "descricao", length = 255)
    private String descricaoCliente;

    @Lob
    @Column(name = "foto", columnDefinition = "TEXT")
    private String fotoCliente;

    // Construtor padrão
    public Cliente() {}

    // Construtor para facilitar a criação a partir de dados crus (usado no serviço)
    public Cliente(String nome, String email, String senha) {
        this.nomeCliente = nome;
        this.emailCliente = email;
        this.senhaCliente = senha; // Lembre-se: a senha deve ser hasheada ANTES de chegar aqui.
    }

    // Getters e Setters (nomes atualizados para consistência)
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer id) { // Parâmetro 'id' em minúsculo
        this.idCliente = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nome) {
        this.nomeCliente = nome;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String email) {
        this.emailCliente = email;
    }

    public String getSenhaCliente() {
        return senhaCliente;
    }

    public void setSenhaCliente(String senha) {
        this.senhaCliente = senha;
    }

    public String getTelefoneCliente() {
        return telefoneCliente;
    }

    public void setTelefoneCliente(String telefone) {
        this.telefoneCliente = telefone;
    }

    public String getDescricaoCliente() {
        return descricaoCliente;
    }

    public void setDescricaoCliente(String descricao) {
        this.descricaoCliente = descricao;
    }

    public String getFotoCliente() {
        return fotoCliente;
    }

    public void setFotoCliente(String foto) {
        this.fotoCliente = foto;
    }
}