# Reconciliation Service

Java Springboot is a webservice to reconcile system transaction with bank statements by finding data discrepancies 

## Requirements
Prerequisite to run the program:

- JDK Java 21

## Run the webserver on a local machine
By default will start on port 8080
1. checkout to the repository
2. run the following command for bash on windows
```bash
 mvnw spring-boot:run
```
3. if you use command windows please use this
```bash
 ./mvnw spring-boot:run
```

## Usage
There ara one REST API that is `http://localhost:8080/reconcile-data` with POST Method
### Payload Example
```
{
  "bankStatements": ["src/test/resources/BankStatementSMBI.csv"], 
  "systemTransactionPath":"src/test/resources/SystemRrx001.csv",
  "startDate": "2024-07-10", //
  "endDate": "2024-07-14"
}
```
Payload content consist of followiing fields:

- `bankStatements`: list of path of bank statements csv file
- `systemTransactionPath` : path of system transaction csv file
- `startDate` : start date range of the data to be reconciled
- `endDate` : end date range of the data to be reconciled

### System Transaction CSV Format
```[trxId],[amount],[transactionTYpe],[transactionDate]```

Format of system transaction csv has 4 fields:
- trxId : transaction id in the system
- amount : amount in the transaction
- transactionType : transaction type in the transaction can be CREDIT or DEBIT
- transactionDate : transaction date in the transaction it has format `yyyy-MM-dd HH:mm:ss` e.g. `2024-07-27 21:30:40`

Example:

```125,454,CREDIT,2024-07-12 21:30:40```

### Bank Statement CSV Format
```[trxId],[amount],[transactionDate]```

Format of bank statement csv has 3 fields:
- trxId : statement id in the system
- amount : amount in the statement can be negative if debit
- transactionDate : transaction date in the statement it has format `yyyy-MM-dd HH:mm:ss` e.g. `2024-07-27 21:30:40`


Examples: ```3,-455.4,2024-07-12 19:30:40 ```


## Response of the API
following is the example of response of `reconcile-data` api
```json
{
  "totalTransactions": 4, 
  "totalMatchedTransaction": 2,
  "totalMisMatchedTransactions": 2,
  "totalDiscrepancy": 9120.7,
  "detailMismatchedTransaction": {
    "systemTransactions": [
      {
        "trxId": "123",
        "amount": 4555.3,
        "type": "CREDIT",
        "transactionTime": "2024-07-12T02:30:40.000+00:00"
      }
    ],
    "bankStatementsList": [
      {
        "filePath": "src/test/resources/BankStatementSMBI.csv",
        "statements": [
          {
            "unique_identifier": "2",
            "amount": 4565.4,
            "date": "2024-07-12T02:30:40.000+00:00"
          }
        ]
      }
    ]
  }
}
```

Specs of the responses are:
- `totalTransactions`: total transactions that being processed by the system and the bank
- `totalMatchedTransaction`: total transactions that exist in the bank statement and system
- `totalMisMatchedTransactions` : total transactions that exist only in the bank statement or only in system
- `totalDiscrepancy` : total amount of mismatched transactions
- `detailMismatchedTransaction` : detail of mismatched transactions either in the system or in the bank statements

## Thank you
