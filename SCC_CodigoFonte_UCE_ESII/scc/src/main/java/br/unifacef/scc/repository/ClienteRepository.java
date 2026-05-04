package br.unifacef.scc.repository;

import br.unifacef.scc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByAtivoTrueOrderByNomeAsc();

    /**
     * Busca por nome, sobrenome, CPF ou email.
     * Requisito da elicitacao: busca rapida pelo nome
     * (cidade pequena, todos se conhecem pelo nome).
     */
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND (" +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%',:t,'%')) OR " +
           "LOWER(c.sobrenome) LIKE LOWER(CONCAT('%',:t,'%')) OR " +
           "c.cpf LIKE CONCAT('%',:t,'%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%',:t,'%')))")
    List<Cliente> buscar(@Param("t") String termo);
}
