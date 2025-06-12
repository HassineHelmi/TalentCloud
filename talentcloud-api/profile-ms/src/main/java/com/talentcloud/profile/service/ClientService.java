package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.ClientProfileDto;
import com.talentcloud.profile.exception.ClientNotFoundException;
import com.talentcloud.profile.iservice.IServiceClient;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.model.Profile;
import com.talentcloud.profile.repository.ClientRepository;
import com.talentcloud.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final ProfileRepository profileRepository; // + Added

    @Autowired
    public ClientService(ClientRepository clientRepository, ProfileRepository profileRepository) { // + Modified
        this.clientRepository = clientRepository;
        this.profileRepository = profileRepository; // + Added
    }

    @Override
    public Client createClientProfile(Client client, Profile userProfile) {
        client.setProfileUserId(userProfile.getId());
        client.setCreatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long clientId) {
        return clientRepository.findById(clientId);
    }

    @Override
    @Transactional
    public Client updateClientAndProfile(Long clientId, ClientProfileDto dto) {
        // 1. Find the client
        Client existingClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id " + clientId));

        // 2. Find the associated profile
        Profile profile = profileRepository.findById(existingClient.getProfileUserId())
                .orElseThrow(() -> new EntityNotFoundException("Associated profile not found for client id " + clientId));

        // 3. Update Profile fields if they are provided
        if (dto.getFirstName() != null) profile.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) profile.setLastName(dto.getLastName());
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) profile.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) profile.setEmail(dto.getEmail()); // Assumes email can be updated here
        profileRepository.save(profile);

        // 4. Update Client fields if they are provided
        if (dto.getCompanyName() != null) existingClient.setCompanyName(dto.getCompanyName());
        if (dto.getIndustry() != null) existingClient.setIndustry(dto.getIndustry());
        if (dto.getCountry() != null) existingClient.setCountry(dto.getCountry());
        if (dto.getWebsite() != null) existingClient.setWebsite(dto.getWebsite());
        if (dto.getLinkedInUrl() != null) existingClient.setLinkedInUrl(dto.getLinkedInUrl());
        if (dto.getCompanyDescription() != null) existingClient.setCompanyDescription(dto.getCompanyDescription());

        existingClient.setUpdatedAt(LocalDateTime.now());
        return clientRepository.save(existingClient);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Optional<Client> getClientProfileByProfileUserId(Long profileUserId) {
        return clientRepository.findByProfileUserId(profileUserId);
    }
}