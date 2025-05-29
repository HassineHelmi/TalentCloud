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
    public Client updateClientProfile(Long clientId, UpdateClientDto clientDto) { // Renamed parameter to clientDto
        System.out.println("ettst" + clientId);
        return clientRepository.findById(clientId)
                .map(existingClient -> {
                    if (clientDto.getCompanyName() != null) existingClient.setCompanyName(clientDto.getCompanyName());
                    if (clientDto.getIndustry() != null) existingClient.setIndustry(clientDto.getIndustry());
                    if (clientDto.getAddress() != null) existingClient.setAddress(clientDto.getAddress());
                    if (clientDto.getCountry() != null) existingClient.setCountry(clientDto.getCountry());
                    if (clientDto.getPhoneNumber() != null) existingClient.setPhoneNumber(clientDto.getPhoneNumber());
                    if (clientDto.getEmail() != null) existingClient.setEmail(clientDto.getEmail());
                    if (clientDto.getWebsite() != null) existingClient.setWebsite(clientDto.getWebsite());
                    if (clientDto.getLinkedInUrl() != null) existingClient.setLinkedInUrl(clientDto.getLinkedInUrl());
                    if (clientDto.getCompanyDescription() != null) existingClient.setCompanyDescription(clientDto.getCompanyDescription());

                    existingClient.setUpdatedAt(LocalDateTime.now());
                    return clientRepository.save(existingClient);
                })
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id " + clientId));
    }
    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client blockProfile(Long clientId) {
        return clientRepository.findById(clientId)
                .map(existingClient -> {
                    existingClient.setBlocked(true);
                    existingClient.setUpdatedAt(LocalDateTime.now());
                    return clientRepository.save(existingClient);
                })
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id " + clientId));
    }
    @Override
    public Optional<Client> getClientProfileByProfileUserId(Long profileUserId) { // Updated method name
        return clientRepository.findByProfileUserId(profileUserId);
    }
}