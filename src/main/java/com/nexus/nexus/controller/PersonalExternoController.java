package com.nexus.nexus.controller;

import com.nexus.nexus.model.PersonalExterno;
import com.nexus.nexus.repository.PersonalExternoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personalExterno")
public class PersonalExternoController
{
    @Autowired
    private PersonalExternoRepository personalExternoRepository;

    /**
     * Retorna la lista completa de todos los registros de personal externo
     * almacenados en la base de datos.
     * Endpoint: GET /api/personalExterno
     */
    @GetMapping
    public List<PersonalExterno> getAll() {
        return personalExternoRepository.findAll();
    }

    /**
     * Busca y retorna un registro de personal externo por su ID.
     * Si no existe un registro con el ID proporcionado, retorna null.
     * Endpoint: GET /api/personalExterno/{id}
     */
    @GetMapping("/{id}")
    public PersonalExterno getById(@PathVariable Long id) {
        return personalExternoRepository.findById(id).orElse(null);
    }

    /**
     * Crea y guarda un nuevo registro de personal externo en la base de datos
     * a partir del objeto recibido en el cuerpo de la petición.
     * Retorna el registro creado con su ID generado.
     * Endpoint: POST /api/personalExterno
     */
    @PostMapping
    public PersonalExterno create(@RequestBody PersonalExterno personalExterno) {
        return personalExternoRepository.save(personalExterno);
    }

    /**
     * Actualiza un registro existente de personal externo identificado por su ID.
     * Asigna el ID recibido en la URL al objeto del cuerpo de la petición
     * y lo guarda en la base de datos sobreescribiendo el registro anterior.
     * Retorna el registro actualizado.
     * Endpoint: PUT /api/personalExterno/{id}
     */
    @PutMapping("/{id}")
    public PersonalExterno update(@PathVariable Long id, @RequestBody PersonalExterno personalExterno) {
        personalExterno.setId_personal_externo(id);
        return personalExternoRepository.save(personalExterno);
    }

    /**
     * Elimina de la base de datos el registro de personal externo
     * correspondiente al ID proporcionado en la URL.
     * Endpoint: DELETE /api/personalExterno/{id}
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        personalExternoRepository.deleteById(id);
    }
}
