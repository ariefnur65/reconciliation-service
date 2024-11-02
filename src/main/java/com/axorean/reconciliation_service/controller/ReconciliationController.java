package com.axorean.reconciliation_service.controller;


import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ReconciliationController {

    @PostMapping("reconcile-data")
    public ResponseEntity<ReconciliationResponse> ReconcileDataController (@RequestBody ReconciliationRequest request) {

        return new ResponseEntity<>(null);

    }
}
