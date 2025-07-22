package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.UpdateClientProfessionalDto;
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
    private final ProfileRepository profileRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, ProfileRepository profileRepository) {
        this.clientRepository = clientRepository;
        this.profileRepository = profileRepository;
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
    public Client updateClientProfessionalDetails(String authServiceUserId, UpdateClientProfessionalDto dto) {
        Profile profile = profileRepository.findByAuthServiceUserId(authServiceUserId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found."));

        // Then, find the specific client profile using the profile's ID
        Client existingClient = clientRepository.findByProfileUserId(profile.getId())
                .orElseThrow(() -> new ClientNotFoundException("Client professional profile not found for this user."));

        // Now, update ONLY the professional (Client entity) fields
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
