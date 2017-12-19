package webserver;

import java.io.IOException;
import java.util.Collection;

import db.DataBase;
import model.User;

public class ListUserController implements Controller {

	@Override
	public void serve(HttpRequest request, HttpResponse response) throws IOException {
		if (!Boolean.parseBoolean(request.getCookie("logined"))) {
        	response.forward("/user/login.html");
            return;
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
	}

}
