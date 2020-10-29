
package Servlets;

import DTO.KeyValueDTO;
import DataStore.DataStore;
import Enums.UserType;
import Handlers.SuperDuperHandler;
import Models.SdmUser;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(
        urlPatterns = "/order-history"
)

public class OrdersHistoryServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;

    public OrdersHistoryServlet() {
        superDuperHandler = new SuperDuperHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String area = request.getParameter("area");
        SdmUser usernameFromSession = SessionUtils.getUser(request);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (usernameFromSession.userType == UserType.CUSTOMER) {
            rows = superDuperHandler.getOrdersHistoryDetailsForCustomer(area, usernameFromSession.id);

        } else if (usernameFromSession.userType == UserType.STORE_OWNER) {
            rows = superDuperHandler.getOrdersHistoryDetailsForStoreOwner(area, usernameFromSession);

        }

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}