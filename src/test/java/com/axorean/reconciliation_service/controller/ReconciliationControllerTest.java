package com.axorean.reconciliation_service.controller;

import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import com.axorean.reconciliation_service.service.ReconciliationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = {ReconciliationController.class})
class ReconciliationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReconciliationService reconciliationService;

    @Test
    void reconcileDataController_whouldHaveStatusOK_whenCalled() throws Exception {
        ReconciliationRequest request = new ReconciliationRequest("src/test/resources/BankStatementSMBI.csv", new String[]{"src/test/resources/BankStatementSMBI.csv"}, new Date(), new Date());
        ObjectMapper mapper = new ObjectMapper();
        when(this.reconciliationService.reconcileData(request)).thenReturn(ReconciliationResponse.builder().build());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reconcile-data")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}