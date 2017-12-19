package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        	HttpRequest request = new HttpRequest(in);
        	HttpResponse response = new HttpResponse(out);
        	
            Controller controller = RequestMapping.getController(request.getPath());
            if (controller != null) {
            	controller.serve(request, response);
            } else {
            	response.forward(getDefaultUrl(request.getPath()));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private String getDefaultUrl(String url) {
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

}
