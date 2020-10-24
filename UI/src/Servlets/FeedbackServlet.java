package Servlets;

        import DTO.InnerJSON;
        import DTO.KeyValueDTO;
        import DTO.OrderData;
        import DataStore.DataStore;
        import Enums.OrderStatus;
        import Handlers.*;
        import Models.*;
        import UIUtils.ServletHelper;
        import UIUtils.SessionUtils;
        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.JsonElement;
        import com.google.gson.reflect.TypeToken;

        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import javax.swing.*;
        import java.beans.BeanInfo;
        import java.beans.Introspector;
        import java.beans.PropertyDescriptor;
        import java.io.BufferedReader;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.ObjectInputStream;
        import java.lang.reflect.Method;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.*;
        import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = "/feedbacks"
)

public class FeedbackServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;

    public FeedbackServlet() {
        superDuperHandler = new SuperDuperHandler();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();

        Feedback[] feedback = gson.fromJson(jb.toString(), Feedback[].class);
        String area = request.getParameter("area");
        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);
        storeOwner.feedbacks.addAll(Arrays.asList(feedback));

        //int orderID = new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("orderID")).collect(Collectors.toList()).get(0).value);
        //OrderStatus status = OrderStatus.values()[new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("Status")).collect(Collectors.toList()).get(0).value)];

        // Order order = null;

        // TODO: Fix Feedback
        //new NotificationsHandler().Add(order.id, NotificationType.Feedback, storeOwner.username);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        //keyValueDTO.Values.put("Order", order);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String area = request.getParameter("area");
        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);

        List<Map<String, Object>> rows = new ArrayList();
        for (Feedback feedback :
                storeOwner.feedbacks) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", feedback.username);
            map.put("date", feedback.date);
            map.put("rate",feedback.rate);
            map.put("message",feedback.message);

            rows.add(map);
        }
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}
