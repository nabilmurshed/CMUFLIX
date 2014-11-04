package local.cmu.qatar.db;

import java.sql.*;
import java.util.*;
import java.util.Properties;

/** JDBC Driver documentation can be fount at:
 *  jdbc.postgresql.org/documentation/92/index.html **/

public class DBConnection {
	
	public java.sql.Connection conn;
	public java.sql.PreparedStatement st;
	public java.sql.ResultSet rs;

	public static void main(String[] args){
		
		String hostname  = "mnabilmu-db.qatar.cmu.local";
		String DBname    = "Project3";
		String username  = "postgres";
		String password  = "uA2Q9xLV";	
		
		DBConnection newConn = new DBConnection();
		
		try {
			newConn.loadDriver();
		} 
		catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}

		try {
			newConn.connectToDatabase(hostname, DBname, username, password);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		// try {
		// 	newConn.executeSelect("JR", "Computer Science");
		// } 
		// catch (SQLException e) {
		// 	e.printStackTrace();
		// }
	}

	public DBConnection() {
		
		this.conn = null;
		this.st = null;
		this.rs = null;	
	}
	
	public void loadDriver() throws ClassNotFoundException {
		
		String driver = "org.postgresql.Driver";
		Class.forName(driver);
		println("JDBC driver loaded!");
		
	}
	
	public void connectToDatabase(String hostname, String DB, String username, String password) throws SQLException {
		
		println("Establishing a connection to DB " + DB +". ..");
		
		String url = "jdbc:postgresql://" + hostname + "/" + DB;
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		conn = DriverManager.getConnection(url, props);
		
		println("Connection established!");
	}

	//Is this user a member with this password?
	public boolean isMember(String usrnm, String pass) throws SQLException 
	{	
			String query = "select * from users U where logid = ? ";
			
			st = conn.prepareStatement(query);
			st.setString(1, usrnm);
			rs = st.executeQuery();
			if(rs.next())
			{
				if(pass.equals(rs.getObject(2).toString())) 
					return true;
			}
			return false;
	}

	//Is the given username already taken?
	public boolean nameTaken(String usrnm) throws SQLException 
	{	
			String query = "select * from users U where logid = ? ";
			
			st = conn.prepareStatement(query);
			st.setString(1, usrnm);
			rs = st.executeQuery();
			if(rs.next()) return true;
			return false;
	}

	//Add new member to Database, used by registration servlet
	public void addMember(String usrnm, String pass, String email) throws SQLException 
	{	
			String query = "insert into users values ( ? , ? , ? ); ";
			
			st = conn.prepareStatement(query);
			st.setString(1, usrnm);
			st.setString(2, pass);
			st.setString(3, email);
			st.executeUpdate();
	}

	//The total number of registered users in the system.
	public int userCount() throws SQLException 
	{	
			String query = "select count(*) from users;";
			
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			if(rs.next()) return Integer.parseInt(rs.getObject(1).toString());
			else return 0;
	}

	//The total number of "likes" in the system from all users.
	public int likesCount() throws SQLException 
	{	
			String query = "select count(*) from likes;";
			
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			if(rs.next()) return Integer.parseInt(rs.getObject(1).toString());
			else return 0;
	}

	//The list of movies that no one has liked, sorted in alpha order on the title. 
	public String poorMovies() throws SQLException 
	{	
			String query = "select m.title from moviedata m where m.mid not in (select l.mid from likes l) order by m.title;";
			
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			String result="";
			while(rs.next())
			{
				result += rs.getObject(1).toString();
				result += "<br>";
			} 
			return result;
	}

	//Recommends movies based on clan and indegree
	public String recommender(String usrnm) throws SQLException 
	{	
			String query = "select m.title from moviedata m, (select l.mid, count(*) as c from likes l where l.logid in (select distinct l2.logid from likes l2 where l2.mid in (select l1.mid from likes l1 where l1.logid = 'omar') and l2.logid <> ? ) group by l.mid) as x where m.mid= x.mid order by x.c desc, m.title asc limit 5;";
			
			st = conn.prepareStatement(query);
			st.setString(1, usrnm);
			rs = st.executeQuery();
			String result="";
			while(rs.next())
			{
				result += rs.getObject(1).toString();
				result += "<br>";
			} 
			return result;
	}

	//Get the email ID of this user
	public String getEmail(String usrnm) throws SQLException 
	{	
			String query = "select u.email from users u where u.logid = ? ;";
			
			st = conn.prepareStatement(query);
			st.setString(1, usrnm);
			rs = st.executeQuery();
			if(rs.next()) return rs.getObject(1).toString();
			else return "email not found ";
	}

	
	//The list of the "avid users" - the usernames of users with the most number 
	//of "likes", limited to top 10, sorted by the number of likes. Break ties by the sorting the usernames alphabetically. 
	public String avid() throws SQLException 
	{	
			String query = "select counter.logid from (select l.logid, count(*) as c from likes l group by l.logid) as counter order by counter.c desc,counter.logid asc limit 10;";
			
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			String result="";
			while(rs.next())
			{
				result += rs.getObject(1).toString();
				result += "<br>";
			} 
			return result;
	}

	//The movies liked by this user
	public List<String> likeList(String usrnm) throws SQLException
	{
		List<String> mlist = new ArrayList<String>();
		String query = "select m.title from moviedata m where m.mid in (select l.mid from likes l where l.logid = ?);";
			
		st = conn.prepareStatement(query);
		st.setString(1, usrnm);
		rs = st.executeQuery();
		while(rs.next())
		{
			mlist.add(rs.getObject(1).toString());
		} 
		return mlist;
	}

	//The movies not yet liked by user
	public List<String> pending(String usrnm) throws SQLException
	{
		List<String> mlist = new ArrayList<String>();
		String query = "select m.title from moviedata m where m.mid not in (select l.mid from likes l where l.logid = ?);";
			
		st = conn.prepareStatement(query);
		st.setString(1, usrnm);
		rs = st.executeQuery();
		while(rs.next())
		{
			mlist.add(rs.getObject(1).toString());
		} 
		return mlist;
	}

	//Thetop 5 liked movies
	public List<String> top5() throws SQLException
	{
		List<String> mlist = new ArrayList<String>();
		String query = "select m.title from (select l.mid , count(*) as c from likes l group by l.mid limit 5 )as t, moviedata m where m.mid=t.mid order by c desc,m.title asc;";
			
		st = conn.prepareStatement(query);
		rs = st.executeQuery();
		while(rs.next())
		{
			mlist.add(rs.getObject(1).toString());
		} 
		return mlist;
	}

	//When a like button is clicked, new entree in Likes table
	public void likeit(String movie, String usrnm) throws SQLException 
	{		
			int mid =getmid(movie);
			String query = "insert into likes values ( ? , ? );";
			
			st = conn.prepareStatement(query);
			st.setInt(1, mid); 
			st.setString(2, usrnm);
			st.executeUpdate();
	}

	//Give movie name to get its mid
	public int getmid(String movie) throws SQLException 
	{		
			String query = "select m.mid from moviedata m where m.title = ? ";
			
			st = conn.prepareStatement(query);
			st.setString(1, movie); 
			rs = st.executeQuery();
			if(rs.next()) return Integer.parseInt(rs.getObject(1).toString());
			else return 0;
	}
	
	public void clean() throws SQLException 
	{
		st.close();
		rs.close();
	}
	
	public static void println(Object msg)
	{
		System.out.println(msg.toString());
	}
	
	public static void print(Object msg)
	{
		System.out.print(msg.toString());
	}
}
