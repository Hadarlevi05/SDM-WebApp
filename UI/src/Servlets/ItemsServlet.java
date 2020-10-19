
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
        urlPatterns = "/items"
)

public class ItemsServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;

    public ItemsServlet() {
        superDuperHandler = new SuperDuperHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String area = request.getParameter("area");

        List<Map<String, Object>> rows = superDuperHandler.getItemsDetails(area);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}
