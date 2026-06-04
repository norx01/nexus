package com.nexus.nexus.view;

import com.nexus.nexus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardView {

    @Autowired private UsuariosRepository usuariosRepository;
    @Autowired private PersonalRepository personalRepository;
    @Autowired private PersonalExternoRepository personalExternoRepository;
    @Autowired private AmbientesRepository ambientesRepository;
    @Autowired private LlavesRepository llavesRepository;
    @Autowired private PrestamoAmbientesRepository prestamoAmbientesRepository;
    @Autowired private AgentesRepository agentesRepository;
    @Autowired private RolRepository rolRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", usuariosRepository.count());
        model.addAttribute("totalPersonal", personalRepository.count());
        model.addAttribute("totalPersonalExterno", personalExternoRepository.count());
        model.addAttribute("totalAmbientes", ambientesRepository.count());
        model.addAttribute("totalAmbientesDisponibles",
                ambientesRepository.findAll().stream()
                        .filter(a -> "Disponible".equalsIgnoreCase(a.getEstado()))
                        .count());
        model.addAttribute("totalLlaves", llavesRepository.count());
        model.addAttribute("totalLlavesDisponibles",
                llavesRepository.findAll().stream()
                        .filter(l -> "Disponible".equalsIgnoreCase(l.getEstado()))
                        .count());
        model.addAttribute("totalPrestamos", prestamoAmbientesRepository.count());
        model.addAttribute("totalAgentes", agentesRepository.count());

        model.addAttribute("ultimosExternos",
                personalExternoRepository.findAll().stream()
                        .sorted((a, b) -> {
                            if (a.getFecha_visita() == null) return 1;
                            if (b.getFecha_visita() == null) return -1;
                            return b.getFecha_visita().compareTo(a.getFecha_visita());
                        })
                        .limit(5)
                        .toList());

        model.addAttribute("ultimosPrestamos",
                prestamoAmbientesRepository.findAll().stream()
                        .sorted((a, b) -> {
                            if (a.getFecha() == null) return 1;
                            if (b.getFecha() == null) return -1;
                            return b.getFecha().compareTo(a.getFecha());
                        })
                        .limit(5)
                        .toList());

        return "dashboard";
    }
}
