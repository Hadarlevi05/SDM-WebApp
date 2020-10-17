package Servlets;

import DTO.KeyValueDTO;
import DTO.UsersDTO;
import DataStore.DataStore;
import Models.SdmUser;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        urlPatterns = "/users"
)

public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action.equals("users")) {
            getAllUsers(request, response);
        } else if (action.equals("currentUser")) {
            getCurrentUser(request, response);
        }
    }

    private void getCurrentUser(HttpServletRequest request, HttpServletResponse response) {

        SdmUser currentUserSession = SessionUtils.getUser(request);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("user", currentUserSession);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    protected void getAllUsers(HttpServletRequest request, HttpServletResponse response) {

        DataStore dataStore = DataStore.getInstance();

        UsersDTO usersDTO = new UsersDTO();
        usersDTO.Status = 200;
        usersDTO.Users = dataStore.userDataStore.list();

        ServletHelper.WriteToOutput(response, usersDTO);
    }
}
