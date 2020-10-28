package Servlets;

import DTO.KeyValueDTO;
import DTO.ResponseDTO;
import DTO.TransactionsDTO;
import DataStore.DataStore;
import Enums.TransactionType;
import Handlers.TransactionsHandler;
import Models.Transaction;
import Models.SdmUser;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(
        urlPatterns = "/transactions"
)

public class TransactionsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ResponseDTO responseDTO = new ResponseDTO();

        try {
            SdmUser user = SessionUtils.getUser(request);
            int sumOfTransaction = Integer.parseInt(request.getParameter("sumOfTransaction"));

            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));

            new TransactionsHandler().doTransaction(user.username, sumOfTransaction, TransactionType.CHARGE_MONEY.toString(),date);
            responseDTO.Status = 200;
        } catch (ParseException e) {
            responseDTO.Status = 500;
            responseDTO.ErrorMessage = "Invalid date";
        }

        response.setContentType("text/html;charset=UTF-8");

        ServletHelper.WriteToOutput(response, responseDTO);
    }

        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataStore dataStore = DataStore.getInstance();

        SdmUser user = SessionUtils.getUser(request);

        if(user == null){
            throw new InternalError("user does not have logged in session");
        }

        List<Transaction> lst = dataStore.transactionsStore.get(user.username);

        TransactionsDTO transactionsDTO = new TransactionsDTO();
        transactionsDTO.Status = 200;
        transactionsDTO.Transactions = lst;

        response.setContentType("text/html;charset=UTF-8");

        ServletHelper.WriteToOutput(response, transactionsDTO);
    }
}