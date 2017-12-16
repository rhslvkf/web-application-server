package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	String line = br.readLine();
        	if (line == null) return;
        	String[] tokens = line.split(" ");
        	int contentLength = 0;
        	Boolean logined = false;
        	while (!line.equals("")) {
        		log.debug(line);
        		if (line.contains("Content-Length")) {
        			contentLength = getContentLength(line);
        		} else if (line.contains("Cookie")) {
        			Map<String, String> cookie = HttpRequestUtils.parseCookies(line.split(":")[1].trim());
        			logined = Boolean.parseBoolean(cookie.get("logined"));
        			log.debug("logined : {}", logined);
        		}
        		line = br.readLine();
        	}
        	String url = tokens[1];
        	log.debug("url : {}", url);
            DataOutputStream dos = new DataOutputStream(out);
            if (url.equals("/user/create")) {
            	Map<String, String> params = getParamsByBody(br, contentLength);
            	User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
            	log.debug("User : {} ", user);
            	DataBase.addUser(user);
            	response302Header(dos, "/index.html");
            } else if (url.equals("/user/login")) {
            	Map<String, String> params = getParamsByBody(br, contentLength);
            	User user = DataBase.findUserById(params.get("userId"));
            	if (isSuccessLogin(params, user)) {
            		response302HeaderLoginSuccess(dos, "/index.html");
            		return;
            	}
            	response302HeaderLoginFail(dos, "/user/login_failed.html");
            } else if (url.equals("/user/list")) {
            	if (!logined) {
            		byte[] body = Files.readAllBytes(new File("./webapp/index.html").toPath());
            		responseResource(dos, body);
            		return;
            	}
            	StringBuilder sb = makeUserListTable();
            	responseResource(dos, sb.toString().getBytes());
            } else if (url.endsWith("css")) {
            	byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            	responseResourceCSS(dos, body);
            } else {
            	byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            	responseResource(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

	private void responseResource(DataOutputStream dos, byte[] body) {
		response200Header(dos, body.length);
		responseBody(dos, body);
	}
	
	private void responseResourceCSS(DataOutputStream dos, byte[] body) {
		response200HeaderCSS(dos, body.length);
		responseBody(dos, body);
	}

	private StringBuilder makeUserListTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		Collection<User> userList = DataBase.findAll();
		sb.append("<tr>");
		sb.append("<th>ID</th>");
		sb.append("<th>NAME</th>");
		sb.append("<th>EMAIL</th>");
		sb.append("</tr>");
		for (User user : userList) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "<td>");
			sb.append("<td>" + user.getName() + "<td>");
			sb.append("<td>" + user.getEmail() + "<td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb;
	}

	private boolean isSuccessLogin(Map<String, String> params, User user) {
		return user != null && user.getPassword().equals(params.get("password"));
	}

	private Map<String, String> getParamsByBody(BufferedReader br, int contentLength) throws IOException {
		String requestBody = IOUtils.readData(br, contentLength);
		Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
		return params;
	}
	
	private void response200HeaderCSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302HeaderLoginSuccess(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302HeaderLoginFail(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("Set-Cookie: logined=false \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private int getContentLength(String line) {
    	return Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
	}
    
}
