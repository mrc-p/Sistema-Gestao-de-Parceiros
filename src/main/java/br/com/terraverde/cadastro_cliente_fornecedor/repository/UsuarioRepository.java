package br.com.terraverde.cadastro_cliente_fornecedor.repository;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de CRUD na Entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo nome de usuário.
     */
    Optional<Usuario> findByUsername(String username);
}
