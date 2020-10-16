package UIUtils;

import Models.SdmUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static SdmUser getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object sessionAttributeUser = session != null ? session.getAttribute("user") : null;

        return sessionAttributeUser != null ? (SdmUser) sessionAttributeUser : null;
    }


    public static void setUser(HttpServletRequest request, SdmUser user) {
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }

    public static void clearCurrentUser (HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("user", null);
    }

}