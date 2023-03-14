import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

public class LoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().print("<h2>Hello world!</h2>");
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取系统时间，记录为登陆时间
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        System.out.println(formatter.format(date));

        Enumeration<String> headers = request.getHeaderNames();
        while(headers.hasMoreElements()){
            String value = request.getHeader(headers.nextElement());
            System.out.println(headers.nextElement() + ":" + value);
        }

        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        System.out.println(username + " " + pwd);
        response.getWriter().print("<h2>你好！" + username + "</h2>");
    }
}
