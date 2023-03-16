import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;

import java.io.*;
import java.util.Date;
import java.lang.*;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Date timestamp = new Date(System.currentTimeMillis());
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        String gender = request.getParameter("gender");
        int age = Integer.parseInt(request.getParameter("age"));
        String email = request.getParameter("email");

        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/simple_login_system", "postgres", "20029530");
            String sqlQuery = "SELECT * FROM users WHERE username=?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                response.getWriter().print("<h2>" + username + "已经注册过了！</h2>");
            } else {
                String sqlAddUser = "INSERT INTO users(username,pwd,gender,age,email,registime,lasttime) VALUES(?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(sqlAddUser);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, pwd);
                int sex = gender.equals("man") ? 1 : 0;
                String date = String.valueOf(timestamp);
                preparedStatement.setInt(3, sex);
                preparedStatement.setInt(4, age);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, date);
                preparedStatement.setString(7, date);
                int result = preparedStatement.executeUpdate();
                if (result == 1) {
                    response.getWriter().print("<h2>" + username + "注册成功！</h2>");
                    response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
                    response.setHeader("refresh", "3;url=login.html");
                }

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

