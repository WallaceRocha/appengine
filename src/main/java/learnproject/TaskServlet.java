package learnproject;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.repackaged.com.google.gson.Gson;

import entities.Task;

@SuppressWarnings("serial")
@WebServlet("/task/*")
public class TaskServlet extends HttpServlet {
	
	private Gson gson = new Gson();
	
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//Task task =  
		if(request.getParameter("id") != null) {
			Task task = new Task();	
			task.setId(new Long(request.getParameter("id")));
			task = ofy().load().type(Task.class).id(task.getId()).now();
			sendAsJson(response, task);
		}else {
			List<Task> tasks = new ArrayList<>();
			tasks = ofy().load().type(Task.class).list();
			sendAsJson(response, tasks);
		}
		
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String body = IOUtils.toString(request.getInputStream());
		Task task = gson.fromJson(body,Task.class);
		if(task != null) {
			ofy().save().entity(task).now();
			sendAsJson(response, task);
		}
	}
	
	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Task task =  gson.fromJson(IOUtils.toString(request.getInputStream()),Task.class);
		if(task != null) {			
			ofy().save().entity(task).now();
			sendAsJson(response, task);
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if(request.getParameter("id") != null) {
			
			Long id = new Long(request.getParameter("id"));
			Task task = ofy().load().type(Task.class).id(id).now();
			
			if(task == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			
			ofy().delete().entity(task).now();
			PrintWriter out =  response.getWriter();
			out.print("removed");
		}else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	private void sendAsJson(
			HttpServletResponse response, 
			Object obj) throws IOException {
			response.addHeader("Access-Control-Allow-Origin","http://localhost:3000");
			response.setContentType("application/json");
			String res = gson.toJson(obj);
			PrintWriter out = response.getWriter();
			out.print(res);
			out.flush();
		}
	
}
