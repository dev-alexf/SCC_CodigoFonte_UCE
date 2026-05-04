package br.unifacef.scc.service;

import br.unifacef.scc.model.PerfilUsuario;
import br.unifacef.scc.model.Usuario;
import br.unifacef.scc.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario u = repo.findByLogin(login.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + login));
        if (!u.isAtivo()) throw new UsernameNotFoundException("Usuario inativo.");
        String role = "ROLE_" + u.getPerfil().name();
        return new org.springframework.security.core.userdetails.User(
                u.getLogin(), u.getSenha(), List.of(new SimpleGrantedAuthority(role)));
    }

    public Usuario buscarPorLogin(String login) {
        return repo.findByLogin(login.toLowerCase()).orElse(null);
    }

    @Transactional
    public Usuario salvar(String nome, String login, String senha, PerfilUsuario perfil) {
        Usuario u = new Usuario(nome, login.toLowerCase(), passwordEncoder.encode(senha), perfil);
        return repo.save(u);
    }

    public boolean existeLogin(String login) {
        return repo.existsByLogin(login.toLowerCase());
    }
}
