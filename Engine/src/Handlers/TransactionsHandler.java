package Handlers;

import DTO.ResponseDTO;
import DataStore.DataStore;
import Enums.TransactionType;
import Models.SdmUser;
import Models.Transaction;

import java.util.Date;
import java.util.List;

public class TransactionsHandler {

    public void doTransaction(String username, double sumOfTransaction, String transactionType) {
        DataStore dataStore = DataStore.getInstance();
        Transaction acc = new Transaction();

        acc.sumOfTransaction = (int) sumOfTransaction; // TODO: Double instead of int
        List<Transaction> allTransactionsOfUser = dataStore.transactionsStore.get(username);

        if (allTransactionsOfUser.size() > 0) {
            acc.balanceBeforeAction = allTransactionsOfUser.get(allTransactionsOfUser.size() - 1).balanceAfterAction;
        } else {
            acc.balanceBeforeAction = 0;
        }

        acc.balanceAfterAction = acc.balanceBeforeAction + acc.sumOfTransaction;

        acc.transactionDate = new Date(System.currentTimeMillis()); // TODO
        acc.transactionType = TransactionType.valueOf((transactionType));

        dataStore.transactionsStore.add(username, acc);
    }
}