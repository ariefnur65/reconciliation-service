package com.axorean.reconciliation_service.controller;


import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import com.axorean.reconciliation_service.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
class ReconciliationController {
    private final ReconciliationService reconciliationService;

    @PostMapping(value = "reconcile-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReconciliationResponse> reconcileDataController(@RequestBody ReconciliationRequest request) throws ExecutionException, InterruptedException {
        ReconciliationResponse reconciliationResponse = reconciliationService.reconcileData(request);
        return new ResponseEntity<>(reconciliationResponse, HttpStatus.OK);

    }
}
