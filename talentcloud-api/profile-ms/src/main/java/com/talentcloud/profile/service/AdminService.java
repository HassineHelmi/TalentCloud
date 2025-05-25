package com.talentcloud.profile.service;

import com.talentcloud.profile.iservice.IServiceAdmin;
import com.talentcloud.profile.model.Admin;
import com.talentcloud.profile.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService implements IServiceAdmin {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Optional<Admin> getAdminProfileByUserId(Long userId) {
        return adminRepository.findByUserId(userId);
    }

    @Override
    public Admin createAdminProfile(Admin admin) {
        return adminRepository.save(admin);
    }


}