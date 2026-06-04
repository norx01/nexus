package com.nexus.nexus.controller;

import com.nexus.nexus.model.PrestamoAmbientes;
import com.nexus.nexus.repository.PrestamoAmbientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamoAmbientes")
public class PrestamoAmbientesController {

    @Autowired
    private PrestamoAmbientesRepository prestamoAmbientesRepository;

    @GetMapping
    public List<PrestamoAmbientes> getAll() {
        return prestamoAmbientesRepository.findAll();
    }

    @GetMapping("/{id}")
    public PrestamoAmbientes getById(@PathVariable Long id) {
        return prestamoAmbientesRepository.findById(id).orElse(null);
    }

    @PostMapping
    public PrestamoAmbientes create(@RequestBody PrestamoAmbientes prestamo) {
        return prestamoAmbientesRepository.save(prestamo);
    }

    @PutMapping("/{id}")
    public PrestamoAmbientes update(@PathVariable Long id, @RequestBody PrestamoAmbientes prestamo) {
        prestamo.setId_prestamo(id);
        return prestamoAmbientesRepository.save(prestamo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        prestamoAmbientesRepository.deleteById(id);
    }
}
