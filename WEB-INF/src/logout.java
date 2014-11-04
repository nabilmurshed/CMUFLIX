package local.cmu.qatar.db;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class logout extends HttpServlet
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
		unameCookie.setMaxAge(0); /* expire the cookie */  
		response.addCookie(unameCookie);

		response.sendRedirect("index.html");
	}

	public void  doGet(HttpServletRequest request, HttpServletResponse response)
                                                        throws ServletException, IOException 
	{
		doPost(request,response);
	}
}