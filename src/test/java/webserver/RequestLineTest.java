package webserver;

import static org.junit.Assert.*;

import org.junit.Test;

public class RequestLineTest {

	@Test
	public void requestLine_method_and_path() {
		RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
		assertEquals("GET", line.getMethod().toString());
		assertEquals("/index.html", line.getPath());
		
		line = new RequestLine("POST /index.html HTTP/1.1");
		assertEquals("POST", line.getMethod().toString());
		assertEquals("/index.html", line.getPath());
	}
	
	@Test
	public void requestLine_method_and_path_and_params() {
		RequestLine line = new RequestLine("GET /user/create?userId=javajig&password=pass HTTP/1.1");
		assertEquals("GET", line.getMethod().toString());
		assertEquals("/user/create", line.getPath());
		assertEquals(2, line.getParameter().size());
	}

}
