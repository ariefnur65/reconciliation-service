package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.TransactionType;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.axorean.reconciliation_service.util.Constant.COMMA_DELIMITER;
import static com.axorean.reconciliation_service.util.Constant.DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class SystemTransactionService {

    public List<SystemTransaction> readSystemTransactionFile(ReconciliationRequest request) {
        Path path = Path.of(request.getSystemTransactionPath());
        File file = path.toFile();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        List<SystemTransaction> transactions = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                String trxId = values[0];
                Double amount = Double.parseDouble(values[1]);
                TransactionType transactionType = TransactionType.valueOf(values[2]);
                Date transactionDate = dateFormat.parse(values[3]);
                SystemTransaction transaction = new SystemTransaction(trxId,
                        amount,
                        transactionType,
                        transactionDate);
                transactions.add(transaction);
            }

        } catch (Exception ex) {

        }
        return transactions;
    }
}
