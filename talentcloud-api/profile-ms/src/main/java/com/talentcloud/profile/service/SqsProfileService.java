package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CvParsedDataDto;
import com.talentcloud.profile.model.*;
import com.talentcloud.profile.repository.CandidateRepository;
import com.talentcloud.profile.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class SqsProfileService {

    private final ProfileRepository profileRepository;
    private final CandidateRepository candidateRepository;

    public SqsProfileService(ProfileRepository profileRepository, CandidateRepository candidateRepository) {
        this.profileRepository = profileRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public void processProfileData(CvParsedDataDto parsedData) {
        if (parsedData.getAuthUserId() == null || parsedData.getAuthUserId().isBlank()) {
            log.error("SQS message is missing the required authUserId. Cannot process profile.");
            return;
        }

        Profile profile = profileRepository.findByAuthServiceUserId(parsedData.getAuthUserId())
                .orElseGet(() -> createNewProfile(parsedData));

        Candidate candidate = candidateRepository.findByProfileUserId(profile.getId())
                .orElseGet(() -> createNewCandidate(profile, parsedData));

        hydrateCandidate(candidate, parsedData);

        candidateRepository.save(candidate);
        log.info("Successfully processed and saved CV data for authUserId: {}", profile.getAuthServiceUserId());
    }

    private Profile createNewProfile(CvParsedDataDto parsedData) {
        Profile profile = new Profile();
        profile.setAuthServiceUserId(parsedData.getAuthUserId());
        profile.setEmail(parsedData.getEmail());
        profile.setFirstName(parsedData.getFirstName());
        profile.setLastName(parsedData.getLastName());
        profile.setPhoneNumber(parsedData.getPhone());
        profile.setAddress(parsedData.getCity() != null ? parsedData.getCity() + ", " + parsedData.getCountry() : parsedData.getCountry());
        profile.setLinkedInUrl(parsedData.getLinkedin());
        return profileRepository.save(profile);
    }

    private Candidate createNewCandidate(Profile profile, CvParsedDataDto parsedData) {
        Candidate candidate = new Candidate();
        candidate.setProfileUserId(profile.getId());
        candidate.setResumeUrl(parsedData.getResumeKey());
        return candidate;
    }

    private void hydrateCandidate(Candidate candidate, CvParsedDataDto dto) {
        candidate.setJobTitle(dto.getCurrentJob());
        candidate.setJobCategory(dto.getJobCategory());
        candidate.setResumeUrl(dto.getResumeKey());
        candidate.setVisibilitySettings(VisibilitySettings.PUBLIC);

        candidate.getExperiences().clear();
        candidate.getEducations().clear();
        candidate.getSkills().clear();
        candidate.getCertifications().clear();

        // --- MAPPING LOGIC WITH VALIDATION ---

        if (dto.getExperience() != null) {
            dto.getExperience().forEach(expDto -> {
                LocalDate startDate = parseDurationToLocalDate(expDto.getDuration(), true);

                // ** FIX IS HERE: Only add the experience if it has a valid start date **
                if (startDate != null) {
                    Experience exp = new Experience();
                    LocalDate endDate = parseDurationToLocalDate(expDto.getDuration(), false);

                    exp.setJobTitle(expDto.getTitle());
                    exp.setCompanyName(expDto.getCompany());
                    exp.setDescription(expDto.getDescription());
                    exp.setStartDate(startDate);
                    exp.setEndDate(endDate);
                    exp.setIsCurrent(endDate == null);
                    exp.setCandidate(candidate);
                    candidate.getExperiences().add(exp);
                } else {
                    log.warn("Skipping experience record due to missing start date: {}", expDto.getTitle());
                }
            });
        }

        if (dto.getEducations() != null) {
            dto.getEducations().forEach(eduDto -> {
                Education edu = new Education();
                edu.setInstitutionName(eduDto.getName());
                edu.setDegree(eduDto.getDegree());
                edu.setFieldOfStudy(eduDto.getDepartment());
                edu.setStartDate(parseDurationToLocalDate(eduDto.getDuration(), true));
                edu.setEndDate(parseDurationToLocalDate(eduDto.getDuration(), false));
                edu.setCandidate(candidate);
                candidate.getEducations().add(edu);
            });
        }

        if (dto.getSkills() != null) {
            Skills skillsEntity = new Skills();
            CvParsedDataDto.SkillsDto skillsDto = dto.getSkills();
            skillsEntity.setProgrammingLanguages(joinList(skillsDto.getProgrammingLanguages()));
            skillsEntity.setTechnicalSkill(joinList(skillsDto.getTechnicalSkills()));
            skillsEntity.setToolsAndTechnologies(joinList(skillsDto.getToolsAndTechnologies()));
            skillsEntity.setSoftSkills(joinList(skillsDto.getSoftSkills()));
            skillsEntity.setCustomSkills(joinList(skillsDto.getCustomSkills()));
            skillsEntity.setCandidate(candidate);
            candidate.getSkills().add(skillsEntity);
        }

        if (dto.getCertifications() != null) {
            dto.getCertifications().forEach(certDto -> {
                Certification cert = new Certification();
                cert.setName(certDto.getName());
                cert.setOrganization(certDto.getIssuer());
                cert.setCertificationUrl(certDto.getDetails());
                if (certDto.getYear() != null && !certDto.getYear().isBlank()) {
                    try {
                        cert.setObtainedDate(LocalDate.of(Integer.parseInt(certDto.getYear()), 1, 1));
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse certification year: {}", certDto.getYear());
                    }
                }
                cert.setCandidate(candidate);
                candidate.getCertifications().add(cert);
            });
        }
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(", ", list);
    }

    private LocalDate parseDurationToLocalDate(CvParsedDataDto.DurationDto durationDto, boolean isStart) {
        if (durationDto == null) return null;

        Map<String, String> dateMap = isStart ? durationDto.getStart() : durationDto.getEnd();
        if (dateMap == null || !dateMap.containsKey("year") ||
            dateMap.get("year") == null || dateMap.get("year").trim().isEmpty()) {
            return null;
        }

        try {
            String yearStr = dateMap.get("year").trim();
            if (yearStr.isEmpty()) {
                return null;
            }

            int year = Integer.parseInt(yearStr);
            String monthStr = dateMap.get("month");

            if (monthStr == null || monthStr.trim().isEmpty()) {
                return LocalDate.of(year, (isStart ? Month.JANUARY : Month.DECEMBER), 1);
            }

            monthStr = monthStr.trim();
            for (Month month : Month.values()) {
                if (month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).equalsIgnoreCase(monthStr)) {
                    return LocalDate.of(year, month, 1);
                }
            }

            return LocalDate.of(year, (isStart ? Month.JANUARY : Month.DECEMBER), 1);

        } catch (Exception e) {
            log.warn("Could not parse date from map: {}", dateMap, e);
            return null;
        }
    }
}