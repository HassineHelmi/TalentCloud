package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.exception.ClientNotFoundException;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService implements IServiceClient {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client createClientProfile(Client client) {
        client.setCreatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> getClientById(Long clientId) {
        return clientRepository.findById(clientId);
    }

    @Override
    @Transactional
    public Client updateClientProfile(Long clientId, UpdateClientDto client) {

        System.out.println("ettst"+ clientId);
        return clientRepository.findById(clientId)
                .map(existingClient -> {
                    if (client.getCompanyName() != null) existingClient.setCompanyName(client.getCompanyName());
                    if (client.getIndustry() != null) existingClient.setIndustry(client.getIndustry());
                    if (client.getAddress() != null) existingClient.setAddress(client.getAddress());
                    if (client.getCountry() != null) existingClient.setCountry(client.getCountry());
                    if (client.getPhoneNumber() != null) existingClient.setPhoneNumber(client.getPhoneNumber());
                    if (client.getEmail() != null) existingClient.setEmail(client.getEmail());
                    if (client.getWebsite() != null) existingClient.setWebsite(client.getWebsite());
                    if (client.getLinkedInUrl() != null) existingClient.setLinkedInUrl(client.getLinkedInUrl());
                    if (client.getLogoUrl() != null) existingClient.setLogoUrl(client.getLogoUrl());
                    if (client.getCompanyDescription() != null) existingClient.setCompanyDescription(client.getCompanyDescription());

                    existingClient.setUpdatedAt(LocalDateTime.now());
                    return clientRepository.save(existingClient);
                })
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id " + clientId));
    }
    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();  // Fetch all clients from the database
    }

    @Override
    public Client blockProfile(Long clientId) {
        return clientRepository.findById(clientId)
                .map(existingClient -> {
                    existingClient.setBlocked(true); // Set blocked to true
                    existingClient.setUpdatedAt(LocalDateTime.now()); // Update the timestamp
                    return clientRepository.save(existingClient);
                })
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id " + clientId));
    }
    @Override
    public Optional<Client> getClientProfileByUserId(Long userId) {
        return clientRepository.findByUserId(userId);
    }
}
