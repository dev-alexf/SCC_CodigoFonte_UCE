package br.unifacef.scc.service;

import br.unifacef.scc.model.Cliente;
import br.unifacef.scc.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findByAtivoTrueOrderByNomeAsc();
    }

    public List<Cliente> buscar(String termo) {
        if (termo == null || termo.isBlank()) return listar();
        return repo.buscar(termo.trim());
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        return repo.save(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        repo.findById(id).ifPresent(c -> {
            c.setAtivo(false);
            repo.save(c);
        });
    }
}
