package Servlets;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletUtils {

    public static void WriteToOutput(HttpServletResponse response, String msg) {
        try (PrintWriter out = response.getWriter()) {
            out.println(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
