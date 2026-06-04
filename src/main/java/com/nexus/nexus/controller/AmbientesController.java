package com.nexus.nexus.controller;

import com.nexus.nexus.model.Ambientes;
import com.nexus.nexus.repository.AmbientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
public class AmbientesController {

    @Autowired
    private AmbientesRepository ambientesRepository;

    @GetMapping
    public List<Ambientes> getAll() {
        return ambientesRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ambientes getById(@PathVariable Integer id) {
        return ambientesRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Ambientes create(@RequestBody Ambientes ambiente) {
        return ambientesRepository.save(ambiente);
    }

    @PutMapping("/{id}")
    public Ambientes update(@PathVariable Integer id, @RequestBody Ambientes ambiente) {
        ambiente.setId_ambiente(id);
        return ambientesRepository.save(ambiente);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        ambientesRepository.deleteById(id);
    }
}
