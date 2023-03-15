import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Date timestamp = new Date(System.currentTimeMillis());

        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        String gender = request.getParameter("gender");
        String age = request.getParameter("age");
        String email = request.getParameter("email");

        try {
            String txtPath = "D:\\00_code\\08_WebApp_LoginSys\\loginsys\\src\\main\\user_data\\data.txt";
            File txtFile = new File(txtPath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile, true));
            String lineData;
            boolean isUserExist = false;
            while ((lineData = bufferedReader.readLine()) != null) {
                String[] detail = lineData.split(",");
                if (detail[0].equals(username)) {
                    response.getWriter().print("<h2>" + username + "已经注册过了！</h2>");
                    isUserExist = true;
                    break;
                }
            }

            if (!isUserExist) {
                bufferedWriter.newLine();
                bufferedWriter.write(username + "," + pwd + "," + gender + "," + age + "," + email + "," + timestamp + ",0," + timestamp);
                bufferedReader.close();
                bufferedWriter.close();
                response.getWriter().print("<h2>" + username + "注册成功！</h2>");
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

