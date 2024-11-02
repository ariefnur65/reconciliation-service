package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.axorean.reconciliation_service.model.TransactionType.CREDIT;
import static com.axorean.reconciliation_service.model.TransactionType.DEBIT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SystemTransactionServiceTest {
    @InjectMocks
    private SystemTransactionService systemTransactionService;

    @Test
    void readSystemTransactionFile_shouldReturn3DataTransaction_whenReadFileSystemTrx001Csv() throws ParseException {
        String systemTransactionPath = "src/test/resources/SystemRrx001.csv";
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath(systemTransactionPath)
                .build();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTransaction1 = simpleDateFormat.parse("2024-07-12 09:30:40");
        Date dateTransaction2 = simpleDateFormat.parse("2024-07-12 19:30:40");
        Date dateTransaction3 = simpleDateFormat.parse("2024-07-12 21:30:40");
        SystemTransaction systemTransaction1 = new SystemTransaction("123", 4565.4, CREDIT, dateTransaction1);
        SystemTransaction systemTransaction2 = new SystemTransaction("124", 455.4, DEBIT, dateTransaction2);
        SystemTransaction systemTransaction3 = new SystemTransaction("125", 454.0, CREDIT, dateTransaction3);
        List<SystemTransaction> expectedTransactions = Arrays.asList(systemTransaction1, systemTransaction2, systemTransaction3);

        List<SystemTransaction> result = this.systemTransactionService.readSystemTransactionFile(reconciliationRequest);

        assertArrayEquals(expectedTransactions.toArray(), result.toArray());
    }
}