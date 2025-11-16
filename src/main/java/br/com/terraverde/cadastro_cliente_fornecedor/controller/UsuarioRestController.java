package br.com.terraverde.cadastro_cliente_fornecedor.controller;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Usuario;
import br.com.terraverde.cadastro_cliente_fornecedor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // IMPORTANTE
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError; 
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;


/**
 * Controlador REST para Gerenciamento de Usuários (Registro).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST /api/usuarios/register - Permite o registro de novos usuários
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registrar(@Valid @RequestBody Usuario novoUsuario) {
        
        // 1. Checagem rápida de unicidade (dá feedback rápido)
        if (repository.findByUsername(novoUsuario.getUsername()).isPresent()) {
            // Retorna 409 Conflict se o usuário já existir
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nome de usuário já existe.");
        }
        
        try {
            // Criptografa a senha antes de salvar
            novoUsuario.setPassword(passwordEncoder.encode(novoUsuario.getPassword()));
            repository.save(novoUsuario);
            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            // 2. TRATAMENTO ESSENCIAL: Captura falha de integridade do DB (se a checagem acima falhar em concorrência)
            // ESTE BLOCO TRANSFORMA O 500 NO 409.
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Violação de unicidade: Nome de usuário já está em uso.");
        }
    }
    
    /**
     * TRATAMENTO DE ERRO 400: Intercepta erros de @Valid e retorna um JSON detalhado.
     */
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