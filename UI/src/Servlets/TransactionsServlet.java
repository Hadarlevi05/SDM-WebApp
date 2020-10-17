package Servlets;

import DTO.TransactionsDTO;
import DataStore.DataStore;
import Enums.TransactionType;
import Models.Transaction;
import Models.SdmUser;
import UIUtils.ServletHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet(
        urlPatterns = "/transactions"
)

public class TransactionsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        DataStore dataStore = DataStore.getInstance();

        String usernameFromQueryString = request.getParameter("username").trim();
        SdmUser user = dataStore.userDataStore.get(usernameFromQueryString);

        Transaction acc = new Transaction();
        acc.sumOfTransaction = Integer.parseInt(request.getParameter("sumOfTransaction"));

        List<Transaction> allTransactionsOfUser = dataStore.transactionsStore.get(user);

        if(allTransactionsOfUser.size() > 0){
            acc.balanceBeforeAction = allTransactionsOfUser.get(allTransactionsOfUser.size() - 1).balanceAfterAction;
        }
        else{
            acc.balanceBeforeAction = 0;
        }

        acc.balanceAfterAction =  acc.balanceBeforeAction + acc.sumOfTransaction;

        acc.transactionDate = new Date(System.currentTimeMillis()); // TODO
        acc.transactionType =  TransactionType.valueOf(request.getParameter("transactionType"));

        dataStore.transactionsStore.add(user, acc);
    }

        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataStore dataStore = DataStore.getInstance();

        //String username = request.getParameter("username").trim();
        //SdmUser user = SessionUtils.getUser(request);
        String usernameFromQueryString = request.getParameter("username").trim();
        SdmUser user = dataStore.userDataStore.get(usernameFromQueryString);

        if(user == null){
            throw new InternalError("user does not exists: " + usernameFromQueryString);
        }

        List<Transaction> lst = dataStore.transactionsStore.get(user);

        TransactionsDTO transactionsDTO = new TransactionsDTO();
        transactionsDTO.Status = 200;
        transactionsDTO.Transactions = lst;

        response.setContentType("text/html;charset=UTF-8");

        ServletHelper.WriteToOutput(response, transactionsDTO);
    }
}