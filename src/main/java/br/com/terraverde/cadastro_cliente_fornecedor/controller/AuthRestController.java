package br.com.terraverde.cadastro_cliente_fornecedor.controller;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Usuario;
import br.com.terraverde.cadastro_cliente_fornecedor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para lidar com a autenticação (login e registro inicial).
 * Usamos a API /api/auth.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Classe DTO (Data Transfer Object) para receber as credenciais de login
    record LoginRequest(String username, String password) {}
    
    // Classe DTO para receber as credenciais de registro
    record RegisterRequest(String username, String password) {}

    /**
     * Endpoint de registro (criar novo usuário).
     * @param request DTO com username e password.
     * @return 201 Created ou 409 Conflict se usuário já existir.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            // Conflito: usuário já existe
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(request.username());
        novoUsuario.setPassword(passwordEncoder.encode(request.password()));
        
        usuarioRepository.save(novoUsuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /**
     * Endpoint de login.
     * @param request Um mapa contendo "username" e "password".
     * @return 200 OK se autenticado, 401 Unauthorized caso contrário.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return usuarioRepository.findByUsername(request.username())
            .filter(usuario -> passwordEncoder.matches(request.password(), usuario.getPassword()))
            .map(usuario -> ResponseEntity.ok().body("Login successful"))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas"));
    }
    /**
     * Endpoint para criar um usuário inicial se não houver nenhum.
     * Usuario 'admin' adicionado para testar o acesso
     * @return 201 Created ou 200 OK se o usuário já existe.
     */
    @PostMapping("/setup")
    public ResponseEntity<String> setupInitialUser() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            // A senha 'admin123' será hasheada
            admin.setPassword(passwordEncoder.encode("admin123"));
            usuarioRepository.save(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body("Initial user 'admin' created with password 'admin123'.");
        }
        return ResponseEntity.ok("Initial user already set up.");
    }
}
    