package com.profile_service.profile_service.controller;

import com.profile_service.profile_service.model.Document;
import com.profile_service.profile_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users/{userId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public Mono<ResponseEntity<Document>> uploadDocument(@PathVariable Long userId,
                                                         @RequestParam("file") MultipartFile file,
                                                         @RequestParam("documentType") String documentType) {
        return documentService.uploadDocument(userId, file, documentType)
                .map(document -> ResponseEntity.ok(document))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
