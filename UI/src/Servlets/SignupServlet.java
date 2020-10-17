package Servlets;

import DTO.KeyValueDTO;
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

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;

        response.setContentType("text/html;charset=UTF-8");
        SdmUser usernameFromSession = SessionUtils.getUser(request);

        if (usernameFromSession == null) {

            String username = request.getParameter("username");
            UserType userType = UserType.valueOf(request.getParameter("type"));

            if (username == null || username.isEmpty()) {

                keyValueDTO.Status = 400;
                keyValueDTO.ErrorMessage = "Username cannot be empty.";

                ServletHelper.WriteToOutput(response, keyValueDTO);
                return;
            }
            DataStore dataStore = DataStore.getInstance();

            username = username.trim();
            SdmUser user = dataStore.userDataStore.get(username);

            if (user != null) {
                keyValueDTO.Status = 400;
                keyValueDTO.ErrorMessage = "Username '" + username + "' already exists.";
            } else {
                user = new SdmUser(username, userType);
                dataStore.userDataStore.create(user);

                keyValueDTO.RedirectUrl = ServletHelper.StoreListPage;
                keyValueDTO.Values.put("User", user);

                SessionUtils.setUser(request, user);
            }
            ServletHelper.WriteToOutput(response, keyValueDTO);
        }
    }

}