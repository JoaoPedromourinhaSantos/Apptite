package com.apptite.apptite.repository; // Certifique-se que o pacote está correto

import com.apptite.apptite.model.ResetSenha; // Referência atualizada
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetSenhaRepository extends JpaRepository<ResetSenha, Long> { // Referência atualizada

    Optional<ResetSenha> findByToken(String token);

    void deleteByCliente(Cliente cliente);
    void deleteByRestaurante(Restaurante restaurante);
}