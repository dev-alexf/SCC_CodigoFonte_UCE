package br.unifacef.scc.controller;

import br.unifacef.scc.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ServicoService servicoService;

    public DashboardController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalACobrar",     servicoService.totalACobrar());
        model.addAttribute("totalPendente",    servicoService.totalPendente());
        model.addAttribute("totalPago",        servicoService.totalPago());
        model.addAttribute("totalMes",         servicoService.totalRecebidoMes());
        model.addAttribute("qtdInadimplentes", servicoService.getInadimplentes().size());
        return "dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
