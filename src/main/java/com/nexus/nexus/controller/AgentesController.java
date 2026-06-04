package com.nexus.nexus.controller;

import com.nexus.nexus.model.Agentes;
import com.nexus.nexus.repository.AgentesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agentes")
public class AgentesController {

    @Autowired
    private AgentesRepository agentesRepository;

    @GetMapping
    public List<Agentes> getAll() {
        return agentesRepository.findAll();
    }

    @GetMapping("/{id}")
    public Agentes getById(@PathVariable Long id) {
        return agentesRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Agentes create(@RequestBody Agentes agente) {
        return agentesRepository.save(agente);
    }

    @PutMapping("/{id}")
    public Agentes update(@PathVariable Long id, @RequestBody Agentes agente) {
        agente.setId_agente(id);
        return agentesRepository.save(agente);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        agentesRepository.deleteById(id);
    }
}
