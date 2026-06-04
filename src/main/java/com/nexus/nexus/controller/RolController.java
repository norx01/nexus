package com.nexus.nexus.controller;

import com.nexus.nexus.model.Rol;
import com.nexus.nexus.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rol")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @GetMapping("/{id}")
    public Rol getById(@PathVariable Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Rol create(@RequestBody Rol rol) {
        return rolRepository.save(rol);
    }

    @PutMapping("/{id}")
    public Rol update(@PathVariable Long id, @RequestBody Rol rol) {
        rol.setId_rol(id);
        return rolRepository.save(rol);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rolRepository.deleteById(id);
    }
}
