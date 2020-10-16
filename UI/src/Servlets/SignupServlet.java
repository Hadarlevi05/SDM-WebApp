package Servlets;

import DTO.ResponseDTO;
import DataStore.DataStore;
import Enums.UserType;
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
    urlPatterns = "/signup"
)

public class SignupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ResponseDTO responseDTOJson = new ResponseDTO();
        responseDTOJson.Status = 200;

        response.setContentType("text/html;charset=UTF-8");
        SdmUser usernameFromSession = SessionUtils.getUser(request);

        if (usernameFromSession == null) {

            String username = request.getParameter("username");
            UserType userType = UserType.valueOf(request.getParameter("type"));

            if (username == null || username.isEmpty()) {

                responseDTOJson.Status = 400;
                responseDTOJson.ErrorMessage = "Username cannot be empty.";

                ServletHelper.WriteToOutput(response, responseDTOJson);
                return;
            }
            DataStore dataStore = DataStore.getInstance();

            username = username.trim();
            SdmUser user = dataStore.userDataStore.get(username);

            if (user != null) {
                responseDTOJson.Status = 400;
                responseDTOJson.ErrorMessage = "Username '" + username + "' already exists.";
            } else {
                user = new SdmUser(username, userType);
                dataStore.userDataStore.create(user);
                responseDTOJson.RedirectUrl = ServletHelper.StoreListPage;
                SessionUtils.setUser(request, user);
            }
            ServletHelper.WriteToOutput(response, responseDTOJson);
        }
    }

}