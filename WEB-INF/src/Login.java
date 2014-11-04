package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class Login extends HttpServlet
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

		String username = request.getParameter("username");
		String password = request.getParameter("password");


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

		if((newUser == true) && (username.length() != 0)) 
		{ 
			unameCookie = new Cookie("username", username); 
			unameCookie.setMaxAge(1800); /* 30 minutes */  
			response.addCookie(unameCookie); 
		}

		boolean ismemb=false;
		try {
			ismemb = newConn.isMember(username,password);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		if(ismemb)
		{
			outstream.println("<head>");
			outstream.println("<title> Welcome " + username + "! </title>");
			outstream.println("</head>");
			
			String email = "";
			try {
				email = newConn.getEmail(username);
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}

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
		else
		{
			outstream.println("<head>");
			outstream.println("<title> Login Error </title>");
			outstream.println("</head>");
			
			outstream.println("<center><br><br><h1>Sorry, you Username or Password is incorrect!</h1>");
			
			outstream.println("<FORM ACTION=\"LoginServlet\" METHOD=\"POST\" NAME=\"LoginForm\">");
			outstream.println("<INPUT TYPE=\"text\" NAME=\"username\" VALUE=\"Eg. Superman1\" id=\"t1\" required><br>");
			outstream.println("<INPUT TYPE=\"password\" NAME=\"password\" required><P>");
			outstream.println("<INPUT TYPE=\"reset\" VALUE=\"Reset Values\"><P>");
			outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Log-in\"><P>");
			outstream.println("</FORM>");
			outstream.println("<br><br><br>");
			
			outstream.println("<h2>Not a user? Register!!</h2>");
			outstream.println("<FORM ACTION=\"RegistrationServlet\" METHOD=\"POST\" NAME=\"registrationForm\">");
			outstream.println("Choose a Username  <INPUT TYPE=\"text\" NAME=\"rusername\" VALUE=\"Eg. Superman1\" id=\"t1\" required><br>");
			outstream.println("Choose a password  <INPUT TYPE=\"password\" NAME=\"rpassword\" required><br>");
			outstream.println("Confirm password  <INPUT TYPE=\"password\" NAME=\"repassword\" required><br>");
			outstream.println("Enter your email  <INPUT TYPE=\"email\" NAME=\"email\" VALUE=\"Eg. superman@comics.com\" required><P>");
			outstream.println("<INPUT TYPE=\"reset\" VALUE=\"Reset Values\"><P>");
			outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Register-in\"><P>");
			outstream.println("</center></FORM>");

		}
	}

	public void  doGet(HttpServletRequest request, HttpServletResponse response)
                                                        throws ServletException, IOException 
	{
		doPost(request,response);
	}
}
