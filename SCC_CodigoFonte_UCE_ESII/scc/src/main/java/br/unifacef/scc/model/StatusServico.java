package br.unifacef.scc.model;

/**
 * Ciclo de vida de um servico prestado:
 *  A_COBRAR  -> servico anotado, ainda nao cobrado ao cliente
 *  PENDENTE  -> cobranca emitida, aguardando pagamento
 *  PAGO      -> valor integralmente quitado
 */
public enum StatusServico {
    A_COBRAR("A Cobrar"),
    PENDENTE("Pendente"),
    PAGO("Pago");

    private final String descricao;

    StatusServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
