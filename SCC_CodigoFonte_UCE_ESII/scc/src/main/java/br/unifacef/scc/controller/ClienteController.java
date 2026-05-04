package br.unifacef.scc.controller;

import br.unifacef.scc.model.*;
import br.unifacef.scc.repository.PagamentoRepository;
import br.unifacef.scc.service.ClienteService;
import br.unifacef.scc.service.ServicoService;
import br.unifacef.scc.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService     clienteService;
    private final ServicoService     servicoService;
    private final UsuarioService     usuarioService;
    private final PagamentoRepository pagamentoRepo;

    public ClienteController(ClienteService clienteService, ServicoService servicoService,
                             UsuarioService usuarioService, PagamentoRepository pagamentoRepo) {
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.pagamentoRepo  = pagamentoRepo;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        List<Cliente> clientes = (busca != null && !busca.isBlank())
                ? clienteService.buscar(busca)
                : clienteService.listar();
        model.addAttribute("clientes", clientes);
        model.addAttribute("busca", busca);
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("editando", false);
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Cliente cliente, BindingResult br,
                         RedirectAttributes ra, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("editando", cliente.getId() != null);
            return "clientes/form";
        }
        clienteService.salvar(cliente);
        ra.addFlashAttribute("sucesso", "Cliente salvo com sucesso!");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Cliente c = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
        model.addAttribute("cliente", c);
        model.addAttribute("editando", true);
        return "clientes/form";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        clienteService.excluir(id);
        ra.addFlashAttribute("sucesso", "Cliente removido.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}")
    public String ficha(@PathVariable Long id, Model model) {
        Cliente c = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
        List<Servico> servicos = servicoService.listarPorCliente(id);

        BigDecimal totalGasto = servicos.stream()
                .map(Servico::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoDevedor = servicos.stream()
                .filter(s -> s.getStatus() != StatusServico.PAGO)
                .map(s -> {
                    BigDecimal pago = pagamentoRepo.findByServicoIdOrderByDataDesc(s.getId())
                            .stream().map(Pagamento::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return s.getValor().subtract(pago).max(BigDecimal.ZERO);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cliente",      c);
        model.addAttribute("servicos",     servicos);
        model.addAttribute("totalGasto",   totalGasto);
        model.addAttribute("saldoDevedor", saldoDevedor);
        model.addAttribute("statusList",   StatusServico.values());
        model.addAttribute("hoje",         LocalDate.now().toString());
        return "clientes/ficha";
    }

    @PostMapping("/{id}/servicos/salvar")
    public String salvarServico(@PathVariable Long id,
                                @RequestParam String descricao,
                                @RequestParam BigDecimal valor,
                                @RequestParam String data,
                                @AuthenticationPrincipal UserDetails user,
                                RedirectAttributes ra) {
        Cliente c = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
        Usuario u = usuarioService.buscarPorLogin(user.getUsername());

        Servico s = new Servico();
        s.setCliente(c); s.setDescricao(descricao);
        s.setValor(valor); s.setData(LocalDate.parse(data));
        s.setStatus(StatusServico.A_COBRAR); s.setUsuario(u);
        servicoService.salvar(s);

        ra.addFlashAttribute("sucesso", "Servico cadastrado!");
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/servicos/{sid}/status")
    public String alterarStatus(@PathVariable Long sid,
                                @RequestParam StatusServico status,
                                @RequestParam Long clienteId,
                                RedirectAttributes ra) {
        servicoService.alterarStatus(sid, status);
        ra.addFlashAttribute("sucesso", "Status atualizado!");
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/servicos/{sid}/pagamento")
    public String registrarPagamento(@PathVariable Long sid,
                                     @RequestParam BigDecimal valor,
                                     @RequestParam String data,
                                     @RequestParam(required = false) String observacao,
                                     @RequestParam Long clienteId,
                                     @AuthenticationPrincipal UserDetails user,
                                     RedirectAttributes ra) {
        servicoService.registrarPagamento(sid, valor, LocalDate.parse(data), observacao, user.getUsername());
        ra.addFlashAttribute("sucesso", "Pagamento registrado!");
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/servicos/{sid}/excluir")
    public String excluirServico(@PathVariable Long sid,
                                 @RequestParam Long clienteId,
                                 RedirectAttributes ra) {
        servicoService.excluir(sid);
        ra.addFlashAttribute("sucesso", "Servico removido.");
        return "redirect:/clientes/" + clienteId;
    }
}
