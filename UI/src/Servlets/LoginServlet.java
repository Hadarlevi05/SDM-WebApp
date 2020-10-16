package Servlets;

import DTO.ResponseDTO;
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
        urlPatterns = "/login"
)

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SdmUser user = null;
        boolean success = true;

        String usernameFromQueryString = request.getParameter("username").trim();
        SdmUser usernameFromSession = SessionUtils.getUser(request);

        ResponseDTO responseDTOJson = new ResponseDTO();
        responseDTOJson.Status = 200;

        response.setContentType("text/html;charset=UTF-8");

        if (usernameFromSession == null) {

            if (usernameFromQueryString == null || usernameFromQueryString.isEmpty()) {
                responseDTOJson.Status = 400;
                responseDTOJson.ErrorMessage =  "Username cannot be empty.";
                ServletHelper.WriteToOutput(response, responseDTOJson);
                return;
            }

            DataStore dataStore = DataStore.getInstance();
            user = dataStore.userDataStore.get(usernameFromQueryString);

            if (user == null) {

                responseDTOJson.Status = 400;
                responseDTOJson.ErrorMessage =  "Username '" + usernameFromQueryString + "' doesn't exists.";
                success = false;
            }
        }

        if (success) {
            responseDTOJson.RedirectUrl = ServletHelper.StoreListPage;

            SessionUtils.setUser(request, user);
        }
        ServletHelper.WriteToOutput(response, responseDTOJson);
    }
}
