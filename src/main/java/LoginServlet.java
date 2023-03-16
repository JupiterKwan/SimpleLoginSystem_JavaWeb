import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
            ;
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
        catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
            response.getWriter().print("<h2>" + username + "已被阻止登录！</h2>");
            response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
            response.setHeader("refresh", "3;url=login.html");

        } else {
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/simple_login_system", "postgres", "20029530");
                ;
                String sqlQuery = "SELECT * FROM users WHERE username=? AND pwd=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, pwd);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String[] detail = new String[resultSet.getMetaData().getColumnCount()];
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++){
                        detail[i - 1] = resultSet.getString(i);
                    }
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    response.getWriter().print("<h2>" + username + "，你好！</h2>");
                    String gender = (detail[2].equals("1") ? "男" : "女");
                    detail[6] = String.valueOf(Integer.parseInt(detail[6]) + 1);
                    response.getWriter().print("<h2>个人基本信息：</h2><ul><li>姓名：" + detail[0] + "</li><li>性别：" + gender + "</li><li>年龄：" + detail[3] + "</li><li>邮箱：" + detail[4] + "</li><li>注册时间：" + detail[5] + "</li><li>登录次数：" + detail[6] + "</li><li>上次登录时间：" + detail[7] + "</li><li>本次登录时间：" + date + "</li></ul>");
                    response.getWriter().print("<h2>其它信息：</h2><ul><li>请求方式：" + request.getMethod() + "</li><li>请求端ip地址：" + request.getRemoteAddr() + "</li>");

                    Enumeration<String> headers = request.getHeaderNames();
                    while (headers.hasMoreElements()) {
                        String value = request.getHeader(headers.nextElement());
                        System.out.println(headers.nextElement() + ":" + value);
                    }

                    detail[7] = String.valueOf(date);

                    String sqlUpdateUser = "UPDATE users SET accessfreq = ?, lasttime = ? WHERE username = ?";
                    preparedStatement = connection.prepareStatement(sqlUpdateUser);

                    preparedStatement.setInt(1, Integer.parseInt(detail[6]));
                    preparedStatement.setString(2, detail[7]);
                    preparedStatement.setString(3, username);
                    int result = preparedStatement.executeUpdate();

                }else {
                    response.getWriter().print("<h2>账号不存在或密码错误！</h2>");
                    response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
                    response.setHeader("refresh", "3;url=login.html");
                }

            } catch (SQLException | ClassNotFoundException | IOException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
