<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.user.User" %>
<%@ page import="java.util.Date" %>
<html>
<head>
    <title>登陆成功</title>
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
    </style>
</head>
<body>
<%
    User user = (User) session.getAttribute("user");
    String[] detail = user.getUserDetail();
    String currentDate = String.valueOf(new Date(System.currentTimeMillis()));
    String date = user.getLoginTime();
    String gender = (detail[2].equals("man") ? "男" : "女");
%>
<h2 class="parent"><%=detail[0] + "，你好！"%>
</h2>

<h2 class="parent">个人基本信息</h2>
<div class="parent">
    <ul class="form">
        <%="<li>姓名：" + detail[0] + "</li><li>性别：" + gender + "</li><li>年龄：" + detail[3] + "</li><li>邮箱：" + detail[4] + "</li><li>注册时间：" + detail[5] + "</li><li>登录次数：" + detail[6] + "</li><li>上次登录时间：" + detail[7] + "</li><li>本次登录时间：" + date + "</li><li>页面静止时间：" + currentDate + "</li>"%>
    </ul>
</div>

<h2 class="parent">其他信息</h2>
<div class="parent">
    <ul class="form">
        <%="<li>请求方式：" + request.getMethod() + "</li><li>请求端ip地址：" + request.getRemoteAddr() + "</li><li>sessionID：" + session.getId() + "</li>"%>
    </ul>
</div>
<div class="parent"><a href="toLogout">登出</a></div>


</body>
</html>
