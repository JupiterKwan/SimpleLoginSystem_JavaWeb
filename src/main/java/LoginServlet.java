import com.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.Enumeration;


public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

    private int IsBlockUser(String username) {
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            File blacklistFile = new File(path + "/user_data/blacklist.txt");
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
        if (isBlockUser == 1) {
            response.getWriter().print("<script language='javascript'>alert('黑名单用户拒绝登入！');window.location.href='login.jsp';</script>");
        } else {
            try {
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                String userPath = path + "/user_data/data.txt";
                String bufferPath = path + "/user_data/data_buffer.txt";
                File userFile = new File(userPath);
                File bufferFile = new File(bufferPath);
                BufferedReader bufferedReader = new BufferedReader(new FileReader(userFile));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter((bufferFile), false));
                String lineData;
                boolean isUserExist = false;
                int status = -1;

                while ((lineData = bufferedReader.readLine()) != null) {
                    String[] detail = lineData.split(",");
                    if (detail[0].equals(username)) {
                        if (detail[1].equals(pwd)) {
                            HttpSession session = request.getSession();
                            // 获取当前系统时间
                            Date date = new Date(System.currentTimeMillis());

                            // 为当前session设定属性值，用于存储该用户的相关信息
                            User user = new User(username, detail, String.valueOf(date), getIpAddr(request));
                            if (session.getAttribute("user") != null) {
                                User getUser = (User) session.getAttribute("user");
                                String[] getDetail = getUser.getUserDetail();
                                if (!(getDetail[0].equals(detail[0]) && getDetail[1].equals(detail[1]))) {
                                    bufferedWriter.write(lineData);
                                    bufferedWriter.newLine();
                                    status = 1;
                                } else {
                                    if (username.equals("admin") && pwd.equals("123456")) {
                                        bufferedWriter.write(lineData);
                                        bufferedWriter.newLine();
                                        status = 2;

                                    } else {
                                        bufferedWriter.write(lineData);
                                        bufferedWriter.newLine();
                                        status = 3;
                                    }
                                }
                            } else {
                                session.setAttribute("user", user);

                                // HTML请求报头内容输出至命令行
                                System.out.println("request headers: ");
                                Enumeration<String> headers = request.getHeaderNames();
                                while (headers.hasMoreElements()) {
                                    String value = request.getHeader(headers.nextElement());
                                    System.out.println(headers.nextElement() + ":" + value);
                                }
                                System.out.println("-------------------------------------------------------------");

                                // 更新登陆用户的登录次数和最后一次登陆时间数据
                                detail[6] = String.valueOf(Integer.parseInt(detail[6]) + 1);
                                detail[7] = String.valueOf(date);
                                String updateData = detail[0] + "," + detail[1] + "," + detail[2] + "," + detail[3] + "," + detail[4] + "," + detail[5] + "," + detail[6] + "," + detail[7];
                                bufferedWriter.write(updateData);
                                bufferedWriter.newLine();
                                status = 4;
                            }
                        } else {
                            response.getWriter().print("<script language='javascript'>alert('账号或密码错误！');window.location.href='login.jsp';</script>");
                        }
                    } else {
                        bufferedWriter.write(lineData);
                        bufferedWriter.newLine();
                    }
                }

                bufferedReader.close();
                bufferedWriter.close();
                userFile.delete();
                bufferFile.renameTo(new File(userPath));

                // 页面跳转
                // 必须当数据文件（txt，亦可理解成线性表）读写完成，关闭读写流之后才能进行页面跳转，否则会造成数据丢失
                if (status == 1) {
                    response.getWriter().print("<script language='javascript'>alert('不允许同一设备登入多个账号！请检查账号或密码是否错误！');window.location.href='login.jsp';</script>");
                } else if (status == 2) {
                    request.getRequestDispatcher("admin_success.jsp").forward(request, response);
                } else if (status == 3) {
                    request.getRequestDispatcher("login_success.jsp").forward(request, response);
                } else if (status == 4) {
                    if (username.equals("admin") && pwd.equals("123456")) {
                        request.getRequestDispatcher("admin_success.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("login_success.jsp").forward(request, response);
                    }
                } else {
                    response.getWriter().print("<script language='javascript'>alert('账号不存在！');window.location.href='login.jsp';</script>");
                }
            } catch (IOException | ServletException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
