package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.exception.BankStatementFileInvalidException;
import com.axorean.reconciliation_service.exception.SystemTransactionFileInvalidException;
import com.axorean.reconciliation_service.model.BankStatement;
import com.axorean.reconciliation_service.model.BankStatements;
import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.TransactionType;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.axorean.reconciliation_service.util.Constant.COMMA_DELIMITER;
import static com.axorean.reconciliation_service.util.Constant.DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class BankStatementService {
    public BankStatements readBankStatementFile(String bankStatementPathFile, ReconciliationRequest request) throws FileNotFoundException, BankStatementFileInvalidException {
        List<BankStatement> bankStatements = new ArrayList<>();
        Path path = Path.of(bankStatementPathFile);
        File file = path.toFile();
        if (!file.exists()) {
            String errorMessage = String.format("Bank statement %s does not exist", bankStatementPathFile);
            throw new FileNotFoundException(errorMessage);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                String trxId = values[0];
                Double amount = Double.parseDouble(values[1]);
                Date transactionDate = simpleDateFormat.parse(values[2]);
                if (request.getStartDate().after(transactionDate) || request.getEndDate().before(transactionDate)) {
                    continue;
                }
                BankStatement bankStatement = new BankStatement(trxId,
                        amount,
                        transactionDate);
                bankStatements.add(bankStatement);
            }
        } catch (Exception exception) {
            String errorMessage = String.format("Error while reading file bank statement with path %s", bankStatementPathFile);
            throw new BankStatementFileInvalidException(errorMessage, exception);
        }
        return new BankStatements(bankStatementPathFile, bankStatements);
    }
}
