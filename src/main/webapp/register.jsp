<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page session = "false" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录</title>
    <style>
        .parent {
            position: relative;
            top: 50px;
            left: 50px;
        }
    </style>
</head>
<body>
<div class="parent">
    <h2>用户注册</h2>
    <div>
        <form action="toRegister" method="post">
            <table>
                <tr>
                    <td>
                        用户名:
                    </td>
                    <td>
                        <input type="text" name="username">
                    </td>
                </tr>
                <tr>
                    <td>
                        密码:
                    </td>
                    <td>
                        <input type="password" name="pwd">
                    </td>
                </tr>
                <tr>
                    <td>
                        性别
                    </td>
                    <td>
                        <input type="radio" name="gender" value="man">男
                        <input type="radio" name="gender" value="woman">女
                    </td>
                </tr>
                <tr>
                    <td>
                        年龄
                    </td>
                    <td>
                        <input type="text" name="age">
                    </td>
                </tr>
                <tr>
                    <td>
                        Email
                    </td>
                    <td>
                        <input type="text" name="email">
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" value="注册">
                        <input type="reset" value="重置">
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div><a href="login.jsp">已经注册？点击登录</a></div>
</div>

</body>
</html>