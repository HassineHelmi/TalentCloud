package com.profile_service.profile_service.service;

import com.profile_service.profile_service.model.Document;
import com.profile_service.profile_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class DocumentService {
    @Autowired
    private  DocumentRepository documentRepository;
    @Autowired
    private  S3Service s3Service;

    // New method to handle file upload and saving document metadata
    public Mono<Document> uploadDocument(Long userId, MultipartFile file, String documentType) {
        return Mono.fromCallable(() -> s3Service.uploadFile(file))
                .subscribeOn(Schedulers.boundedElastic())  // offload blocking call
                .flatMap(url -> {
                    Document doc = new Document();
//                    doc.setUserId(userId);
//                    doc.setDocumentType(documentType);
//                    doc.setDocumentUrl(url);
                    // Optionally, you can set storageUrl if needed:
                    // doc.setStorageUrl(url);
                    return documentRepository.save(doc);
                });
    }

    public Mono<Void> deleteDocument(Long id) {
        return documentRepository.deleteById(id);
    }
}
