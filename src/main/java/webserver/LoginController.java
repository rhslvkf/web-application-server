package webserver;

import java.io.IOException;

import db.DataBase;
import model.User;

public class LoginController implements Controller {

	@Override
	public void serve(HttpRequest request, HttpResponse response) throws IOException {
		User user = DataBase.findUserById(request.getParameter("userId"));
		if (user != null) {
		    if (user.login(request.getParameter("password"))) {
		    	response.addHeader("Set-Cookie", "logined=true");
		    	response.sendRedirect("/index.html");
		    } else {
		    	response.forward("/user/login_failed.html");
		    }
		} else {
			response.forward("/user/login_failed.html");
		}
	}

}
