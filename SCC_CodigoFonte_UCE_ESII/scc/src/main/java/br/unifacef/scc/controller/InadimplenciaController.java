package br.unifacef.scc.controller;

import br.unifacef.scc.model.StatusServico;
import br.unifacef.scc.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inadimplencia")
public class InadimplenciaController {

    private final ServicoService servicoService;

    public InadimplenciaController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("inadimplentes", servicoService.getInadimplentes());
        return "inadimplencia";
    }

    @PostMapping("/marcar-pago/{id}")
    public String marcarPago(@PathVariable Long id, RedirectAttributes ra) {
        servicoService.alterarStatus(id, StatusServico.PAGO);
        ra.addFlashAttribute("sucesso", "Servico marcado como Pago!");
        return "redirect:/inadimplencia";
    }
}
