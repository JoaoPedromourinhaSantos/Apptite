package com.apptite.apptite.service;

import com.apptite.apptite.dtos.LoginRequestDTO;
import com.apptite.apptite.dtos.ClienteDTOs.CriarClienteRequestDTO;
import com.apptite.apptite.dtos.ClienteDTOs.EditarClienteRequestDTO;
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.repository.ClienteRepository;
import com.apptite.apptite.repository.FavoritoRepository;
import com.apptite.apptite.repository.AvaliacaoRepository;
import com.apptite.apptite.repository.ResetSenhaRepository;
import com.apptite.apptite.repository.ReservaRepository;
import com.apptite.apptite.util.UploadUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private ResetSenhaRepository mudarSenhaTokenRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetar o PasswordEncoder

    @Transactional
    public Cliente cadastrarCliente(CriarClienteRequestDTO dtoCliente) {
        if (!dtoCliente.senhaCliente().equals(dtoCliente.confirmarSenhaCliente())) {
            throw new IllegalArgumentException("As senhas não coincidem!");
        }
        if (clienteRepository.findByEmailCliente(dtoCliente.emailCliente()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        String senhaHasheada = passwordEncoder.encode(dtoCliente.senhaCliente()); // HASHEAR A SENHA
        Cliente novoCliente = new Cliente(dtoCliente.nomeCliente(), dtoCliente.emailCliente(), senhaHasheada);

        return clienteRepository.save(novoCliente);
    }

    public Optional<Cliente> login(LoginRequestDTO dtoLogin) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmailCliente(dtoLogin.email());
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            // COMPARAR SENHA FORNECIDA COM A SENHA HASHEADA ARMAZENADA
            if (passwordEncoder.matches(dtoLogin.senha(), cliente.getSenhaCliente())) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Cliente editarPerfil(Integer id, EditarClienteRequestDTO dtoCliente, MultipartFile imagem) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        if (dtoCliente.nomeCliente() != null && !dtoCliente.nomeCliente().isEmpty()) {
            cliente.setNomeCliente(dtoCliente.nomeCliente());
        }
        if (dtoCliente.emailCliente() != null && !dtoCliente.emailCliente().isEmpty()) {
            // Você pode querer adicionar uma verificação aqui para garantir que o novo email não está em uso por outro cliente
            Optional<Cliente> clientePorNovoEmail = clienteRepository.findByEmailCliente(dtoCliente.emailCliente());
            if (clientePorNovoEmail.isPresent() && !clientePorNovoEmail.get().getIdCliente().equals(id)) {
                throw new IllegalArgumentException("Este email já está em uso por outra conta.");
            }
            cliente.setEmailCliente(dtoCliente.emailCliente());
        }
        if (dtoCliente.telefoneCliente() != null && !dtoCliente.telefoneCliente().isEmpty()) {
            cliente.setTelefoneCliente(dtoCliente.telefoneCliente());
        }
        if (dtoCliente.descricaoCliente() != null) {
            cliente.setDescricaoCliente(dtoCliente.descricaoCliente());
        }

        // Se uma nova senha foi fornecida e não está vazia
        if (dtoCliente.senhaCliente() != null && !dtoCliente.senhaCliente().isEmpty()) {
            if (dtoCliente.confirmarSenhaCliente() == null || !dtoCliente.senhaCliente().equals(dtoCliente.confirmarSenhaCliente())) {
                throw new IllegalArgumentException("As senhas fornecidas para atualização não coincidem!");
            }
            String novaSenhaHasheada = passwordEncoder.encode(dtoCliente.senhaCliente()); // HASHEAR A NOVA SENHA
            cliente.setSenhaCliente(novaSenhaHasheada);
        }

        if (imagem != null && !imagem.isEmpty()) {
            String caminhoImagem = UploadUtil.fazerUploadImagem(imagem);
            cliente.setFotoCliente(caminhoImagem);
        }

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> getClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public void excluirCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        mudarSenhaTokenRepository.deleteByCliente(cliente);
        avaliacaoRepository.deleteByCliente(cliente);
        favoritoRepository.deleteByCliente(cliente);
        reservaRepository.deleteByCliente(cliente);

        clienteRepository.delete(cliente);
    }
}