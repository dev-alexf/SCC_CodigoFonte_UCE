package br.unifacef.scc.service;

import br.unifacef.scc.model.*;
import br.unifacef.scc.repository.PagamentoRepository;
import br.unifacef.scc.repository.ServicoRepository;
import br.unifacef.scc.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository    servicoRepo;
    private final PagamentoRepository  pagamentoRepo;
    private final UsuarioRepository    usuarioRepo;

    public ServicoService(ServicoRepository servicoRepo,
                          PagamentoRepository pagamentoRepo,
                          UsuarioRepository usuarioRepo) {
        this.servicoRepo   = servicoRepo;
        this.pagamentoRepo = pagamentoRepo;
        this.usuarioRepo   = usuarioRepo;
    }

    public List<Servico> listarPorCliente(Long clienteId) {
        return servicoRepo.findByClienteIdOrderByDataDesc(clienteId);
    }

    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepo.findById(id);
    }

    public List<Servico> getInadimplentes() {
        LocalDateTime limite = LocalDateTime.now().minusDays(30);
        return servicoRepo.findInadimplentes(limite);
    }

    public List<Servico> relatorio(Long clienteId, StatusServico status,
                                   LocalDate inicio, LocalDate fim) {
        return servicoRepo.relatorio(clienteId, status, inicio, fim);
    }

    public BigDecimal totalACobrar()  { return servicoRepo.somarPorStatus(StatusServico.A_COBRAR); }
    public BigDecimal totalPendente() { return servicoRepo.somarPorStatus(StatusServico.PENDENTE); }
    public BigDecimal totalPago()     { return servicoRepo.somarPorStatus(StatusServico.PAGO); }

    public BigDecimal totalRecebidoMes() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return pagamentoRepo.totalRecebidoNoPeriodo(inicio, LocalDate.now());
    }

    @Transactional
    public Servico salvar(Servico servico) {
        return servicoRepo.save(servico);
    }

    @Transactional
    public void alterarStatus(Long servicoId, StatusServico novoStatus) {
        servicoRepo.findById(servicoId).ifPresent(s -> {
            s.alterarStatus(novoStatus);
            servicoRepo.save(s);
        });
    }

    @Transactional
    public void registrarPagamento(Long servicoId, BigDecimal valor,
                                   LocalDate data, String obs, String loginUsuario) {
        Servico s = servicoRepo.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Servico nao encontrado"));

        Usuario u = usuarioRepo.findByLogin(loginUsuario).orElse(null);

        Pagamento p = new Pagamento();
        p.setServico(s);
        p.setValor(valor);
        p.setData(data);
        p.setObservacao(obs);
        p.setUsuario(u);
        pagamentoRepo.save(p);

        // Recalcula se deve marcar como pago
        BigDecimal totalPago = pagamentoRepo.findByServicoIdOrderByDataDesc(servicoId)
                .stream().map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPago.compareTo(s.getValor()) >= 0) {
            s.alterarStatus(StatusServico.PAGO);
            servicoRepo.save(s);
        }
    }

    @Transactional
    public void excluir(Long id) {
        servicoRepo.deleteById(id);
    }

    public List<Pagamento> listarPagamentos(Long servicoId) {
        return pagamentoRepo.findByServicoIdOrderByDataDesc(servicoId);
    }
}
