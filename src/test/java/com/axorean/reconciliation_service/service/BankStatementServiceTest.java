package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.exception.BankStatementFileInvalidException;
import com.axorean.reconciliation_service.model.BankStatement;
import com.axorean.reconciliation_service.model.BankStatements;
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

import static com.axorean.reconciliation_service.util.Constant.DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BankStatementServiceTest {
    @InjectMocks
    private BankStatementService bankStatementService;

    @Test
    void readBankStatementFile_shouldReturnBankStatementsThatContain3BankStatement_whenReadFileBankStatementSMBI() throws ParseException, FileNotFoundException, BankStatementFileInvalidException {
        String bankStatementPathFile = "src/test/resources/BankStatementSMBI.csv";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-15 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Date transactionDate = simpleDateFormat.parse("2024-07-12 09:30:40");
        Date transactionDate2 = simpleDateFormat.parse("2024-07-12 19:30:40");
        Date transactionDate3 = simpleDateFormat.parse("2024-07-12 21:30:40");
        BankStatement bankStatement = new BankStatement("2", 4565.4, transactionDate);
        BankStatement bankStatement2 = new BankStatement("3", -455.4, transactionDate2);
        BankStatement bankStatement3 = new BankStatement("4", 454.0, transactionDate3);
        List<BankStatement> statements = Arrays.asList(bankStatement, bankStatement2, bankStatement3);
        BankStatements expected = new BankStatements(bankStatementPathFile, statements);

        BankStatements resultRead = this.bankStatementService.readBankStatementFile(bankStatementPathFile, reconciliationRequest);

        assertEquals(expected.filePath(), resultRead.filePath());
        assertArrayEquals(expected.statements().toArray(), resultRead.statements().toArray());
    }

    @Test
    void readBankStatementFile_shouldReturnBankStatementsThatContain1BankStatement_whenReadFileBankStatementSMBB() throws ParseException, FileNotFoundException, BankStatementFileInvalidException {
        String bankStatementPathFile = "src/test/resources/BankStatementSMBB.csv";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-11 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Date transactionDate = simpleDateFormat.parse("2024-07-11 09:30:40");
        BankStatement bankStatement = new BankStatement("2", 4565.4, transactionDate);
        List<BankStatement> statements = List.of(bankStatement);
        BankStatements expected = new BankStatements(bankStatementPathFile, statements);

        BankStatements resultRead = this.bankStatementService.readBankStatementFile(bankStatementPathFile, reconciliationRequest);

        assertEquals(expected.filePath(), resultRead.filePath());
        assertArrayEquals(expected.statements().toArray(), resultRead.statements().toArray());
    }

    @Test
    void readBankStatementFile_shouldThrowFileNotFoundException_whenReadFileBankStatementSMBC() throws ParseException {
        String bankStatementPathFile = "src/test/resources/BankStatementSMBC.csv";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-11 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThrows(FileNotFoundException.class, () -> this.bankStatementService.readBankStatementFile(bankStatementPathFile, reconciliationRequest));
    }

    @Test
    void readBankStatementFile_shouldThrowBankStatementFIleInvalidException_whenReadFileBankStatementSMBP() throws ParseException {
        String bankStatementPathFile = "src/test/resources/BankStatementSMBP.csv";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-11 23:59:59");
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThrows(BankStatementFileInvalidException.class, () -> this.bankStatementService.readBankStatementFile(bankStatementPathFile, reconciliationRequest));
    }
}