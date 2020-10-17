package Models;

import Enums.TransactionType;

import java.util.Date;

public class Transaction {
    public TransactionType transactionType;
    public int sumOfTransaction;
    public int balanceBeforeAction;
    public int balanceAfterAction;
    public Date transactionDate;

    public Transaction(){
    };

    public Transaction(TransactionType transactionType, Date transactionDate, int sumOfTransaction, int balanceBeforeAction, int balanceAfterAction ) {
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.sumOfTransaction = sumOfTransaction;
        this.balanceBeforeAction = balanceBeforeAction;
        this.balanceAfterAction = balanceAfterAction;
    }
}