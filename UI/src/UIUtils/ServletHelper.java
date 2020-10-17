package UIUtils;

import DTO.ResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletHelper {

    public static final String BaseUrl = "http://localhost:8080/UI_Web_exploded/Pages/";

    public static final String StoreListPage = BaseUrl + "store/list.html";
    public static final String LoginPage = BaseUrl + "login/login.html";

    public static void WriteToOutput(HttpServletResponse response, ResponseDTO responseDTO) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        String jsonString = gson.toJson(responseDTO);

        try (PrintWriter out = response.getWriter()) {
            out.println(jsonString);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
