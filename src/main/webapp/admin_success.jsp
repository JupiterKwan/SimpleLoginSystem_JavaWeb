<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.user.User" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.MySessionContext.SessionRegistry" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.IOException" %>
<html>
<head>
    <title>管理员页面</title>
    <style>
        .parent {
            margin-top: 30px;
            width: 50%;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
            line-height: 50%;
            vertical-align: middle;
        }

        .form {
            align-items: start;
            justify-content: center;
            line-height: 180%;
            list-style-type: none;

        }

        .table {
            position: relative;
            left: 30px;
            justify-content: center;
            table-layout: fixed;
            border-color: darkcyan;
            border-collapse: collapse;
            border-spacing: 15px;
            white-space: nowrap;
        }
    </style>
</head>
<body>
<%
    User user = (User) session.getAttribute("user");
    String[] detail = user.getUserDetail();
    String currentDate = String.valueOf(new Date(System.currentTimeMillis()));
    String date = user.getLoginTime();
%>
<h2 class="parent"><%="管理员，你好！"%>
</h2>

<h2 class="parent">基本信息</h2>
<div class="parent">
    <ul class="form">
        <%="<li>登录次数：" + detail[6] + "</li><li>上次登录时间：" + detail[7] + "</li><li>本次登录时间：" + date + "</li><li>页面静止时间：" + currentDate + "</li>"%>
    </ul>
</div>

<h2 class="parent">其他信息</h2>
<div class="parent">
    <ul class="form">
        <%="<li>请求方式：" + request.getMethod() + "</li><li>请求端ip地址：" + request.getRemoteAddr() + "</li><li>sessionID：" + session.getId() + "</li>"%>
    </ul>
</div>
<div class="parent"><a href="toLogout">登出</a></div>

<h2 class="parent">用户数据列表</h2>
<div class="parent">
    <table class="table">
        <%
            try {
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                String userPath = path + "/user_data/data.txt";
                File userFile = new File(userPath);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(userFile));
                String lineData;

                String[] columnName = new String[]{"用户名", "密码", "性别", "年龄", "邮箱", "注册时间", "登录次数", "最后一次登陆时间"};
                out.print("<tr>");
                for (int i = 0; i < 8; i++) {
                    out.println("<td>");
                    out.println(columnName[i]);
                    out.println("</td>");
                }
                out.print("</tr>");
                while ((lineData = bufferedReader.readLine()) != null) {
                    out.print("<tr>");
                    String[] userDetail = lineData.split(",");
                    for (int i = 0; i < userDetail.length; i++) {
                        out.println("<td>");
                        out.println(userDetail[i]);
                        out.println("</td>");
                    }
                    out.print("</tr>");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        %>
    </table>

</div>


<h2 class="parent">黑名单用户名列表</h2>
<div class="parent">
    <table class="table">
        <%
            try {
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                String blockPath = path + "/user_data/blacklist.txt";
                File blacklistFile = new File(blockPath);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(blacklistFile));
                String lineData;

                out.print("<tr>");
                out.println("<td>");
                out.println("用户名");
                out.println("</td>");
                out.print("</tr>");
                while ((lineData = bufferedReader.readLine()) != null) {
                    out.print("<tr>");
                    String[] blacklistdetail = lineData.split(",");
                    for (int i = 0; i < blacklistdetail.length; i++) {
                        out.println("<td>");
                        out.println(blacklistdetail[i]);
                        out.println("</td>");
                    }
                    out.print("</tr>");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        %>
    </table>
</div>


<h2 class="parent">当前在线人数为：<%=this.getServletConfig().getServletContext().getAttribute("onlineCount")%>人
</h2>

<h2 class="parent">在线用户列表</h2>
<%
    List<HttpSession> allSessions = SessionRegistry.getAllSessions();
    String[] columnName = new String[]{"用户名", "性别", "年龄", "邮箱", "注册时间", "登录次数", "上一次登陆时间","本次登陆时间", "IP地址"};
    out.println("<div class=\"parent\"><table class=\"table\">");
    out.print("<tr>");
    for (int i = 0; i < 9; i++) {
        out.println("<td>");
        out.println(columnName[i]);
        out.println("</td>");
    }
    out.print("</tr>");
    for (HttpSession session2 : allSessions) {
        out.print("<tr>");
        User attributeValue = (User)session2.getAttribute("user");
        String[] loginDetail = attributeValue.getUserDetail();
        for (int i = 0; i < 8; i++) {
            if (i == 1){
                continue;
            }
            out.println("<td>");
            out.println(loginDetail[i]);
            out.println("</td>");
        }
        out.println("<td>");
        out.println(user.getLoginTime());
        out.println("</td>");
        out.println("<td>");
        out.println(user.getRemoteIP());
        out.println("</td>");
        out.print("</tr>");
    }
    out.println("</table></div>");
%>
</body>
</html>
