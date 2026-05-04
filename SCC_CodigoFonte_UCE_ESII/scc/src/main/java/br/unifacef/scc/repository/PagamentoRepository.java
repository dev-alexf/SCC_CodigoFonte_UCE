package br.unifacef.scc.repository;

import br.unifacef.scc.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByServicoIdOrderByDataDesc(Long servicoId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p " +
           "WHERE p.data >= :inicio AND p.data <= :fim")
    BigDecimal totalRecebidoNoPeriodo(@Param("inicio") LocalDate inicio,
                                      @Param("fim") LocalDate fim);
}
