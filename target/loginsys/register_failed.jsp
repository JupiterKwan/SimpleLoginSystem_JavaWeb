<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session = "false" %>
<html>
<head>
    <title>注册失败</title>
    <style>
        .parent {
            margin-top: 80px;
            width: 50%;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
            line-height: 50%;
            vertical-align: middle;
        }
    </style>
</head>
<body>
<%String username = request.getParameter("username");%>
<h2 class="parent"><% response.getWriter().print(username + "已经注册过了！"); %></h2>
<h2 class="parent"><% response.getWriter().print("三秒后自动返回登陆页面"); %></h2>
<%response.setHeader("refresh", "3;url=login.jsp");%>
</body>
</html>
