package webserver;

import java.io.IOException;

public interface Controller {
	void serve(HttpRequest request, HttpResponse response) throws IOException;
}
