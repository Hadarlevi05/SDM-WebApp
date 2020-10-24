package Servlets;
        import DTO.KeyValueDTO;
        import Handlers.NotificationsHandler;
        import Models.*;
        import UIUtils.ServletHelper;
        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.util.*;

@WebServlet(
        urlPatterns = "/notifications"
)

public class NotificationsServlet extends HttpServlet {

    public NotificationsServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        List<String> rows = new NotificationsHandler().GetNotificationsMessages(username);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    /*
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String itemType = request.getParameter("itemType");
        Integer itemId = Integer.parseInt(request.getParameter("itemId"));

        new NotificationsHandler().Add(itemId,itemType,username);
    }
    */
}