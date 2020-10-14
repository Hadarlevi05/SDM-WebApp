package Servlets;


import DataStore.DataStore;
import Enums.UserType;
import Models.SdmUser;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet (
    urlPatterns = "/signup"
)

public class SignupServlet extends HttpServlet {
    private final String PAGE_2 = "Pages/page2/Page2.html";
    private final String SIGN_UP_URL = "Pages/loginPage/LoginPage.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataStore dataStore = DataStore.getInstance();

        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        if (usernameFromSession == null) {
            String username = request.getParameter("username");
            UserType userType = UserType.valueOf(request.getParameter("type"));

            if (username == null || username.isEmpty()) {
                response.sendRedirect(SIGN_UP_URL);
            } else {
                username = username.trim();
                synchronized (this) {
                    if (dataStore.userDataStore.get(username) != null) {
                      /*  if(userManager.isUserOnline(username)) {// isUserOnline
                            String errorMessage = "Username " + username + " already exists. Please enter a different username.";
                            request.setAttribute("loggedInAlready", errorMessage);
                            response.sendRedirect(SIGN_UP_URL);
                        }
                        else {
                            myMagit.loadUserData(userManager.getUserByName(username));
                            request.getSession(true).setAttribute("username", username);
                            userManager.getUserByName(username).setOnline(true);
                            response.sendRedirect(PAGE_2);
                        }*/
                    } else {
                        dataStore.userDataStore.create(new SdmUser(username, userType));
                        request.getSession(true).setAttribute("username", username);
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.sendRedirect(PAGE_2);
                    }
                }
            }
        }
    }

}
