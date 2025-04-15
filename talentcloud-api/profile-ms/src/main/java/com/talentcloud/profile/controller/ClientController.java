package com.talentcloud.profile.controller;


import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final IServiceClient serviceClient;

    @Autowired
    public ClientController(IServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @PostMapping("/create")
    public ResponseEntity<Client> createClientProfile(@RequestBody @Valid Client client) {
        Client createdClient = serviceClient.createClientProfile(client);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId) {
        return serviceClient.getClientById(clientId)
                .map(client -> new ResponseEntity<>(client, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{clientId}/edit")
    public ResponseEntity<Client> editClientProfile(@PathVariable Long clientId, @RequestBody @Valid UpdateClientDto client) {
        if (client.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        Client updatedClient = serviceClient.updateClientProfile(clientId, client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    // Ensure this method is mapped specifically for /all
    @GetMapping("/all")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = serviceClient.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
    @PutMapping("/{clientId}/block")
    public ResponseEntity<Client> blockProfile(@PathVariable Long clientId) {
        Client blockedClient = serviceClient.blockProfile(clientId);
        return new ResponseEntity<>(blockedClient, HttpStatus.OK);
    }
}
