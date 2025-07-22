package com.talentcloud.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Data transfer object for parsed CV data.
 * Used for both CV parsing service and SQS message processing.
 */
@Data
public class CvParsedDataDto {
    // Additional fields for SQS processing
    @JsonProperty("auth_user_id")
    private String authUserId;
    
    @JsonProperty("resume_key")
    private String resumeKey;
    
    @JsonProperty("s3_bucket")
    private String s3Bucket;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    private String phone;
    @JsonProperty("current_job")
    private String currentJob;
    private String country;
    private String city;
    @JsonProperty("job_category")
    private String jobCategory;
    private String linkedin;
    private SkillsDto skills;
    private List<ExperienceDto> experience;
    private List<CertificationDto> certifications;
    private List<EducationDto> educations;
    
    @Data
    public static class SkillsDto {
        @JsonProperty("programming_languages")
        private List<String> programmingLanguages;
        @JsonProperty("technical_skills")
        private List<String> technicalSkills;
        @JsonProperty("tools_and_technologies")
        private List<String> toolsAndTechnologies;
        @JsonProperty("soft_skills")
        private List<String> softSkills;
        @JsonProperty("custom_skills")
        private List<String> customSkills;
    }
    
    @Data
    public static class ExperienceDto {
        private String title;
        private String company;
        private DurationDto duration;
        private String description;
    }
    
    @Data
    public static class CertificationDto {
        private String name;
        private String issuer;
        private String year;
        private String details;
    }
    
    @Data
    public static class EducationDto {
        private String name;
        private String degree;
        private String department;
        private DurationDto duration;
    }
    
    @Data
    public static class DurationDto {
        private Map<String, String> start;
        private Map<String, String> end;
    }
}
