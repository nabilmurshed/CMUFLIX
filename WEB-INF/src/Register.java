package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class Register extends HttpServlet
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

		boolean newUser = true; 
 		Cookie unameCookie = null;

		String username = request.getParameter("rusername");
		String password = request.getParameter("rpassword");
		String repassword = request.getParameter("repassword");
		String email = request.getParameter("email");

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

		response.setContentType("text/html");
		PrintWriter outstream = response.getWriter();

		boolean name=false;
		try {
			name = newConn.nameTaken(username);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		if(!name)
		{
			outstream.println("<head>");
			outstream.println("<title> Welcome " + username + "! </title>");
			outstream.println("</head>");
					
			outstream.println("<body>");
			
			if(password.length() == 0)
			{
				outstream.println("<center><h2> Registration Failed </h2>");
				outstream.println("Your password can not be empty!");

				outstream.println("<FORM ACTION=\"RegistrationServlet\" METHOD=\"POST\" NAME=\"registrationForm\">");
				outstream.println("Choose a Username  <INPUT TYPE=\"text\" NAME=\"rusername\" VALUE=\"Eg. Superman1\" id=\"t1\" required><br>");
				outstream.println("Choose a password  <INPUT TYPE=\"password\" NAME=\"rpassword\" required><br>");
				outstream.println("Confirm password  <INPUT TYPE=\"password\" NAME=\"repassword\" required><br>");
				outstream.println("Enter your email  <INPUT TYPE=\"email\" NAME=\"email\" VALUE=\"Eg. superman@comics.com\" required><P>");
				outstream.println("<INPUT TYPE=\"reset\" VALUE=\"Reset Values\"><P>");
				outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Register-in\"><P>");
				outstream.println("</center></FORM>");
			}

			else
			{
				if(!password.equals(repassword))
				{
					outstream.println("<center><h2> Registration Failed </h2>");
					outstream.println("Your passwords did not match");

					outstream.println("<FORM ACTION=\"RegistrationServlet\" METHOD=\"POST\" NAME=\"registrationForm\">");
					outstream.println("Choose a Username  <INPUT TYPE=\"text\" NAME=\"rusername\" VALUE=\"Eg. Superman1\" id=\"t1\" required><br>");
					outstream.println("Choose a password  <INPUT TYPE=\"password\" NAME=\"rpassword\" required><br>");
					outstream.println("Confirm password  <INPUT TYPE=\"password\" NAME=\"repassword\" required><br>");
					outstream.println("Enter your email  <INPUT TYPE=\"email\" NAME=\"email\" VALUE=\"Eg. superman@comics.com\" required><P>");
					outstream.println("<INPUT TYPE=\"reset\" VALUE=\"Reset Values\"><P>");
					outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Register-in\"><P>");
					outstream.println("</center></FORM>");
				}
				else
				{
					try {
						newConn.addMember(username, password, email);
					} 
					catch (SQLException e) {
						e.printStackTrace();
					}
					outstream.println("<h2><br> Welcome " + username + " and thankyou for using CMUQFlix </h2>");
					outstream.println("<h2>" + email + "</h2><br>");
					outstream.println("<br><br><FORM ACTION=\"LikeServlet\" METHOD=\"POST\" NAME=\"LikeForm\">");
					outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Like more movies\"><P>");
					outstream.println("</FORM><br><br>");
					outstream.println("<FORM ACTION=\"summaryServlet\" METHOD=\"POST\" NAME=\"summaryForm\">");
					outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Site Summary\"><P>");
					outstream.println("</FORM><br><br>");
					outstream.println("<FORM ACTION=\"recommendServlet\" METHOD=\"POST\" NAME=\"recommendForm\">");
					outstream.println("<INPUT TYPE=\"submit\" VALUE=\"Recommended movie\"><P>");
					outstream.println("</FORM><br><br>");
					outstream.println("<h2><u>The Top 5 Liked Movies </h2></u>");


					List<String> xlist = new ArrayList<String>();
					try {
						xlist = newConn.top5();
					} 
					catch (SQLException e) {
						e.printStackTrace();
					}

					for (String s : xlist) {
						outstream.println("<b>"+s+"</b><br>");
					}

					outstream.println("<br><br><FORM ACTION=\"logoutServlet\" METHOD=\"POST\" NAME=\"logoutForm\">");
					outstream.println("<INPUT TYPE=\"submit\" VALUE=\"logout\"><P>");
					outstream.println("</FORM><br><br>");
				}
				
			}
			
			outstream.println("</body>");
		}
		else
		{
			outstream.println("<center><h2> Registration Failed </h2>");
			outstream.println("Your username "+username+" is already taken");

			outstream.println("<FORM ACTION=\"RegistrationServlet\" METHOD=\"POST\" NAME=\"registrationForm\">");
			outstream.println("Choose a Username  <INPUT TYPE=\"text\" NAME=\"rusername\" VALUE=\"Eg. Superman1\" id=\"t1\"><br>");
			outstream.println("Choose a password  <INPUT TYPE=\"password\" NAME=\"rpassword\"><br>");
			outstream.println("Confirm password  <INPUT TYPE=\"password\" NAME=\"repassword\"><br>");
			outstream.println("Enter your email  <INPUT TYPE=\"email\" NAME=\"email\" VALUE=\"Eg. superman@comics.com\"><P>");
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
