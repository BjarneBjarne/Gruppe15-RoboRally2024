package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Table.Register;
import com.example.demo.repository.RegisterRepository;

@RestController
public class ProgController {

    private RegisterRepository registerRepository;

    public ProgController(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    @GetMapping(value = "/games/{gameId}/registers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Register[]> getRegisters(@PathVariable("gameId") long gameId) {
        Register[] registers = registerRepository.findByGameId(gameId);
        return ResponseEntity.ok(registers);
    }

    @PostMapping(value = "/games/{gameId}/registers/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void postRegister(@RequestBody Register register, @PathVariable("playerId") long playerId) {
        registerRepository.save(register);
    }

    @PostMapping(value = "/games/{gameId}/choice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void postChoice() {
    }

    @GetMapping(value = "/games/{gameId}/choice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRegisters() {
        return ResponseEntity.badRequest().build();
    }
}