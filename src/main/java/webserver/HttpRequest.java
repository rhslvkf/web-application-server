package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private RequestLine requestLine;
	private Map<String, String> header = new HashMap<String, String>();
	private Map<String, String> parameter = new HashMap<String, String>();
	private Map<String, String> cookie = new HashMap<String, String>();

	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	        String line = br.readLine();
	        if (line == null) {
	            return;
	        }
	        
	        processRequestLine(line);
	        processHeader(br);
	        processBody(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processRequestLine(String line) {
		requestLine = new RequestLine(line);
	}

	private void processBody(BufferedReader br) throws IOException {
		if (requestLine.getMethod().isPost()) {
		    String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
		    log.debug("body : {}", body);
		    parameter = HttpRequestUtils.parseQueryString(body);
		} else {
			parameter = requestLine.getParameter();
		}
	}

	private void processHeader(BufferedReader br) throws IOException {
		String line = br.readLine();
		while (!line.equals("")) {
			log.debug("header : {}", line);
		    int index = line.indexOf(":");
		    processCookie(line, index);
		    header.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
		    line = br.readLine();
		}
	}

	private void processCookie(String line, int index) {
		if (line.substring(0, index).trim().equals("Cookie")) {
			String[] cookieValue = line.substring(index + 1).trim().split("=");
			cookie.put(cookieValue[0].trim(), cookieValue[1].trim());
		}
	}

	public String getMethod() {
		return requestLine.getMethod().toString();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getHeader(String name) {
		return header.get(name);
	}

	public String getParameter(String name) {
		return parameter.get(name);
	}

	public String getCookie(String name) {
		return cookie.get(name);
	}
	
}
