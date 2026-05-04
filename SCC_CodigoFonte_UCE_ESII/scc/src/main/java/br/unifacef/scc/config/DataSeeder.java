package br.unifacef.scc.config;

import br.unifacef.scc.model.*;
import br.unifacef.scc.repository.*;
import br.unifacef.scc.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Popula o banco com dados de exemplo na primeira execucao.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository   usuarioRepo;
    private final ClienteRepository   clienteRepo;
    private final ServicoRepository   servicoRepo;
    private final PagamentoRepository pagamentoRepo;
    private final UsuarioService      usuarioService;

    public DataSeeder(UsuarioRepository usuarioRepo, ClienteRepository clienteRepo,
                      ServicoRepository servicoRepo, PagamentoRepository pagamentoRepo,
                      UsuarioService usuarioService) {
        this.usuarioRepo   = usuarioRepo;
        this.clienteRepo   = clienteRepo;
        this.servicoRepo   = servicoRepo;
        this.pagamentoRepo = pagamentoRepo;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepo.existsByLogin("admin")) return; // ja inicializado

        System.out.println("[SCC] Inicializando banco de dados...");

        // Usuarios
        Usuario admin = usuarioService.salvar("Administrador", "admin", "admin123", PerfilUsuario.ADMIN);
        usuarioService.salvar("Ana Funcionaria", "funcionario", "func123", PerfilUsuario.FUNCIONARIO);

        // Clientes
        Cliente c1 = new Cliente();
        c1.setTipo("PF"); c1.setNome("Joao"); c1.setSobrenome("da Silva");
        c1.setCpf("111.111.111-11"); c1.setTelefone("(16) 99111-1111");
        c1.setEmail("joao@email.com");
        clienteRepo.save(c1);

        Cliente c2 = new Cliente();
        c2.setTipo("PF"); c2.setNome("Maria"); c2.setSobrenome("Oliveira");
        c2.setCpf("222.222.222-22"); c2.setTelefone("(16) 99222-2222");
        c2.setEmail("maria@email.com");
        clienteRepo.save(c2);

        Cliente c3 = new Cliente();
        c3.setTipo("PJ"); c3.setNome("Comercio Ramos"); c3.setSobrenome("ME");
        c3.setCpf("33.333.333/0001-33"); c3.setTelefone("(16) 3333-3333");
        c3.setEmail("comercio@ramos.com");
        clienteRepo.save(c3);

        LocalDate hoje = LocalDate.now();
        LocalDate mesP = hoje.minusMonths(1).withDayOfMonth(10);

        // Servico 1 - Pendente (cobranca emitida)
        Servico s1 = new Servico();
        s1.setCliente(c1); s1.setDescricao("Declaracao IRPF 2026");
        s1.setValor(new BigDecimal("350.00")); s1.setData(hoje.withDayOfMonth(1));
        s1.setStatus(StatusServico.PENDENTE); s1.setUsuario(admin);
        s1.setDataStatusAlterado(LocalDateTime.now().minusDays(5));
        servicoRepo.save(s1);

        // Servico 2 - Pago
        Servico s2 = new Servico();
        s2.setCliente(c1); s2.setDescricao("Abertura de MEI");
        s2.setValor(new BigDecimal("150.00")); s2.setData(hoje);
        s2.setStatus(StatusServico.PAGO); s2.setUsuario(admin);
        s2.setDataStatusAlterado(LocalDateTime.now().minusDays(10));
        servicoRepo.save(s2);

        // Servico 3 - A Cobrar
        Servico s3 = new Servico();
        s3.setCliente(c2); s3.setDescricao("Assessoria Fiscal Mensal");
        s3.setValor(new BigDecimal("480.00")); s3.setData(hoje.withDayOfMonth(1));
        s3.setStatus(StatusServico.A_COBRAR); s3.setUsuario(admin);
        servicoRepo.save(s3);

        // Servico 4 - INADIMPLENTE (pendente ha 40 dias)
        Servico s4 = new Servico();
        s4.setCliente(c3); s4.setDescricao("Folha de Pagamento");
        s4.setValor(new BigDecimal("620.00")); s4.setData(mesP);
        s4.setStatus(StatusServico.PENDENTE); s4.setUsuario(admin);
        s4.setDataStatusAlterado(LocalDateTime.now().minusDays(40));
        servicoRepo.save(s4);

        // Servico 5 - Pago (mes anterior)
        Servico s5 = new Servico();
        s5.setCliente(c2); s5.setDescricao("Declaracao IRPF 2026");
        s5.setValor(new BigDecimal("350.00")); s5.setData(mesP);
        s5.setStatus(StatusServico.PAGO); s5.setUsuario(admin);
        s5.setDataStatusAlterado(LocalDateTime.now().minusDays(20));
        servicoRepo.save(s5);

        // Pagamentos de exemplo
        Pagamento p1 = new Pagamento();
        p1.setServico(s2); p1.setValor(new BigDecimal("150.00"));
        p1.setData(hoje.minusDays(8)); p1.setUsuario(admin);
        pagamentoRepo.save(p1);

        Pagamento p2 = new Pagamento();
        p2.setServico(s5); p2.setValor(new BigDecimal("350.00"));
        p2.setData(mesP.plusDays(3)); p2.setUsuario(admin);
        pagamentoRepo.save(p2);

        // Pagamento parcial no s1
        Pagamento p3 = new Pagamento();
        p3.setServico(s1); p3.setValor(new BigDecimal("200.00"));
        p3.setData(hoje); p3.setObservacao("Entrada parcial"); p3.setUsuario(admin);
        pagamentoRepo.save(p3);

        System.out.println("[SCC] Banco inicializado! Login: admin / admin123");
    }
}
