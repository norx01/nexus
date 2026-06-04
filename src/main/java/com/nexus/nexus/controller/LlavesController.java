package com.nexus.nexus.controller;

import com.nexus.nexus.model.Llaves;
import com.nexus.nexus.repository.LlavesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/llaves")
public class LlavesController {

    @Autowired
    private LlavesRepository llavesRepository;

    @GetMapping
    public List<Llaves> getAll() {
        return llavesRepository.findAll();
    }

    @GetMapping("/{id}")
    public Llaves getById(@PathVariable Long id) {
        return llavesRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Llaves create(@RequestBody Llaves llave) {
        return llavesRepository.save(llave);
    }

    @PutMapping("/{id}")
    public Llaves update(@PathVariable Long id, @RequestBody Llaves llave) {
        llave.setId_llave(id);
        return llavesRepository.save(llave);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        llavesRepository.deleteById(id);
    }
}
