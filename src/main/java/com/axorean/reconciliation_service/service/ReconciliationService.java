package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.model.BankStatement;
import com.axorean.reconciliation_service.model.BankStatements;
import com.axorean.reconciliation_service.model.DetailMismatchedTransaction;
import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            Date endDate = this.setEndDate(request.getEndDate());
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
            Iterator<SystemTransaction> systemTransactionIterator = listSystemTransaction.iterator();
            while (systemTransactionIterator.hasNext()) {
                SystemTransaction systemTransaction = systemTransactionIterator.next();
                boolean isFound = findInBankStatementList(systemTransaction, bankStatementsList, systemTransactionIterator);
                if (isFound) {
                    transactionMatch++;
                }
            }
            long totalMisMatchedTransactions = totalInitialTransaction - transactionMatch * 2;
            long totalTransaction = transactionMatch + totalMisMatchedTransactions;
            double totalDiscrepancy = 0;
            if (!listSystemTransaction.isEmpty()) {
                totalDiscrepancy = listSystemTransaction.stream()
                        .reduce((double) 0, (partialAmountResult, systemTransaction) -> partialAmountResult + systemTransaction.amount(), Double::sum);
            }
            Iterator<BankStatements> bankStatementsIterator = bankStatementsList.iterator();
            while (bankStatementsIterator.hasNext()) {
                BankStatements bankStatements = bankStatementsIterator.next();
                List<BankStatement> bankStatementList = bankStatements.statements();
                if (bankStatementList.isEmpty()) {
                    bankStatementsIterator.remove();
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

    private static boolean findInBankStatementList(SystemTransaction systemTransaction, List<BankStatements> bankStatementsList, Iterator<SystemTransaction> iteratorSystemTransaction) {
        for (BankStatements bankStatements : bankStatementsList) {
            Iterator<BankStatement> bankStatementIterator = bankStatements.statements().iterator();
            boolean isFound = findBankStatement(systemTransaction, bankStatementIterator, iteratorSystemTransaction);
            if (isFound) {
                return true;
            }
        }
        return false;
    }

    private static boolean findBankStatement(SystemTransaction systemTransaction, Iterator<BankStatement> iteratorStatement, Iterator<SystemTransaction> iteratorSystemTransaction) {
        while (iteratorStatement.hasNext()) {
            BankStatement statement = iteratorStatement.next();
            boolean isAmountEqual = Objects.equals(statement.amount(), systemTransaction.amount());
            boolean isTransactionTimeEquals = Objects.equals(systemTransaction.transactionTime(), statement.date());
            if (isAmountEqual && isTransactionTimeEquals) {
                iteratorStatement.remove();
                iteratorSystemTransaction.remove();
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
