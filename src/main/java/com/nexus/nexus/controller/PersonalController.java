package com.nexus.nexus.controller;

import com.nexus.nexus.model.Personal;
import com.nexus.nexus.repository.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    @Autowired
    private PersonalRepository personalRepository;

    @GetMapping
    public List<Personal> getAll() {
        return personalRepository.findAll();
    }

    @GetMapping("/{id}")
    public Personal getById(@PathVariable Integer id) {
        return personalRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Personal create(@RequestBody Personal personal) {
        return personalRepository.save(personal);
    }

    @PutMapping("/{id}")
    public Personal update(@PathVariable Integer id, @RequestBody Personal personal) {
        personal.setId_personal(id);
        return personalRepository.save(personal);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        personalRepository.deleteById(id);
    }
}
