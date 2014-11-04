package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class profile extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException 
	{

		boolean newUser = true; 
 		Cookie unameCookie = null;  
 		Cookie allCookies[] = request.getCookies();

 		if(allCookies != null && allCookies.length != 0) 
		 { 
			 for (Cookie c : allCookies) 
			 { 
				 if(c.getName().equals("username")) 
				 { 
					 newUser = false; 
					 unameCookie = c; 
				 } 
			 } 
		 }

		if(newUser)response.sendRedirect("index.html");

		String hostname  = "mnabilmu-db.qatar.cmu.local";
		String DBname    = "Project3";
		String dusername  = "postgres";
		String dpassword  = "uA2Q9xLV";	
		
		DBConnection newConn = new DBConnection();
		String username = unameCookie.getValue();
		
		try {
			newConn.loadDriver();
		} 
		catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}

		try {
			newConn.connectToDatabase(hostname, DBname, dusername, dpassword);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		String email = "";
		try {
			email = newConn.getEmail(username);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		response.setContentType("text/html");
		PrintWriter outstream = response.getWriter();

		outstream.println("<head>");
		outstream.println("<title> Welcome " + username + "! </title>");
		outstream.println("</head>");
				
		outstream.println("<body>");
		outstream.println("<h2> Welcome to your profile, " + username + "</h2>");
		outstream.println("<h2>" + email + "</h2>");
		outstream.println("<br><br><FORM ACTION=\"LikeServlet\" METHOD=\"POST\" NAME=\"LikeForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Like more movies\"><P>");
		outstream.println("</FORM>");
		outstream.println("<br><br><FORM ACTION=\"summaryServlet\" METHOD=\"POST\" NAME=\"summaryForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Site Summary\"><P>");
		outstream.println("</FORM><br><br>");
		outstream.println("<FORM ACTION=\"recommendServlet\" METHOD=\"POST\" NAME=\"recommendForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Recommended movie\"><P>");
		outstream.println("</FORM><br><br>");
		outstream.println("<h2><u>Your Favorite Movies </h2></u>");

		List<String> mlist = new ArrayList<String>();
		try {
			mlist = newConn.likeList(username);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		for (String s : mlist) {
			outstream.println("<b>"+s+"</b><br>");
		}

		outstream.println("<br><br><FORM ACTION=\"logoutServlet\" METHOD=\"POST\" NAME=\"logoutForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"logout\"><P>");
		outstream.println("</FORM><br><br>");

		outstream.println("</body>");
	}

	public void  doGet(HttpServletRequest request, HttpServletResponse response)
                                                        throws ServletException, IOException 
	{
		doPost(request,response);
	}
}