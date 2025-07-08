package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.ClienteDTOs.*;
import com.apptite.apptite.dtos.LoginRequestDTO;
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.service.ClienteService;

import jakarta.validation.Valid; // Importar @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/cliente") 
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@Valid @RequestBody CriarClienteRequestDTO dtoCliente) { // Adicionado @Valid
        try {
            Cliente novoCliente = clienteService.cadastrarCliente(dtoCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponseDTO.fromEntity(novoCliente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dtoLogin) { // Adicionado @Valid
        Optional<Cliente> clienteOpt = clienteService.login(dtoLogin);
        if (clienteOpt.isPresent()) {
            return ResponseEntity.ok(ClienteResponseDTO.fromEntity(clienteOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editarPerfil(@PathVariable Integer id,
                                          @Valid @ModelAttribute EditarClienteRequestDTO dtoCliente, // Adicionado @Valid
                                          @RequestParam(value = "file", required = false) MultipartFile imagem) {
        try {
            Cliente clienteAtualizado = clienteService.editarPerfil(id, dtoCliente, imagem);
            return ResponseEntity.ok(ClienteResponseDTO.fromEntity(clienteAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getCliente(@PathVariable Integer id) {
        return clienteService.getClientePorId(id)
                .map(cliente -> ResponseEntity.ok(ClienteResponseDTO.fromEntity(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConta(@PathVariable Integer id) {
        try {
            clienteService.excluirCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Exception Handler para MethodArgumentNotValidException (erros de validação do @Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
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