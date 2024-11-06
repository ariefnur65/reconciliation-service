package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.exception.SystemTransactionFileInvalidException;
import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.axorean.reconciliation_service.model.TransactionType.CREDIT;
import static com.axorean.reconciliation_service.model.TransactionType.DEBIT;
import static com.axorean.reconciliation_service.util.Constant.DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SystemTransactionServiceTest {
    @InjectMocks
    private SystemTransactionService systemTransactionService;

    @Test
    void readSystemTransactionFile_shouldReturn3DataTransaction_whenReadFileSystemTrx001Csv() throws ParseException, SystemTransactionFileInvalidException, FileNotFoundException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        String systemTransactionPath = "src/test/resources/SystemRrx001.csv";
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-15 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath(systemTransactionPath)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Date dateTransaction1 = simpleDateFormat.parse("2024-07-12 09:30:40");
        Date dateTransaction2 = simpleDateFormat.parse("2024-07-12 19:30:40");
        Date dateTransaction3 = simpleDateFormat.parse("2024-07-12 21:30:40");
        SystemTransaction systemTransaction1 = new SystemTransaction("123", 4565.4, CREDIT, dateTransaction1);
        SystemTransaction systemTransaction2MinusResult = new SystemTransaction("124", -455.4, DEBIT, dateTransaction2);
        SystemTransaction systemTransaction3 = new SystemTransaction("125", 454.0, CREDIT, dateTransaction3);
        List<SystemTransaction> expectedTransactions = Arrays.asList(systemTransaction1, systemTransaction2MinusResult, systemTransaction3);

        List<SystemTransaction> result = this.systemTransactionService.readSystemTransactionFile(reconciliationRequest);

        assertArrayEquals(expectedTransactions.toArray(), result.toArray());
    }

    @Test
    void readSystemTransactionFile_shouldReturn1DataTransaction_whenReadFileSystemTrx004WithStartDate10JulyAndEndDate11JulyCsv() throws ParseException, SystemTransactionFileInvalidException, FileNotFoundException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        String systemTransactionPath = "src/test/resources/SystemRrx004.csv";
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-11 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath(systemTransactionPath)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Date dateTransaction1 = simpleDateFormat.parse("2024-07-11 09:30:40");
        SystemTransaction systemTransaction1 = new SystemTransaction("123", 4565.4, CREDIT, dateTransaction1);
        List<SystemTransaction> expectedTransactions = List.of(systemTransaction1);

        List<SystemTransaction> result = this.systemTransactionService.readSystemTransactionFile(reconciliationRequest);

        assertArrayEquals(expectedTransactions.toArray(), result.toArray());
    }

    @Test
    void readSystemTransactionFile_shouldThrowSystemTransactionFileInvalidException_whenReadFileSystemTrx002Csv() {
        String systemTransactionPath = "src/test/resources/SystemRrx002.csv";
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath(systemTransactionPath)
                .build();

        assertThrows(SystemTransactionFileInvalidException.class, () -> this.systemTransactionService.readSystemTransactionFile(reconciliationRequest));
    }

    @Test
    void readSystemTransactionFile_shouldThrowFileNotFoundException_whenReadFileSystemTrx003Csv() {
        String systemTransactionPath = "src/test/resources/SystemRrx003.csv";
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath(systemTransactionPath)
                .build();

        assertThrows(FileNotFoundException.class, () -> this.systemTransactionService.readSystemTransactionFile(reconciliationRequest));
    }

}