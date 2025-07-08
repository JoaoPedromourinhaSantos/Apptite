package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.ResetSenhaDTOs.ConfirmarResetSenhaRequestDTO;
import com.apptite.apptite.dtos.ResetSenhaDTOs.SolicitarResetSenhaRequestDTO;
import com.apptite.apptite.service.ResetSenhaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reset-senha")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
public class ResetSenhaController {

    @Autowired
    private ResetSenhaService resetSenhaService;

    @PostMapping("/solicitar-token")
    @ResponseBody
    public ResponseEntity<?> solicitarToken(@Valid @RequestBody SolicitarResetSenhaRequestDTO requestDTO) {
        try {
            resetSenhaService.solicitarReset(requestDTO); // Nome do método no service atualizado
            return ResponseEntity.ok("Se o email fornecido estiver cadastrado, um token de redefinição será enviado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Logar erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a solicitação.");
        }
    }

    @GetMapping("/formulario")
    public String mostrarFormularioReset(@RequestParam("token") String token, Model model) {
        Optional<String> tipoUsuarioOpt = resetSenhaService.validarToken(token);
        if (tipoUsuarioOpt.isPresent()) {
            model.addAttribute("token", token);
            return "resetarSenhaForm";
        } else {
            model.addAttribute("errorMessage", "Token inválido ou expirado. Solicite um novo token.");
            return "paginaErroToken";
        }
    }

    @PostMapping("/confirmar-form")
    public String confirmarResetViaForm(
            @RequestParam("token") String token,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmarNovaSenha") String confirmarNovaSenha,
            Model model) {

        if (!novaSenha.equals(confirmarNovaSenha)) {
             model.addAttribute("token", token);
             model.addAttribute("errorMessage", "As senhas não coincidem.");
             return "resetarSenhaForm";
        }
        if (novaSenha.length() < 6) {
             model.addAttribute("token", token);
             model.addAttribute("errorMessage", "A nova senha deve ter pelo menos 6 caracteres.");
             return "resetarSenhaForm";
        }

        ConfirmarResetSenhaRequestDTO requestDTO = new ConfirmarResetSenhaRequestDTO(token, novaSenha, confirmarNovaSenha);
        try {
            resetSenhaService.confirmarResetSenha(requestDTO);
            model.addAttribute("successMessage", "Sua senha foi redefinida com sucesso!");
            return "paginaSucessoReset";
        } catch (RuntimeException e) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", e.getMessage());
            return e.getMessage().contains("Token inválido ou expirado") ? "paginaErroToken" : "resetarSenhaForm";
        }
    }

    @PostMapping("/confirmar-api")
    @ResponseBody
    public ResponseEntity<?> confirmarResetViaApi(@Valid @RequestBody ConfirmarResetSenhaRequestDTO requestDTO) { // @Valid está aqui
        try {
            resetSenhaService.confirmarResetSenha(requestDTO);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (RuntimeException e) { // Pode unificar os catches se o tratamento for similar
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Exception Handler para MethodArgumentNotValidException (erros de validação do @Valid nos DTOs)
    // Este handler será acionado para os endpoints de API (@ResponseBody)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody // Garante que a resposta deste handler seja JSON
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}