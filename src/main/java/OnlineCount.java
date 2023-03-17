import com.MySessionContext.SessionRegistry;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import javax.sound.sampled.AudioFormat;
import java.util.HashMap;

public class OnlineCount implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        SessionRegistry.addSession(se.getSession());

        ServletContext servletContext = se.getSession().getServletContext();
        Integer onlineCount = (Integer) servletContext.getAttribute("onlineCount");

        if (onlineCount == null) {
            onlineCount = 1;
        } else {
            int count = onlineCount.intValue();
            onlineCount = ++count;
        }
        servletContext.setAttribute("onlineCount", onlineCount);
        System.out.println("add 1 session");
        System.out.println("session ID: " + se.getSession().getId());
        System.out.println("online: " + onlineCount);
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        SessionRegistry.removeSession(session);
        ServletContext servletContext = se.getSession().getServletContext();
        Integer onlineCount = (Integer) servletContext.getAttribute("onlineCount");
        if (onlineCount == null) {
            onlineCount = 0;
        } else {
            int count = onlineCount.intValue();
            onlineCount = --count;
        }
        servletContext.setAttribute("onlineCount", onlineCount);
        System.out.println("delete 1 session");
        System.out.println("session ID: " + se.getSession().getId());
        System.out.println("online: " + onlineCount);
        System.out.println("-------------------------------------------------------------");
    }
}
