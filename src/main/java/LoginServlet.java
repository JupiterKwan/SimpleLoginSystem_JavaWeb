import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginServlet extends HttpServlet {

    private int IsBlockUser(String username) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/simple_login_system", "postgres", "20029530");
            String sqlQuery = "SELECT * FROM block_users WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return 1;
            } else {
                return 0;
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        int isBlockUser = IsBlockUser(username);
        if (isBlockUser == 1) {
            response.getWriter().print("<script language='javascript'>alert('黑名单用户拒绝登入！');window.location.href='login.jsp';</script>");
        } else {
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/simple_login_system", "postgres", "20029530");
                String sqlQuery = "SELECT * FROM users WHERE username=? AND pwd=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, pwd);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    HttpSession session = request.getSession();
                    String[] detail = new String[resultSet.getMetaData().getColumnCount()];
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++){
                        detail[i - 1] = resultSet.getString(i);
                    }
                    Date date = new Date(System.currentTimeMillis());
                    session.setAttribute("detail", detail);
                    session.setAttribute("date", date);

                    Enumeration<String> headers = request.getHeaderNames();
                    while (headers.hasMoreElements()) {
                        String value = request.getHeader(headers.nextElement());
                        System.out.println(headers.nextElement() + ":" + value);
                    }

                    detail[6] = String.valueOf(Integer.parseInt(detail[6]) + 1);

                    String sqlUpdateUser = "UPDATE users SET accessfreq = ?, lasttime = ? WHERE username = ?";
                    preparedStatement = connection.prepareStatement(sqlUpdateUser);
                    preparedStatement.setInt(1, Integer.parseInt(detail[6]));
                    preparedStatement.setString(2, String.valueOf(date));
                    preparedStatement.setString(3, username);
                    int result = preparedStatement.executeUpdate();
                    if (result == 1) {
                        request.getRequestDispatcher("login_success.jsp").forward(request, response);
                    } else {
                        System.out.println("Data update failed.");
                    }

                }else {
                    response.getWriter().print("<script language='javascript'>alert('账号不存在或密码错误！');window.location.href='login.jsp';</script>");
                }

            } catch (SQLException | ClassNotFoundException | IOException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
