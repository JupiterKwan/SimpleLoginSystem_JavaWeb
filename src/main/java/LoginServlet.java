import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;


public class LoginServlet extends HttpServlet {
    private int IsBlockUser(String username) {
        try {
            String blacklistPath = "D:\\00_code\\08_WebApp_LoginSys\\loginsys\\src\\main\\user_data\\blacklist.txt";
            File blacklistFile = new File(blacklistPath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(blacklistFile));
            String lineData;
            while ((lineData = bufferedReader.readLine()) != null) {
                if (lineData.contains(username)) {
                    return 1;
                }
            }
            return 0;
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
            return -1;
        } catch (IOException e) {
            System.out.println("文件读写出错");
            return -1;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        int isBlockUser = IsBlockUser(username);
        if (isBlockUser == -1) {
            System.out.println("系统内部错误！");
            response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
            response.setHeader("refresh", "3;url=login.html");
        } else if (isBlockUser == 1) {
            response.getWriter().print("<h2>" + username + "已被阻止登录！</h2>");
            response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
            response.setHeader("refresh", "3;url=login.html");
        } else {
            try {
                String userPath = "D:\\00_code\\08_WebApp_LoginSys\\loginsys\\src\\main\\user_data\\data.txt";
                String bufferPath = "D:\\00_code\\08_WebApp_LoginSys\\loginsys\\src\\main\\user_data\\data_buffer.txt";
                File txtFile = new File(userPath);
                File bufferFile = new File(bufferPath);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter((bufferFile), false));
                String lineData;
                boolean isUserExist = false;

                while ((lineData = bufferedReader.readLine()) != null) {
                    String[] detail = lineData.split(",");
                    if (detail[0].equals(username)) {
                        if (detail[1].equals(pwd)) {
                            Date date = new Date(System.currentTimeMillis());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                            isUserExist = true;
                            response.getWriter().print("<h2>" + username + "，你好！</h2>");
                            String gender = (detail[2].equals("man") ? "男" : "女");
                            detail[6] = String.valueOf(Integer.parseInt(detail[6]) + 1);
                            response.getWriter().print("<h2>个人基本信息：</h2><ul><li>姓名：" + detail[0] + "</li><li>性别：" + gender + "</li><li>年龄：" + detail[3] + "</li><li>邮箱：" + detail[4] + "</li><li>注册时间：" + detail[5] + "</li><li>登录次数：" + detail[6] + "</li><li>上次登录时间：" + detail[7] + "</li><li>本次登录时间：" + date + "</li></ul>");

                            response.getWriter().print("<h2>其它信息：</h2><ul><li>请求方式：" + request.getMethod() + "</li><li>请求端ip地址：" + request.getRemoteAddr() + "</li>");

                            Enumeration<String> headers = request.getHeaderNames();
                            while (headers.hasMoreElements()) {
                                String value = request.getHeader(headers.nextElement());
                                System.out.println(headers.nextElement() + ":" + value);
                            }

                            detail[7] = String.valueOf(date);
                            String updateData = detail[0] + "," + detail[1] + "," + detail[2] + "," + detail[3] + "," + detail[4] + "," + detail[5] + "," + detail[6] + "," + detail[7];
                            bufferedWriter.write(updateData);
                            bufferedWriter.newLine();
                        } else {
                            response.getWriter().print("<h2>账号或密码错误！</h2>");
                            response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
                            response.setHeader("refresh", "3;url=login.html");
                        }
                    } else {
                        bufferedWriter.write(lineData);
                        bufferedWriter.newLine();
                    }
                }
                bufferedReader.close();
                bufferedWriter.close();
                txtFile.delete();
                bufferFile.renameTo(new File(userPath));

                if (!isUserExist) {
                    response.getWriter().print("<h2>系统未找到" + username + "的信息！</h2>");
                    response.getWriter().print("<h2>三秒后自动返回登陆页面</h2>");
                    response.setHeader("refresh", "3;url=login.html");
                }
            } catch (FileNotFoundException e) {
                System.out.println("没有找到指定文件");
            } catch (IOException e) {
                System.out.println("文件读写出错");
            }
        }
    }
}
