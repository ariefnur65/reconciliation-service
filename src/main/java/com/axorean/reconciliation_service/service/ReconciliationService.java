package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.exception.SystemTransactionFileInvalidException;
import com.axorean.reconciliation_service.model.BankStatement;
import com.axorean.reconciliation_service.model.BankStatements;
import com.axorean.reconciliation_service.model.DetailMismatchedTransaction;
import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ReconciliationService {
    private final BankStatementService bankStatementService;
    private final SystemTransactionService systemTransactionService;
    private final ExecutorService executorService;

    public ReconciliationResponse reconcileData(ReconciliationRequest request) throws ExecutionException, InterruptedException {
        try {
            Date startDate = this.setStarterDate(request.getStartDate());
            Date endDate = this.setEndDate(request.getStartDate());
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            Future<List<SystemTransaction>> readSystemTransactionFile = this.executorService.submit(() -> this.systemTransactionService.readSystemTransactionFile(request));
            List<Future<BankStatements>> listBankStatementFuture = new ArrayList<>();
            for (String pathBankStatement : request.getBankStatements()) {
                Future<BankStatements> bankStatementsFuture = this.executorService.submit(() -> this.bankStatementService.readBankStatementFile(pathBankStatement, request));
                listBankStatementFuture.add(bankStatementsFuture);
            }
            List<SystemTransaction> listSystemTransaction = readSystemTransactionFile.get();
            List<BankStatements> bankStatementsList = new ArrayList<>();
            long totalInitialTransaction = listSystemTransaction.size();
            for (Future<BankStatements> bankStatementFuture : listBankStatementFuture) {
                BankStatements bankStatements = bankStatementFuture.get();
                totalInitialTransaction = totalInitialTransaction + bankStatements.statements().size();
                bankStatementsList.add(bankStatements);
            }
            long transactionMatch = 0;
            for (SystemTransaction systemTransaction : listSystemTransaction) {
                boolean isFound = findInBankStatementList(systemTransaction, bankStatementsList, listSystemTransaction);
                if (isFound) {
                    transactionMatch++;
                }
            }
            long totalMisMatchedTransactions = totalInitialTransaction - transactionMatch * 2;
            long totalTransaction = totalInitialTransaction + totalMisMatchedTransactions;
            double totalDiscrepancy = 0;
            if (!listSystemTransaction.isEmpty()) {
                totalDiscrepancy = listSystemTransaction.stream()
                        .reduce((double) 0, (partialAmountResult, systemTransaction) -> partialAmountResult + systemTransaction.amount(), Double::sum);
            }
            for (BankStatements bankStatements : bankStatementsList) {
                List<BankStatement> bankStatementList = bankStatements.statements();
                if (bankStatementList.isEmpty()) {
                    bankStatementsList.remove(bankStatements);
                    continue;
                }
                totalDiscrepancy += bankStatementList.stream()
                        .reduce((double) 0, (partialAmountResult, statement) -> partialAmountResult + statement.amount(), Double::sum);
            }

            DetailMismatchedTransaction detailMismatchedTransaction = new DetailMismatchedTransaction(listSystemTransaction, bankStatementsList);
            return ReconciliationResponse.builder()
                    .totalTransactions(totalTransaction)
                    .totalMatchedTransaction(transactionMatch)
                    .totalMisMatchedTransactions(totalMisMatchedTransactions)
                    .totalDiscrepancy(totalDiscrepancy)
                    .detailMismatchedTransaction(detailMismatchedTransaction)
                    .build();

        } catch (Exception exception) {
            throw exception;
        }
    }

    private static boolean findInBankStatementList(SystemTransaction systemTransaction, List<BankStatements> bankStatementsList, List<SystemTransaction> listSystemTransaction) {
        for (BankStatements bankStatements : bankStatementsList) {
            List<BankStatement> statements = bankStatements.statements();
            boolean isFound = findBankStatement(systemTransaction, statements, listSystemTransaction);
            if (isFound) {
                return true;
            }
        }
        return false;
    }

    private static boolean findBankStatement(SystemTransaction systemTransaction, List<BankStatement> statements, List<SystemTransaction> listSystemTransaction) {
        for (BankStatement statement : statements) {
            boolean isAmountEqual = Objects.equals(statement.amount(), systemTransaction.amount());
            boolean isTransactionTimeEquals = Objects.equals(systemTransaction.transactionTime(), statement.date());
            if (isAmountEqual && isTransactionTimeEquals) {
                listSystemTransaction.remove(systemTransaction);
                statements.remove(statement);
                return true;
            }
        }
        return false;
    }

    private Date setStarterDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        return calendar.getTime();
    }

    private Date setEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59);
        return calendar.getTime();
    }
}
