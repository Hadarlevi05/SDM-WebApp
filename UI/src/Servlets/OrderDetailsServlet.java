package Servlets;

        import DTO.KeyValueDTO;
        import Handlers.StoreHandler;
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
        urlPatterns = "/order_details"
)

public class OrderDetailsServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;
    private StoreHandler storeHandler;

    public OrderDetailsServlet() {
        superDuperHandler = new SuperDuperHandler();
        storeHandler = new StoreHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int orderID = new Integer(request.getParameter("orderID"));
        String area = request.getParameter("area");

        SdmUser usernameFromSession = SessionUtils.getUser(request);

        List<Map<String, Object>> rows = superDuperHandler.getOrderDetails(area, orderID);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}