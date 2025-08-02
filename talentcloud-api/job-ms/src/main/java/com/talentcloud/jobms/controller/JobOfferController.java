package com.talentcloud.jobms.controller;
import com.talentcloud.jobms.dto.JobOfferDTO;
import com.talentcloud.jobms.model.OfferStatus;
import com.talentcloud.jobms.service.JobOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class JobOfferController {

    private final JobOfferService offerService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('ADMIN')")
    public ResponseEntity<JobOfferDTO> sendOffer(@Valid @RequestBody JobOfferDTO dto) {
        return new ResponseEntity<>(offerService.sendOffer(dto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasAuthority('CANDIDATE')")
    public ResponseEntity<JobOfferDTO> respondToOffer(@PathVariable Long id, @RequestParam OfferStatus response) {
        return ResponseEntity.ok(offerService.respondToOffer(id, response));
    }
}