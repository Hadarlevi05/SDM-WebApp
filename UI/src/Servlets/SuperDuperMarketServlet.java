package Servlets;

import DTO.KeyValueDTO;
import Handlers.SuperDuperHandler;
import Models.SdmUser;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(
        urlPatterns = "/superdupermarket"
)

public class SuperDuperMarketServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;

    public SuperDuperMarketServlet() {
        superDuperHandler = new SuperDuperHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action.equals("allUserConfig")) {
            getAllUserConfig(request, response);
        } else if (action.equals("currentUser")) {

        }
    }

    private void getAllUserConfig(HttpServletRequest request, HttpServletResponse response) {

        SdmUser currentUserSession = SessionUtils.getUser(request);

        List<Map<String, Object>> rows = superDuperHandler.getStoreAreaDetails(currentUserSession.username);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

}
