//package com.talentcloud.profile.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.core.SdkBytes;
//import software.amazon.awssdk.services.lambda.LambdaClient;
//import software.amazon.awssdk.services.lambda.model.InvocationType;
//import software.amazon.awssdk.services.lambda.model.InvokeRequest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class LambdaService {
//
//    private final LambdaClient lambdaClient;
//    private final ObjectMapper objectMapper;
//
//    @Value("${aws.lambda.function-name}")
//    private String lambdaFunctionName;
//
//    public LambdaService(LambdaClient lambdaClient, ObjectMapper objectMapper) {
//        this.lambdaClient = lambdaClient;
//        this.objectMapper = objectMapper;
//    }
//
//    @Async
//    public void triggerCvParsing(String s3Key, String authUserId, String s3Bucket) {
//        try {
//            Map<String, String> payload = new HashMap<>();
//            payload.put("s3_key", s3Key);
//            payload.put("s3_bucket", s3Bucket);
//            payload.put("auth_user_id", authUserId);
//
//            InvokeRequest invokeRequest = InvokeRequest.builder()
//                    .functionName(lambdaFunctionName)
//                    .invocationType(InvocationType.EVENT)
//                    .payload(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(payload)))
//                    .build();
//
//            lambdaClient.invoke(invokeRequest);
//            System.out.println("Successfully triggered Lambda function for user: " + authUserId);
//        } catch (Exception e) {
//            System.err.println("Error triggering Lambda function: " + e.getMessage());
//            // In a real application, add more robust error handling (e.g., save failed triggers to a DB table for retry)
//        }
//    }
//}