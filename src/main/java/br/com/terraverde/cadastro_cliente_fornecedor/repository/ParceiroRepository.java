package br.com.terraverde.cadastro_cliente_fornecedor.repository;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Parceiro; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParceiroRepository extends JpaRepository<Parceiro, Long> { 

    // Métodos existentes para validação de unicidade
    boolean existsByDocumentoIgnoreCaseOrEmailIgnoreCase(String documento, String email);
    boolean existsByDocumentoIgnoreCaseAndIdIsNotOrEmailIgnoreCaseAndIdIsNot(String documento, Long idDocumento, String email, Long idEmail);

    // Métodos existentes para listagem e dashboard
    List<Parceiro> findByTipoIgnoreCase(String tipo); 
    long countByTipo(String tipo);
    
    // --- NOVOS MÉTODOS PARA O RELATÓRIO ---
    
    /**
     * Busca os parceiros mais recentes (Top 10), ordenados pela data de cadastro descendente.
     */
    List<Parceiro> findTop10ByOrderByDataHoraCadastroDesc(); 
    
    /**
     * Busca parceiros cadastrados a partir de uma data específica (ex: último mês).
     */
    List<Parceiro> findByDataHoraCadastroAfter(LocalDateTime data);
}