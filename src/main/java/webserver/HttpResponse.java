package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	private DataOutputStream dos;
	private Map<String, String> header = new HashMap<String, String>();

	public HttpResponse(OutputStream out) {
		dos = new DataOutputStream(out);
	}
	
	public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        if (url.endsWith(".html")) {
        	header.put("Content-Type", "text/html;charset=utf-8");
        } else if (url.endsWith(".css")) {
        	header.put("Content-Type", "text/css");
        } else if (url.endsWith(".js")) {
        	header.put("Content-Type", "application/javascript");
        }
        header.put("Content-Length", String.valueOf(body.length));
        response200Header(body.length);
        responseBody(body);
    }
	
	public void forwardBody(String body) {
		byte[] contents = body.getBytes();
		header.put("Content-Type", "text/html;charset=utf-8");
		header.put("Content-Length", String.valueOf(contents.length));
		response200Header(contents.length);
		responseBody(contents);
	}
	
	public void sendRedirect(String url) {
		try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            processHeader();
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	public void addHeader(String key, String value) {
		header.put(key, value);
	}
	
	private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeader();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

	private void processHeader() {
		try {
			Set<String> keySet = header.keySet();
			for (String key : keySet) {
				dos.writeBytes(key + ": " + header.get(key) + "\r\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
}
