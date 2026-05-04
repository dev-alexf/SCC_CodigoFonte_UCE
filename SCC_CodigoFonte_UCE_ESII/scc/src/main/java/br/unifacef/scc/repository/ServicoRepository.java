package br.unifacef.scc.repository;

import br.unifacef.scc.model.Servico;
import br.unifacef.scc.model.StatusServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByClienteIdOrderByDataDesc(Long clienteId);

    @Query("SELECT s FROM Servico s WHERE s.status = 'PENDENTE' " +
           "AND s.dataStatusAlterado < :limite ORDER BY s.dataStatusAlterado ASC")
    List<Servico> findInadimplentes(@Param("limite") LocalDateTime limite);

    @Query("SELECT s FROM Servico s WHERE " +
           "(:clienteId IS NULL OR s.cliente.id = :clienteId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:dataInicio IS NULL OR s.data >= :dataInicio) AND " +
           "(:dataFim IS NULL OR s.data <= :dataFim) " +
           "ORDER BY s.data DESC")
    List<Servico> relatorio(@Param("clienteId") Long clienteId,
                            @Param("status") StatusServico status,
                            @Param("dataInicio") LocalDate dataInicio,
                            @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(SUM(s.valor), 0) FROM Servico s WHERE s.status = :status")
    BigDecimal somarPorStatus(@Param("status") StatusServico status);
}
