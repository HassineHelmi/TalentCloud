//package com.talentcloud.profile.dto;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.persistence.Column;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import org.hibernate.validator.constraints.URL;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class UpdateClientDto {
//
//    @NotBlank(message = "Company name is required")
//    private String companyName;
//    @NotBlank(message = "Industry is required")
//    private String industry;
//    @NotBlank(message = "Address is required")
//    private String address;
//    private String country;
//    @NotBlank(message = "Phone number is required")
//    private String phoneNumber;
//    @Email(message = "Please provide a valid email address")
//    private String email;
//    @URL(message = "Please provide a valid website URL")
//    private String website;
//    @URL(message = "Please provide a valid linkedin URL")
//    private String linkedInUrl;
//
//    @Column(columnDefinition = "TEXT")
//    private String companyDescription;
//}