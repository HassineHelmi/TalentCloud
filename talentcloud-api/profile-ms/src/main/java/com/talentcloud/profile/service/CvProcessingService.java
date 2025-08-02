//package com.talentcloud.profile.service;
//
//import com.talentcloud.profile.dto.CvParsedDataDto;
//import com.talentcloud.profile.model.*;
//import com.talentcloud.profile.repository.*;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDate;
//import java.time.Month;
//import java.time.format.TextStyle;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//@Service
//public class CvProcessingService {
//
//    private final ProfileRepository profileRepository;
//    private final CandidateRepository candidateRepository;
//    private final ExperienceRepository experienceRepository;
//    private final EducationRepository educationRepository;
//    private final SkillsRepository skillRepository;
//    private final CertificationRepository certificationRepository;
//
//    public CvProcessingService(ProfileRepository profileRepository, CandidateRepository candidateRepository, ExperienceRepository experienceRepository, EducationRepository educationRepository, SkillsRepository skillsRepository, CertificationRepository certificationRepository) {
//        this.profileRepository = profileRepository;
//        this.candidateRepository = candidateRepository;
//        this.experienceRepository = experienceRepository;
//        this.educationRepository = educationRepository;
//        this.skillRepository = skillsRepository;
//        this.certificationRepository = certificationRepository;
//    }
//
//    @Transactional
//    public void hydrateProfileFromCv(String authUserId, String resumeS3Url, CvParsedDataDto cvData) {
//        Profile profile = profileRepository.findByAuthServiceUserId(authUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user auth ID: " + authUserId));
//
//        profile.setFirstName(cvData.getFirstName());
//        profile.setLastName(cvData.getLastName());
//        profile.setPhoneNumber(cvData.getPhone());
//        profile.setAddress(String.format("%s, %s", cvData.getCity(), cvData.getCountry()));
//        profile.setLinkedInUrl(cvData.getLinkedin());
//        profileRepository.save(profile);
//
//        Candidate candidate = candidateRepository.findByProfileUserId(profile.getId())
//                .orElseGet(() -> {
//                    Candidate newCandidate = new Candidate();
//                    newCandidate.setProfileUserId(profile.getId());
//                    return newCandidate;
//                });
//
//        candidate.setJobTitle(cvData.getCurrentJob());
//        candidate.setJobCategory(cvData.getJobCategory());
//        candidate.setResumeUrl(resumeS3Url);
//        candidate.setVisibilitySettings(VisibilitySettings.valueOf("PUBLIC"));
//        candidateRepository.save(candidate);
//
//        processExperience(candidate, cvData.getExperience());
//        processEducation(candidate, cvData.getEducations());
//        processSkills(candidate, cvData.getSkills());
//        processCertifications(candidate, cvData.getCertifications());
//    }
//
//    private void processExperience(Candidate candidate, List<CvParsedDataDto.ExperienceDto> experienceList) {
//        experienceRepository.deleteByCandidateId(candidate.getId());
//        if (experienceList == null || experienceList.isEmpty()) return;
//
//        for (CvParsedDataDto.ExperienceDto dto : experienceList) {
//            Experience exp = new Experience();
//            exp.setCandidate(candidate);
//            exp.setJobTitle(dto.getTitle());
//            exp.setCompanyName(dto.getCompany());
//            exp.setDescription(dto.getDescription());
//            if (dto.getDuration() != null) {
//                exp.setStartDate(parseDate(dto.getDuration().getStart()));
//                exp.setEndDate(parseDate(dto.getDuration().getEnd()));
//                exp.setIsCurrent(dto.getDuration().getEnd() == null);
//            }
//            experienceRepository.save(exp);
//        }
//    }
//
//    private void processEducation(Candidate candidate, List<CvParsedDataDto.EducationDto> educationList) {
//        educationRepository.deleteByCandidateId(candidate.getId());
//        if (educationList == null || educationList.isEmpty()) return;
//
//        for (CvParsedDataDto.EducationDto dto : educationList) {
//            Education edu = new Education();
//            edu.setCandidate(candidate);
//            edu.setInstitutionName(dto.getName());
//            edu.setDegree(dto.getDegree());
//            edu.setFieldOfStudy(dto.getDepartment());
//            if (dto.getDuration() != null) {
//                edu.setStartDate(parseDate(dto.getDuration().getStart()));
//                edu.setEndDate(parseDate(dto.getDuration().getEnd()));
//                edu.setIsCurrent(dto.getDuration().getEnd() == null);
//            }
//            educationRepository.save(edu);
//        }
//    }
//
//    private void processSkills(Candidate candidate, CvParsedDataDto.SkillsDto skillsDto) {
//        skillRepository.deleteByCandidateId(candidate.getId());
//        if (skillsDto == null) return;
//
//        Skills skills = new Skills();
//        skills.setCandidate(candidate);
//
//        // Add null-safe list joining to prevent NullPointerException
//        skills.setProgrammingLanguages(skillsDto.getProgrammingLanguages() != null ?
//            String.join(", ", skillsDto.getProgrammingLanguages()) : "");
//        skills.setTechnicalSkill(skillsDto.getTechnicalSkills() != null ?
//            String.join(", ", skillsDto.getTechnicalSkills()) : "");
//        skills.setToolsAndTechnologies(skillsDto.getToolsAndTechnologies() != null ?
//            String.join(", ", skillsDto.getToolsAndTechnologies()) : "");
//        skills.setSoftSkills(skillsDto.getSoftSkills() != null ?
//            String.join(", ", skillsDto.getSoftSkills()) : "");
//        skills.setCustomSkills(skillsDto.getCustomSkills() != null ?
//            String.join(", ", skillsDto.getCustomSkills()) : "");
//
//        skillRepository.save(skills);
//    }
//
//    private void processCertifications(Candidate candidate, List<CvParsedDataDto.CertificationDto> certList) {
//        certificationRepository.deleteByCandidateId(candidate.getId());
//        if (certList == null || certList.isEmpty()) return;
//
//        for (CvParsedDataDto.CertificationDto dto : certList) {
//            Certification cert = new Certification();
//            cert.setCandidate(candidate);
//            cert.setName(dto.getName());
//            cert.setOrganization(dto.getIssuer());
//            cert.setCertificationUrl(dto.getDetails());
//            if (dto.getYear() != null && !dto.getYear().isBlank()) {
//                cert.setObtainedDate(LocalDate.of(Integer.parseInt(dto.getYear()), 1, 1));
//            }
//            certificationRepository.save(cert);
//        }
//    }
//
//    private LocalDate parseDate(Map<String, String> dateMap) {
//        if (dateMap == null || dateMap.get("year") == null || dateMap.get("year").isBlank()) {
//            return null;
//        }
//        try {
//            int year = Integer.parseInt(dateMap.get("year"));
//            String monthStr = dateMap.get("month");
//            if (monthStr == null || monthStr.isBlank()) {
//                return LocalDate.of(year, 1, 1);
//            }
//            for (Month month : Month.values()) {
//                if (month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).equalsIgnoreCase(monthStr)) {
//                    return LocalDate.of(year, month, 1);
//                }
//            }
//            return LocalDate.of(year, 1, 1);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}