package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.model.BankStatement;
import com.axorean.reconciliation_service.model.BankStatements;
import com.axorean.reconciliation_service.model.SystemTransaction;
import com.axorean.reconciliation_service.model.TransactionType;
import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static com.axorean.reconciliation_service.util.Constant.DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {
    @Mock
    private BankStatementService bankStatementService;
    @Mock
    private SystemTransactionService systemTransactionService;
    @Mock
    private ExecutorService executorService;
    @InjectMocks
    private ReconciliationService reconciliationService;
    @Captor
    private ArgumentCaptor<Callable<BankStatements>> callableBankStatementArgumentCaptor;
    @Captor
    private ArgumentCaptor<Callable<List<SystemTransaction>>> callableListSystemTransactionArgumentCaptor;

    @Test
    void reconcileData_shouldReturn2SystemTransactionAnd1BankStatements_whenThereIs5SystemTransaction4BankStatementThatConsistOf1MismatchedAnd3Matched() throws ParseException, ExecutionException, InterruptedException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate = simpleDateFormat.parse("2024-07-10 00:00:00");
        Date endDate = simpleDateFormat.parse("2024-07-15 23:59:59");
        Date transactionDate1 = simpleDateFormat.parse("2024-07-10 01:00:00");
        Date transactionDate2 = simpleDateFormat.parse("2024-07-10 02:00:00");
        Date transactionDate3 = simpleDateFormat.parse("2024-07-10 03:00:00");
        Date transactionDate4 = simpleDateFormat.parse("2024-07-10 04:00:00");
        Date transactionDate5 = simpleDateFormat.parse("2024-07-10 05:00:00");
        String[] bankStatements = {"src/test/resources/BankStatementSMBB.csv", "src/test/resources/BankStatementSMBA.csv", "src/test/resources/BankStatementSMBD.csv"};
        SystemTransaction systemTransaction1 = new SystemTransaction("1232", 123.0, TransactionType.CREDIT, transactionDate1);
        SystemTransaction systemTransaction2 = new SystemTransaction("1233", 123213.0, TransactionType.CREDIT, transactionDate2);
        SystemTransaction systemTransaction3 = new SystemTransaction("1234", 12383.3, TransactionType.CREDIT, transactionDate3);
        SystemTransaction systemTransaction4 = new SystemTransaction("1235", -1279813.3, TransactionType.DEBIT, transactionDate4);
        SystemTransaction systemTransaction5 = new SystemTransaction("1236", 123813.0, TransactionType.CREDIT, transactionDate5);
        List<SystemTransaction> systemTransactions = new ArrayList<>(Arrays.asList(systemTransaction1, systemTransaction2, systemTransaction3, systemTransaction4, systemTransaction5));
        ArrayList<BankStatement> statementsSMBB = new ArrayList<>();
        ArrayList<BankStatement> statementsSMBA = new ArrayList<>();
        ArrayList<BankStatement> statementsSMBD = new ArrayList<>();
        BankStatement bankStatement1 = new BankStatement("1232", 123.0, transactionDate1);
        BankStatement bankStatement2 = new BankStatement("1233", 12213.0, transactionDate2);
        BankStatement bankStatement3 = new BankStatement("1234", -1279813.3, transactionDate4);
        BankStatement bankStatement4 = new BankStatement("212", 123813.0, transactionDate5);
        statementsSMBB.add(bankStatement1);
        statementsSMBB.add(bankStatement4);
        statementsSMBA.add(bankStatement2);
        statementsSMBD.add(bankStatement3);
        BankStatements bankStatementsSMBB = new BankStatements(bankStatements[0], statementsSMBB);
        BankStatements bankStatementsSMBA = new BankStatements(bankStatements[1], statementsSMBA);
        BankStatements bankStatementsSMBD = new BankStatements(bankStatements[2], statementsSMBD);
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder()
                .systemTransactionPath("src/test/resources/SystemRrx004.csv")
                .bankStatements(bankStatements)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        CompletableFuture<List<SystemTransaction>> readSystemFuture = CompletableFuture.completedFuture(systemTransactions);
        CompletableFuture<BankStatements> futureSMBB = CompletableFuture.completedFuture(bankStatementsSMBB);
        CompletableFuture<BankStatements> futureSMBA = CompletableFuture.completedFuture(bankStatementsSMBA);
        CompletableFuture<BankStatements> futureSMBD = CompletableFuture.completedFuture(bankStatementsSMBD);
        when(executorService.submit(any(Callable.class))).thenReturn(readSystemFuture).thenReturn(futureSMBB).thenReturn(futureSMBA).thenReturn(futureSMBD);;

        ReconciliationResponse reconciliationResponse = this.reconciliationService.reconcileData(reconciliationRequest);

        verify(this.executorService).submit(this.callableBankStatementArgumentCaptor.capture());
        verify(this.executorService).submit(this.callableListSystemTransactionArgumentCaptor.capture());
    }
}