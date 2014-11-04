package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class likepage extends HttpServlet
{
	public void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException 
	{

		String hostname  = "mnabilmu-db.qatar.cmu.local";
		String DBname    = "Project3";
		String dusername  = "postgres";
		String dpassword  = "uA2Q9xLV";	
		
		DBConnection newConn = new DBConnection();
		
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

		response.setContentType("text/html");
		PrintWriter outstream = response.getWriter();

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
		
		String username = unameCookie.getValue();

		java.util.Enumeration <String> movieNames = request.getParameterNames();
		while(movieNames.hasMoreElements()) 
        {
        	String amovie = (String)movieNames.nextElement();
        	try {
				newConn.likeit(amovie,username);
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
        }

		outstream.println("<head>");
		outstream.println("<title> Welcome " + username + "! </title>");
		outstream.println("</head>");
				
		outstream.println("<body>");

		List<String> xlist = new ArrayList<String>();
		try {
			xlist = newConn.pending(username);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		outstream.println("<FORM ACTION=\"LikeServlet\" METHOD=\"POST\" NAME=\"LikeForm\">");

		outstream.println("<br><h2><u>Your Favorite Movies </h2></u>");
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


		outstream.println("<br><br><h2><u>Try and Like These Movies </h2></u>");
		for (String s : xlist) {
			outstream.println("<br><input type=\"checkbox\" name=\""+s+"\"> "+s);
		}

		outstream.println("<br><INPUT TYPE=\"submit\" VALUE=\"Submit likes\"><P>");
		outstream.println("</FORM><br><br>");
		outstream.println("<FORM ACTION=\"ProfileServlet\" METHOD=\"POST\" NAME=\"ProfileForm\">");
		outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Go back to Profile\"><P>");
		outstream.println("</FORM><br><br>");
		outstream.println("<FORM ACTION=\"logoutServlet\" METHOD=\"POST\" NAME=\"logoutForm\">");
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