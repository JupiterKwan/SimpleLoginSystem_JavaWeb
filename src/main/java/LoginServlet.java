// Tomcat 10 + 引入包方式有所不同
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

// 自定义用户类
import com.user.User;


public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 用于获取请求端ip地址
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多次反向代理后会有多个IP值，第一个为真实IP。
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.indexOf(",") > 0) {
                return ip.substring(0, ip.indexOf(","));
            } else {
                return ip;
            }
        }
        return ip;
    }

    // 用于判断是否为黑名单用户
    private int IsBlockUser(String username) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/simple_login_system", "postgres", "20029530");
            String sqlQuery = "SELECT * FROM block_users WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取提交的表单的用户名和密码
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        //判断是否为黑名单用户
        int isBlockUser = IsBlockUser(username);
        if (isBlockUser == 1) {
            response.getWriter().print("<script language='javascript'>alert('黑名单用户拒绝登入！');window.location.href='login.jsp';</script>");
        } else {
            try {
                // 访问数据库获取登录用户的相关数据
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/simple_login_system", "postgres", "20029530");
                String sqlQuery = "SELECT * FROM users WHERE username=? AND pwd=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, pwd);
                ResultSet resultSet = preparedStatement.executeQuery();

                // 如果登录信息与数据库成功匹配
                if (resultSet.next()) {
                    // 如果存在session则获取，不存在则创建
                    HttpSession session = request.getSession();

                    // 提取数据库中获得的信息
                    String[] detail = new String[resultSet.getMetaData().getColumnCount()];
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        detail[i - 1] = resultSet.getString(i);
                    }

                    // 获取当前系统时间
                    Date date = new Date(System.currentTimeMillis());

                    // 为当前session设定属性值，用于存储该用户的相关信息
                    User user = new User(username, detail, String.valueOf(date), getIpAddr(request));

                    // 判断session中的属性值是否包含用户信息，如果包含则判断是否为当前用户的信息
                    if (session.getAttribute("user") != null) {
                        User getUser = (User) session.getAttribute("user");
                        String[] getDetail = getUser.getUserDetail();
                        if (!(getDetail[0].equals(detail[0]) && getDetail[1].equals(detail[1]))) {
                            response.getWriter().print("<script language='javascript'>alert('不允许同一设备登入多个账号！请检查账号或密码是否错误！');window.location.href='login.jsp';</script>");
                        } else {
                            // 判断是否为管理员界面，如果是则进入管理员界面，否则进入用户界面
                            if (username.equals("admin") && pwd.equals("123456")) {
                                request.getRequestDispatcher("admin_success.jsp").forward(request, response);
                            } else {
                                request.getRequestDispatcher("login_success.jsp").forward(request, response);
                            }
                        }
                    } else {
                        // session中不包含用户信息，注入用户信息属性
                        session.setAttribute("user", user);

                        // HTML请求报头内容输出至命令行
                        System.out.println("request headers: ");
                        Enumeration<String> headers = request.getHeaderNames();
                        while (headers.hasMoreElements()) {
                            String value = request.getHeader(headers.nextElement());
                            System.out.println(headers.nextElement() + ":" + value);
                        }
                        System.out.println("-------------------------------------------------------------");

                        // 刷新登录次数
                        detail[6] = String.valueOf(Integer.parseInt(detail[6]) + 1);

                        // 刷新服务器存储的登录次数和最后一次登陆时间数据
                        String sqlUpdateUser = "UPDATE users SET accessfreq = ?, lasttime = ? WHERE username = ?";
                        preparedStatement = connection.prepareStatement(sqlUpdateUser);
                        preparedStatement.setInt(1, Integer.parseInt(detail[6]));
                        preparedStatement.setString(2, String.valueOf(date));
                        preparedStatement.setString(3, username);
                        int result = preparedStatement.executeUpdate();

                        // 数据库用户信息刷新成功
                        if (result == 1) {
                            if (username.equals("admin") && pwd.equals("123456")) {
                                request.getRequestDispatcher("admin_success.jsp").forward(request, response);
                            } else {
                                request.getRequestDispatcher("login_success.jsp").forward(request, response);
                            }
                        } else {
                            System.out.println("Data update failed.");
                        }

                    }

                } else {
                    response.getWriter().print("<script language='javascript'>alert('账号不存在或密码错误！');window.location.href='login.jsp';</script>");
                }

            } catch (SQLException | ClassNotFoundException | IOException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
