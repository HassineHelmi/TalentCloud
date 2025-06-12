package com.talentcloud.profile.iservice;

import com.talentcloud.profile.dto.ClientProfileDto;
import com.talentcloud.profile.model.Client;
import com.talentcloud.profile.model.Profile;

import java.util.List;
import java.util.Optional;

public interface IServiceClient {
    Client createClientProfile(Client client, Profile userProfile);
    Optional<Client> getClientById(Long clientId);
    Client updateClientAndProfile(Long clientId, ClientProfileDto dto); // Updated method
    List<Client> getAllClients();
    Client blockProfile(Long clientId);
    Optional<Client> getClientProfileByProfileUserId(Long profileUserId);
}