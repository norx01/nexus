package com.nexus.nexus.view;

import com.nexus.nexus.model.Personal;
import com.nexus.nexus.model.RegistroEntradaPersonal;
import com.nexus.nexus.repository.PersonalRepository;
import com.nexus.nexus.repository.RegistroEntradaPersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ScannerView {

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private RegistroEntradaPersonalRepository registroRepository;

    @GetMapping("/scanner")
    public String scannerPage(Model model) {
        cargarModelo(model);
        return "scanner";
    }

    @PostMapping("/scanner/escanear")
    public String escanear(@RequestParam String cedula, Model model) {
        Optional<Personal> personalOpt = personalRepository.findByDocumento(cedula.trim());

        if (personalOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró ningún instructor con cédula: " + cedula);
            cargarModelo(model);
            return "scanner";
        }

        Personal personal = personalOpt.get();
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now().withNano(0);

        Optional<RegistroEntradaPersonal> registroActivo =
                registroRepository.findRegistroActivoHoy(personal.getId_personal(), hoy);

        if (registroActivo.isEmpty()) {
            RegistroEntradaPersonal nuevo = RegistroEntradaPersonal.builder()
                    .id_personal(personal.getId_personal())
                    .fecha(hoy)
                    .hora_entrada(ahora)
                    .estado("Dentro")
                    .build();
            registroRepository.save(nuevo);
            model.addAttribute("exito", "✅ ENTRADA registrada para " + personal.getNombre() + " a las " + ahora);
        } else {
            RegistroEntradaPersonal registro = registroActivo.get();
            registro.setHora_salida(ahora);
            registro.setEstado("Retirado");
            registroRepository.save(registro);
            model.addAttribute("exito", "🚪 SALIDA registrada para " + personal.getNombre() + " a las " + ahora);
        }

        cargarModelo(model);
        return "scanner";
    }

    private void cargarModelo(Model model) {
        List<Personal> todoElPersonal = personalRepository.findAll();

        Map<Integer, String> personalMap = todoElPersonal.stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));

        Map<Integer, String> rolMap = todoElPersonal.stream()
                .collect(Collectors.toMap(
                        Personal::getId_personal,
                        p -> p.getRol() != null ? p.getRol() : "Sin rol"
                ));

        List<RegistroEntradaPersonal> dentroHoy = registroRepository
                .findByFecha(LocalDate.now())
                .stream()
                .filter(r -> "Dentro".equals(r.getEstado()))
                .toList();

        model.addAttribute("dentroHoy", dentroHoy);
        model.addAttribute("personalMap", personalMap);
        model.addAttribute("rolMap", rolMap);
    }
}
