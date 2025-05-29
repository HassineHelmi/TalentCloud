package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.model.Client;

import java.util.List;
import java.util.Optional;

public interface IServiceClient {
    Client createClientProfile(Client client);
    Optional<Client> getClientById(Long clientId); // Retained clientId for direct lookup
    Client updateClientProfile(Long clientId, UpdateClientDto client);
    List<Client> getAllClients();
    Client blockProfile(Long clientId);
    Optional<Client> getClientProfileByProfileUserId(Long profileUserId); // Changed method name
}