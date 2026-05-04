package br.unifacef.scc.controller;

import br.unifacef.scc.model.Servico;
import br.unifacef.scc.model.StatusServico;
import br.unifacef.scc.service.ClienteService;
import br.unifacef.scc.service.ServicoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final ServicoService servicoService;
    private final ClienteService clienteService;

    public RelatorioController(ServicoService servicoService, ClienteService clienteService) {
        this.servicoService = servicoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String relatorio(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusServico status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {

        boolean filtrou = clienteId != null || status != null || inicio != null || fim != null;
        List<Servico> resultados = filtrou
                ? servicoService.relatorio(clienteId, status, inicio, fim)
                : List.of();

        BigDecimal totalValor = resultados.stream()
                .map(Servico::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("clientes",   clienteService.listar());
        model.addAttribute("statusList", StatusServico.values());
        model.addAttribute("resultados", resultados);
        model.addAttribute("totalValor", totalValor);
        model.addAttribute("filtrou",    filtrou);
        model.addAttribute("clienteId",  clienteId);
        model.addAttribute("status",     status);
        model.addAttribute("inicio",     inicio);
        model.addAttribute("fim",        fim);
        return "relatorios/relatorio";
    }
}
