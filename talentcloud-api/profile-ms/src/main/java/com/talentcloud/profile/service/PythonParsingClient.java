package com.talentcloud.profile.service;

import com.talentcloud.profile.dto.CvParsedDataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class PythonParsingClient {

    private final RestTemplate restTemplate;

    @Value("${cv.parser.api.url}")
    private String pythonApiUrl;

    public PythonParsingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CvParsedDataDto parseCvFromS3(String s3Key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("s3_file_key", s3Key);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(pythonApiUrl, entity, CvParsedDataDto.class);
    }
}