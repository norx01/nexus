package com.nexus.nexus.view;

import com.nexus.nexus.model.*;
import com.nexus.nexus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class AutoservicioView {

    @Autowired private PersonalRepository personalRepository;
    @Autowired private AmbientesRepository ambientesRepository;
    @Autowired private LlavesRepository llavesRepository;
    @Autowired private PrestamoAmbientesRepository prestamoRepository;

    @GetMapping("/autoservicio")
    public String autoservicio(Model model) {
        model.addAttribute("step", "cedula");
        return "autoservicio";
    }

    @PostMapping("/autoservicio/buscar")
    public String buscarInstructor(@RequestParam String cedula, Model model) {
        Optional<Personal> personalOpt = personalRepository.findByDocumento(cedula.trim());

        if (personalOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró ningún instructor con cédula: " + cedula);
            model.addAttribute("step", "cedula");
            return "autoservicio";
        }

        Personal personal = personalOpt.get();
        Optional<PrestamoAmbientes> prestamoActivo = prestamoRepository.findPrestamoActivo(personal.getId_personal());

        if (prestamoActivo.isPresent()) {
            PrestamoAmbientes prestamo = prestamoActivo.get();
            Ambientes ambiente = (prestamo.getId_ambiente() != null)
                    ? ambientesRepository.findById(prestamo.getId_ambiente()).orElse(null) : null;
            Llaves llave = (prestamo.getId_llave() != null)
                    ? llavesRepository.findById(prestamo.getId_llave()).orElse(null) : null;

            model.addAttribute("step", "liberar");
            model.addAttribute("personal", personal);
            model.addAttribute("prestamo", prestamo);
            model.addAttribute("ambiente", ambiente);
            model.addAttribute("llave", llave);
        } else {
            List<Ambientes> ambientesDisponibles = ambientesRepository.findAll()
                    .stream()
                    .filter(a -> "Disponible".equalsIgnoreCase(a.getEstado()))
                    .toList();

            model.addAttribute("step", "reservar");
            model.addAttribute("personal", personal);
            model.addAttribute("ambientes", ambientesDisponibles);
        }

        return "autoservicio";
    }

    @PostMapping("/autoservicio/reservar")
    public String reservar(@RequestParam Integer idPersonal,
                           @RequestParam Integer idAmbiente,
                           @RequestParam Long idLlave,
                           RedirectAttributes ra) {

        Personal personal = personalRepository.findById(idPersonal).orElse(null);
        Ambientes ambiente = ambientesRepository.findById(idAmbiente).orElse(null);
        Llaves llave = llavesRepository.findById(idLlave).orElse(null);

        if (personal == null || ambiente == null || llave == null) {
            ra.addFlashAttribute("error", "Error al procesar la solicitud.");
            return "redirect:/autoservicio";
        }

        // Crear préstamo
        PrestamoAmbientes prestamo = PrestamoAmbientes.builder()
                .id_personal(idPersonal)
                .id_ambiente(idAmbiente)
                .id_llave(idLlave)
                .fecha(LocalDate.now())
                .hora_entrada(LocalTime.now())
                .estado("Activo")
                .build();
        prestamoRepository.save(prestamo);

        // Marcar ambiente como ocupado
        ambiente.setEstado("Ocupado");
        ambiente.setId_personal_activo(idPersonal);
        ambientesRepository.save(ambiente);

        // Marcar llave como prestada
        llave.setEstado("Prestada");
        llavesRepository.save(llave);

        ra.addFlashAttribute("exito", "✅ Ambiente y llave asignados a " + personal.getNombre());
        return "redirect:/autoservicio";
    }

    @PostMapping("/autoservicio/liberar")
    public String liberar(@RequestParam Long idPrestamo, RedirectAttributes ra) {
        PrestamoAmbientes prestamo = prestamoRepository.findById(idPrestamo).orElse(null);
        if (prestamo == null) {
            ra.addFlashAttribute("error", "Préstamo no encontrado.");
            return "redirect:/autoservicio";
        }

        // Cerrar préstamo
        prestamo.setHora_salida(LocalTime.now());
        prestamo.setEstado("Finalizado");
        prestamoRepository.save(prestamo);

        // Liberar ambiente
        ambientesRepository.findById(prestamo.getId_ambiente()).ifPresent(a -> {
            a.setEstado("Disponible");
            a.setId_personal_activo(null);
            ambientesRepository.save(a);
        });

        // Liberar llave
        if (prestamo.getId_llave() != null) {
            llavesRepository.findById(prestamo.getId_llave()).ifPresent(l -> {
                l.setEstado("Disponible");
                llavesRepository.save(l);
            });
        }

        ra.addFlashAttribute("exito", "✅ Ambiente y llave liberados correctamente.");
        return "redirect:/autoservicio";
    }

    // Cargar llaves disponibles de un ambiente (AJAX)
    @GetMapping("/autoservicio/llaves/{idAmbiente}")
    @ResponseBody
    public List<Llaves> llavesDeAmbiente(@PathVariable Long idAmbiente) {
        return llavesRepository.findAll().stream()
                .filter(l -> idAmbiente.equals(l.getId_ambiente())
                        && "Disponible".equalsIgnoreCase(l.getEstado()))
                .toList();
    }
}
