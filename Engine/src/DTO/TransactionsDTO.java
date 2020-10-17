package DTO;

import Models.Transaction;
import com.google.gson.annotations.Expose;

import java.util.List;

public class TransactionsDTO extends ResponseDTO {

    @Expose
    public List<Transaction> Transactions;

}