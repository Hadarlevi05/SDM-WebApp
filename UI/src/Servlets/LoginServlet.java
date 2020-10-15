package Servlets;

import DataStore.DataStore;
import Models.SdmUser;
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

    private final String BaseUrl = "Pages/";

    private final String StoreListPage = BaseUrl + "store/list.html";
    private final String loginUrl = BaseUrl + "login/login.html";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String msg = "";

        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) {

            String username = request.getParameter("username");

            if (username == null || username.isEmpty()) {
                String errorMessage = "Username cannot be empty.";
                msg = "{ \"status\": 400, \"errorMessage\": \"" + errorMessage + "\", \"redirectUrl\": \"" + "" + "\" }";
                ServletUtils.WriteToOutput(response, msg);
                //response.sendRedirect(loginUrl);
                return;
            }

            DataStore dataStore = DataStore.getInstance();

            username = username.trim();
            SdmUser user = dataStore.userDataStore.get(username);

            if (user == null) {

                String errorMessage = "Username '" + username + "' doesn't exists.";
                msg = "{ \"status\": 400, \"errorMessage\": \"" + errorMessage + "\", \"redirectUrl\": \"" + "" + "\" }";

            } else {
                msg = "{ \"status\": 200, \"errorMessage\": \"" + "" + "\", \"redirectUrl\": \"" + StoreListPage + "\" }";

            }
            ServletUtils.WriteToOutput(response, msg);
        }


    }
}
