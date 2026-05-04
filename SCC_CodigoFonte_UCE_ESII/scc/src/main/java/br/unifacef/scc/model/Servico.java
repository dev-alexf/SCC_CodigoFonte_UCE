package br.unifacef.scc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico prestado pelo escritorio.
 *
 * Fluxo de status (conforme elicitacao de requisitos):
 *   A_COBRAR -> servico anotado, ainda nao cobrado
 *   PENDENTE -> cobranca emitida ao cliente, aguardando pagamento
 *   PAGO     -> valor integralmente quitado
 */
@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Descricao e obrigatoria")
    @Column(nullable = false, length = 200)
    private String descricao;

    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data e obrigatoria")
    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusServico status = StatusServico.A_COBRAR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dataStatusAlterado = LocalDateTime.now();

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    public Servico() {}

    // --- Metodos de negocio ---

    @Transient
    public BigDecimal getTotalPago() {
        return pagamentos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getSaldoDevedor() {
        return valor.subtract(getTotalPago()).max(BigDecimal.ZERO);
    }

    @Transient
    public long getDiasPendente() {
        if (status != StatusServico.PENDENTE) return 0;
        return ChronoUnit.DAYS.between(dataStatusAlterado.toLocalDate(), LocalDate.now());
    }

    public void alterarStatus(StatusServico novoStatus) {
        this.status = novoStatus;
        this.dataStatusAlterado = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public StatusServico getStatus() { return status; }
    public void setStatus(StatusServico status) { this.status = status; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime d) { this.dataCadastro = d; }
    public LocalDateTime getDataStatusAlterado() { return dataStatusAlterado; }
    public void setDataStatusAlterado(LocalDateTime d) { this.dataStatusAlterado = d; }
    public List<Pagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; }
}
