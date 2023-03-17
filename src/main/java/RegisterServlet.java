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

        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String userPath = path + "/user_data/data.txt";
            File txtFile = new File(userPath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile, true));
            String lineData;
            boolean isUserExist = false;
            while ((lineData = bufferedReader.readLine()) != null) {
                String[] detail = lineData.split(",");
                if (detail[0].equals(username)) {
                    isUserExist = true;
                    bufferedReader.close();
                    bufferedWriter.close();
                    request.getRequestDispatcher("register_failed.jsp").forward(request, response);
                    break;
                }
            }
            if (!isUserExist) {
                bufferedWriter.newLine();
                bufferedWriter.write(username + "," + pwd + "," + gender + "," + age + "," + email + "," + timestamp + ",0," + timestamp);
                bufferedReader.close();
                bufferedWriter.close();
                request.getRequestDispatcher("register_success.jsp").forward(request, response);
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }

    }
}

