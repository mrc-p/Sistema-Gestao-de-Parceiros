package br.com.terraverde.cadastro_cliente_fornecedor.controller;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Parceiro;
import br.com.terraverde.cadastro_cliente_fornecedor.repository.ParceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; 
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/parceiros")
public class ParceiroRestController {

    @Autowired
    private ParceiroRepository repository;

    // POST /api/parceiros - Cria um novo Parceiro (Cliente ou Fornecedor)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> criar(@Valid @RequestBody Parceiro parceiro) { 
        parceiro.setTipo(parceiro.getTipo().toUpperCase()); 
        
        // Pré-verificação de unicidade
        if (repository.existsByDocumentoIgnoreCaseOrEmailIgnoreCase(parceiro.getDocumento(), parceiro.getEmail())) {
            String errorMessage = "Violação de unicidade: Email e/ou Documento já cadastrado(s).";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(parceiro));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Violação de unicidade: Email e/ou Documento já cadastrado(s).";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }
    }

    // PUT /api/parceiros/{id} - Atualiza um Parceiro existente
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Parceiro parceiroDetalhes) { 
        Optional<Parceiro> parceiroOptional = repository.findById(id);
        
        if (parceiroOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Pré-verificação de unicidade para UPDATE
        if (repository.existsByDocumentoIgnoreCaseAndIdIsNotOrEmailIgnoreCaseAndIdIsNot(
            parceiroDetalhes.getDocumento(), id, 
            parceiroDetalhes.getEmail(), id)) {
            
            String errorMessage = "Violação de unicidade: Email e/ou Documento pertencem a outro parceiro.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }
        
    
        Parceiro parceiroExistente = parceiroOptional.get();
        
        parceiroExistente.setNomeOuRazaoSocial(parceiroDetalhes.getNomeOuRazaoSocial());
        parceiroExistente.setDocumento(parceiroDetalhes.getDocumento());
        parceiroExistente.setTipo(parceiroDetalhes.getTipo().toUpperCase());
        parceiroExistente.setEmail(parceiroDetalhes.getEmail());
        parceiroExistente.setTelefone(parceiroDetalhes.getTelefone());
        parceiroExistente.setObservacoes(parceiroDetalhes.getObservacoes());
        
        try {
             // Salvar a entidade existente (com a data intacta)
             return ResponseEntity.ok(repository.save(parceiroExistente));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Violação de unicidade: Email e/ou Documento pertencem a outro parceiro.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }
    }
    
    // =========================================================================
    // TRATAMENTO GLOBAL DE ERROS DE VALIDAÇÃO (400 Bad Request)
    // =========================================================================
    
    /**
     * Intercepta a exceção MethodArgumentNotValidException
     * e retorna um mapa legível de erros, com status 400 Bad Request.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Percorre a lista de erros de validação
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    
    // GET /api/parceiros/clientes OU /api/parceiros/fornecedores
    @GetMapping("/{tipoPlural}")
    public List<Parceiro> listarPorTipo(@PathVariable String tipoPlural) {
        String tipoBusca; 
        if ("clientes".equalsIgnoreCase(tipoPlural)) {
            tipoBusca = "CLIENTE";
        } else if ("fornecedores".equalsIgnoreCase(tipoPlural)) {
            tipoBusca = "FORNECEDOR";
        } else {
            return List.of(); 
        }
        return repository.findByTipoIgnoreCase(tipoBusca);
    }
    
    @GetMapping("/id/{id}")
    public ResponseEntity<Parceiro> buscarPorId(@PathVariable Long id) { 
        Optional<Parceiro> parceiro = repository.findById(id);
        return parceiro.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
    
    @GetMapping("/relatorio")
    public Map<String, Long> getDashboardCounts() {
        long clientes = repository.countByTipo("CLIENTE"); 
        long fornecedores = repository.countByTipo("FORNECEDOR");
        long total = clientes + fornecedores;

        Map<String, Long> counts = new HashMap<>();
        counts.put("clientes", clientes);
        counts.put("fornecedores", fornecedores);
        counts.put("total", total);

        return counts;
    }
}