package com.apptite.apptite.service;

import com.apptite.apptite.dtos.ResetSenhaDTOs.*;

import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.model.ResetSenha;
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.repository.RestauranteRepository;
import com.apptite.apptite.repository.ResetSenhaRepository;
import com.apptite.apptite.repository.ClienteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetSenhaService {

    private final ResetSenhaRepository resetSenhaRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public ResetSenhaService(ResetSenhaRepository resetSenhaRepository,
                           ClienteRepository clienteRepository,
                           RestauranteRepository restauranteRepository,
                           MailService mailService,
                           PasswordEncoder passwordEncoder) {
        this.resetSenhaRepository = resetSenhaRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void solicitarReset(SolicitarResetSenhaRequestDTO requestDTO) {
        String email = requestDTO.email();
        String tipoConta = requestDTO.tipoConta();
        String tokenValue = UUID.randomUUID().toString();

        ResetSenha novoReset = new ResetSenha(); // Entidade atualizada
        novoReset.setToken(tokenValue);
        novoReset.setExpiration(LocalDateTime.now().plusHours(1));

        String nomeEntidadeParaEmail = "";

        if ("cliente".equalsIgnoreCase(tipoConta)) {
            Optional<Cliente> optEntidade = clienteRepository.findByEmailCliente(email);
            if (optEntidade.isPresent()) {
                novoReset.setCliente(optEntidade.get());
                nomeEntidadeParaEmail = optEntidade.get().getNomeCliente();
            } else {
                return; // Não informa ao usuário, apenas não envia o token/email
            }
        } else if ("restaurante".equalsIgnoreCase(tipoConta)) {
            Optional<Restaurante> optEntidade = restauranteRepository.findByEmailRestaurante(email);
            if (optEntidade.isPresent()) {
                novoReset.setRestaurante(optEntidade.get());
                nomeEntidadeParaEmail = optEntidade.get().getNomeRestaurante();
            } else {
                return; // Não informa ao usuário
            }
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + tipoConta);
        }

        resetSenhaRepository.save(novoReset);

        String mensagem = "Olá " + nomeEntidadeParaEmail + ",\n\nSeu token para redefinir a senha é: " + tokenValue + "\n\nUse este token na página de redefinição de senha.\n\nSe não foi você, ignore este email.";
        mailService.enviarEmail(email, "Redefinição de Senha", mensagem);
    }

    @Transactional
    public boolean confirmarResetSenha(ConfirmarResetSenhaRequestDTO requestDTO) {
        if (!requestDTO.novaSenha().equals(requestDTO.confirmarNovaSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        Optional<ResetSenha> optResetSenha = resetSenhaRepository.findByToken(requestDTO.token());

        if (optResetSenha.isEmpty() || optResetSenha.get().getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token inválido ou expirado.");
        }

        ResetSenha resetSenha = optResetSenha.get();
        String senhaHasheada = passwordEncoder.encode(requestDTO.novaSenha());

        boolean sucesso = false;
        if (resetSenha.getCliente() != null) {
            Cliente cliente = resetSenha.getCliente();
            cliente.setSenhaCliente(senhaHasheada);
            clienteRepository.save(cliente);
            sucesso = true;
        } else if (resetSenha.getRestaurante() != null) {
            Restaurante restaurante = resetSenha.getRestaurante();
            restaurante.setSenhaRestaurante(senhaHasheada);
            restauranteRepository.save(restaurante);
            sucesso = true;
        } else {
            throw new RuntimeException("Token não associado a um cliente ou restaurante válido.");
        }

        if (sucesso) {
            resetSenhaRepository.delete(resetSenha);
        }
        return sucesso;
    }

    public Optional<String> validarToken(String tokenValue) {
        Optional<ResetSenha> optToken = resetSenhaRepository.findByToken(tokenValue);
        if (optToken.isPresent() && optToken.get().getExpiration().isAfter(LocalDateTime.now())) {
            if (optToken.get().getCliente() != null) {
                return Optional.of("cliente");
            } else if (optToken.get().getRestaurante() != null) {
                return Optional.of("restaurante");
            }
        }
        return Optional.empty();
    }
}