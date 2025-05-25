package com.talentcloud.profile.iservice;


import com.talentcloud.profile.dto.UpdateClientDto;
import com.talentcloud.profile.model.Client;

import java.util.List;
import java.util.Optional;

public interface IServiceClient {
    Client createClientProfile(Client client);
    Optional<Client> getClientById(Long clientId);
    Client updateClientProfile(Long clientId, UpdateClientDto client);
    List<Client> getAllClients();  // Add this method
    Client blockProfile(Long clientId);  // Add the blockProfile method for Client
    Optional<Client> getClientProfileByUserId(Long userId); // Added method to find by user ID

}

