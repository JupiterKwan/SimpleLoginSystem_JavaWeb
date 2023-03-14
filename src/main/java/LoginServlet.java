import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

public class LoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        try {
            String txtPath = "D:\\00_code\\08_WebApp_LoginSys\\loginsys\\src\\main\\user_data\\data.txt";
            File txtFile = new File(txtPath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile, true));
            String lineData;
            boolean isUserExist = false;
            while ((lineData = bufferedReader.readLine()) != null) {
                if (lineData.contains(username)) {
                    response.getWriter().print("<h2>" + username + "已经注册过了！</h2>");
                    isUserExist = true;
                    break;
                }
            }

            if (!isUserExist) {

            }
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }



        response.getWriter().print("<h2>Hello world!</h2>");
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取系统时间，记录为登陆时间
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        System.out.println(formatter.format(date));

        Enumeration<String> headers = request.getHeaderNames();
        while(headers.hasMoreElements()){
            String value = request.getHeader(headers.nextElement());
            System.out.println(headers.nextElement() + ":" + value);
        }


        System.out.println(username + " " + pwd);
        response.getWriter().print("<h2>你好！" + username + "</h2>");


    }
}
