package com.profile_service.profile_service.repository;

import com.profile_service.profile_service.dto.DocumentDTO;
import com.profile_service.profile_service.model.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DocumentRepository extends ReactiveCrudRepository<Document, Long> {

}