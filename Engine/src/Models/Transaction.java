package Models;

import Enums.TransactionType;
import com.google.gson.annotations.Expose;

import java.util.Date;

public class Transaction {
    @Expose
    public TransactionType transactionType;
    @Expose
    public int sumOfTransaction;
    @Expose
    public int balanceBeforeAction;
    @Expose
    public int balanceAfterAction;
    @Expose
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