package br.unifacef.scc.model;

public enum PerfilUsuario {
    ADMIN("Administrador"),
    FUNCIONARIO("Funcionario");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
