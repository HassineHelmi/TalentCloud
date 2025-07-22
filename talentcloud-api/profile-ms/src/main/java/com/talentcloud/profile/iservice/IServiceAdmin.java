package com.talentcloud.profile.iservice;

import com.talentcloud.profile.model.Admin;

import java.util.Optional;

public interface IServiceAdmin {
    Optional<Admin> getAdminProfileByProfileUserId(Long profileUserId);
    Admin createAdminProfile(Admin admin);
}