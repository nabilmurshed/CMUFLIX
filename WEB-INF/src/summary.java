package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class summary extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException 
	{
		String hostname  = "mnabilmu-db.qatar.cmu.local";
		String DBname    = "Project3";
		String dusername  = "postgres";
		String dpassword  = "uA2Q9xLV";	
		
		DBConnection newConn = new DBConnection();
		
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

		int usercount = 0;
		try {
			usercount = newConn.userCount();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		int likescount = 0;
		try {
			likescount = newConn.likesCount();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		String av = "";
		try {
			av = newConn.avid();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		String poor = "";
		try {
			poor = newConn.poorMovies();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		response.setContentType("text/html");
		PrintWriter outstream = response.getWriter();

		outstream.println("<head>");
		outstream.println("<title> Website Summary! </title>");
		outstream.println("</head>");
				
		outstream.println("<body>");
		outstream.println("<br><br><center>");
		outstream.println("<h1> Website Summary </h1><br><br>");
		outstream.println("<h2>The total number of registered users in the system.</h2><br>");
		outstream.println("<h2>"+usercount+"</h2><br><br>");
		outstream.println("<h2>The total number of \"likes\" in the system from all users.</h2><br>");
		outstream.println("<h2>"+likescount+"</h2><br><br>");
		outstream.println("<h2>Avid Users</h2><br>");
		outstream.println(av+"<br><br>");
		outstream.println("<h2>Movies nobody liked</h2><br>");
		outstream.println(poor+"<br><br>");

		outstream.println("<br><br><FORM ACTION=\"ProfileServlet\" METHOD=\"POST\" NAME=\"ProfileForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Go back to Profile\"><P>");
		outstream.println("</FORM><br><br></center>");

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