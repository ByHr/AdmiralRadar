package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import security.DesEncrypter;

public class dbQuery {
		Connection	conn		= null;
		Statement	statement	= null;
		ResultSet	rs			= null;

		public dbQuery(Connection conn, Statement statement, ResultSet rs) {
			this.conn = conn;
			this.rs = rs;
		}

		public boolean close() {
			
			 try { 
				 if (rs != null) { 
					 rs.close(); 
				 } 
				 if (conn != null) { 
					 conn.close();
				} if (statement != null) {
					statement.close(); 
				} 
			} catch (Exception e) {
				e.printStackTrace(); return false; 
			}
			 
			return true;
		}

	
	/*
	 * For 'int db':
	 * 1 - User DB
	 * 2 - Chat DB
	 */
	public static dbQuery query(String query, int db) {

		Connection conn = null;

		try {
			String url;
			if (db == 1) {
				url = "jdbc:mysql://radar.c87i64zdxk4i.us-east-2.rds.amazonaws.com:3306/AdmiralRadar?verifyServerCertificate=false&useSSL=true&requireSSL=true";
			} else {
				url = "jdbc:mysql://radarchat.c87i64zdxk4i.us-east-2.rds.amazonaws.com:3306/AdmiralRadarChat?verifyServerCertificate=false&useSSL=true&requireSSL=true";
			}
			Properties info = new Properties();
			info.put( "user" , "api_user" );
			info.put( "password" , "password1234" );

			DriverManager.registerDriver( new com.mysql.jdbc.Driver() );
			conn = DriverManager.getConnection( url , info );

		}
		catch (Exception ex) {
			System.out.println( "An error occurred while connecting MySQL databse" );
			ex.printStackTrace();
			return null;
		}

		if (conn != null) {
			// myPrint("Successfully connected to MySQL database");
		} else {
			System.out.println( "Could not connect to MySQL database" );
			return null;
		}

		// Connection established, run query...
		Statement statement = null;

		try {

			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery( query );
			dbQuery obj = new dbQuery( conn , statement , rs );
			return obj;

		}
		catch (Exception e) {
			System.out.println( "Could not execute query on MySQL database" );
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 0 - Success 1 - Invalid Username 2 - Invalid Password
	 */
	public static int login(String user, String pw) {

		dbQuery DBobj = query( "SELECT USERNAME, PASSWORD FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {
				if (user.equals( DBobj.rs.getString( "USERNAME" ) )) {
					if (pw.equals( DBobj.rs.getString( "PASSWORD" ) )) {
						DBobj.close();
						return 0;
					} else {
						// myPrint("PW: " + pw + " | " + DBobj.rs.getString("PASSWORD"));
						DBobj.close();
						return 2;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return 1;
	}

	/*
	 * 0 - Success 1 - Invalid Username 2 - Invalid PIN
	 */
	public static int resetPW(String user, String pw, int pin) {

		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {
				if (user.equals( DBobj.rs.getString( "USERNAME" ) )) {
					if (pin == DBobj.rs.getInt( "PIN" )) {

						String query = "UPDATE USER SET PASSWORD = ? WHERE USERNAME = ?";
						PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
						preparedStmt.setString( 1 , pw );
						preparedStmt.setString( 2 , user );

						// myPrint("Prepared Statement: " + preparedStmt);

						// execute the java preparedstatement
						preparedStmt.executeUpdate();

						DBobj.close();
						return 0;

					} else {
						DBobj.close();
						return 2;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return 1;

	}

	/*
	 * 0 - Success 1 - Misc. Failure
	 */
	public static int setURL(String user, String url) {

		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		String query = "UPDATE USER SET AVATAR = ? WHERE USERNAME = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setString( 1 , url );
			preparedStmt.setString( 2 , user );
			preparedStmt.executeUpdate();
			// myPrint("Prepared Statement: " + preparedStmt);
		}
		catch (Exception e) {
			e.printStackTrace();
			DBobj.close();
			return 1;
		}
		DBobj.close();
		return 0;

	}

	///
	/*
	 * [url returned] - Success ERROR - Invalid username / Misc.
	 */
	public static String getURL(String user) {

		dbQuery DBobj = query( "SELECT USERNAME, AVATAR FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {
				if (user.equals( DBobj.rs.getString( "USERNAME" ) )) {
					String avatar = DBobj.rs.getString( "AVATAR" );
					DBobj.close();
					return avatar;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return "ERROR";

	}

	/*
	 * [# Wins] - Success -1 - Invalid Username
	 */
	public static int getWins(String user) {

		dbQuery DBobj = query( "SELECT USERNAME, WINS FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {
				if (user.equals( DBobj.rs.getString( "USERNAME" ) )) {
					int wins = DBobj.rs.getInt( "WINS" );
					DBobj.close();
					return wins;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return -1;

	}

	/*
	 * [# Losses] - Success -1 - Invalid Username
	 */
	public static int getLosses(String user) {

		dbQuery DBobj = query( "SELECT USERNAME, LOSSES FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {
				if (user.equals( DBobj.rs.getString( "USERNAME" ) )) {
					int losses = DBobj.rs.getInt( "LOSSES" );
					DBobj.close();
					return losses;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return -1;

	}

	/*
	 * Returns a List of formatted messages
	 */
	public static List<String> getTeamMessages(int teamID) {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE, TEAM_ID FROM TEAM_CHAT", 2 );

		List<String> messages = new ArrayList<String>();

		try {
			while (DBobj.rs.next()) {

				int userTeam = DBobj.rs.getInt( "TEAM_ID" );
				String username = DBobj.rs.getString( "USERNAME" );
				String time = DBobj.rs.getString( "TIMESTAMP" ).substring( 11 , 18 );
				String message = DBobj.rs.getString( "MESSAGE" );

				if (teamID == userTeam) {
					messages.add( username + ": " + message );
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return messages;
	}

	/*
	 * Returns a List of formatted messages
	 */
	public static List<String> getGlobalMessages() {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE FROM GLOBAL_CHAT", 2 );

		List<String> messages = new ArrayList<String>();

		try {
			while (DBobj.rs.next()) {

				String username = DBobj.rs.getString( "USERNAME" );
				String time = DBobj.rs.getString( "TIMESTAMP" ).substring( 11 , 18 );
				String message = DBobj.rs.getString( "MESSAGE" );

				messages.add( username + ": " + message );
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}
		DBobj.close();
		return messages;

	}

	/*
	 * Returns a true boolean on successful message entry
	 */
	public static boolean sendTeamMessage(String username, String message, int teamID) {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE FROM TEAM_CHAT", 2 ); // just to init obj.
		String query = "INSERT INTO TEAM_CHAT (USERNAME, MESSAGE, TEAM_ID) VALUES (?,?,?)";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setString( 1 , username );
			preparedStmt.setString( 2 , message );
			preparedStmt.setInt( 3 , teamID );
			preparedStmt.executeUpdate();
			// myPrint("Prepared Statement: " + preparedStmt);
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * Returns a true boolean on successful message entry
	 */
	public static boolean sendGlobalMessage(String username, String message) {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE FROM GLOBAL_CHAT", 2 ); // just to init obj.
		String query = "INSERT INTO GLOBAL_CHAT (USERNAME, MESSAGE) VALUES (?,?)";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setString( 1 , username );
			preparedStmt.setString( 2 , message );
			preparedStmt.executeUpdate();
			// myPrint("Prepared Statement: " + preparedStmt);
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * Returns a true boolean on successful chat wipe
	 */
	public static boolean clearTeamMessages() {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE FROM TEAM_CHAT", 2 ); // just to init obj.
		String query = "TRUNCATE TEAM_CHAT";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * Returns a true boolean on successful chat wipe
	 */
	public static boolean clearGlobalMessages() {
		dbQuery DBobj = query( "SELECT USERNAME, TIMESTAMP, MESSAGE FROM GLOBAL_CHAT", 2 ); // just to init obj.
		String query = "TRUNCATE GLOBAL_CHAT";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * >0 - the user's team_id 0 - this user is not assigned to a team -1 - this user could not be found
	 */
	public static int getTeamID(String username) {
		dbQuery DBobj = query( "SELECT TEAM_ID, USERNAME FROM USER", 1 );

		try {
			while (DBobj.rs.next()) {

				String user = DBobj.rs.getString( "USERNAME" );
				int team_id = DBobj.rs.getInt( "TEAM_ID" );

				if (user.equals( username )) {
					if (team_id != 0) {
						DBobj.close();
						return team_id;
					} else {
						DBobj.close();
						return 0;
					}
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}

		DBobj.close();
		return -1;
	}

	/*
	 * Will return true boolean upon successful update - if false, check that username and teamID exist
	 */
	public static boolean setTeamID(String username, int teamID) {
		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		String query = "UPDATE USER SET TEAM_ID = ? WHERE USERNAME = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setInt( 1 , teamID );
			preparedStmt.setString( 2 , username );
			preparedStmt.executeUpdate();
			// myPrint("Prepared Statement: " + preparedStmt);
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * Will return true boolean upon success TeamID reset
	 */
	public static boolean resetTeamID(String username) {
		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		String query = "UPDATE USER SET TEAM_ID = NULL WHERE USERNAME = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setString( 1 , username );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * >=0 - Returns value of team's health -1 - Could not find team with this ID
	 */
	public static int getTeamHealth(int teamID) {
		dbQuery DBobj = query( "SELECT HEALTH FROM TEAM", 1 );

		try {
			while (DBobj.rs.next()) {

				int ID = DBobj.rs.getInt( "ID" );

				if (ID == teamID) { return ID; }

			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "There's an issue retrieving info. from query results" );
		}

		DBobj.close();
		return -1;
	}

	/*
	 * Will return true boolean upon successful health update
	 */
	public static boolean setTeamHealth(int teamID, int health) {
		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		String query = "UPDATE TEAM SET HEALTH = ? WHERE ID = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setInt( 1 , health );
			preparedStmt.setInt( 2 , teamID );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			DBobj.close();
			e.printStackTrace();
			return false;
		}

		DBobj.close();
		return true;
	}

	/*
	 * 0-9999 - Success
	 * -1 - ERROR: Username already in-use
	 * -2 - ERROR: Misc.
	 */
	public static int createUser(String username, String password, String avatar) {	
		
		Random rand = new Random();
		int pinInt = rand.nextInt( 10000 );
		String pinString = String.format( "%04d" , pinInt );

		// 0000, 1111, ...., 9999.
		for (int i = 0; i < 10; i++) {
			String pinCheck = i + "" + i + "" + i + "" + i;
			// check if PIN is one of these values
			if (pinCheck.equals( pinString )) {
				// re-gen. PIN if it is
				return createUser( username , password , avatar );
			}
		}

		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER" , 1); // just to init obj.

		String query = "INSERT INTO USER (USERNAME, PASSWORD, AVATAR, PIN) VALUES (?,?,?, ?)";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setString( 1 , username );
			preparedStmt.setString( 2 , password );
			preparedStmt.setString( 3 , avatar );
			preparedStmt.setInt( 4 , pinInt );
			preparedStmt.executeUpdate();

			//myPrint("Prepared Statement: " + preparedStmt);
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate")) {
				DBobj.close();
				return -1;
			} else {
				DBobj.close();
				e.printStackTrace();
				return -2;
			}
		}

		DBobj.close();
		return pinInt;
	}
	/*
	 * Self-explanatory on what it returns.
	 */
	public static boolean userExists(String username) {
		dbQuery DBobj = query("SELECT * FROM USER WHERE USERNAME = \"" + username + "\"", 1);
		
		try {
			while (DBobj.rs.next()) {
				DBobj.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There's an issue retrieving info. from query results");
		}
		
		DBobj.close();
		return false;
	}
	
	
	/*
	 * 0000-9999 - returned PIN
	 * -1 - Misc. Error
	 */
	public static int getUserPIN(String username) {
		dbQuery DBobj = query("SELECT PIN FROM USER WHERE USERNAME = \"" + username + "\"", 1);
		
		try {
			while (DBobj.rs.next()) {
				int result = DBobj.rs.getInt( "PIN" );
				DBobj.close();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There's an issue retrieving info. from query results");
		}
		
		DBobj.close();
		return -1;
	}
	
	/*
	 * true - success
	 * false - failure
	 */
	public static boolean addWin(String username) {
		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		
		int newWins = getWins(username) + 1;
		if (newWins == 0) {
			DBobj.close();
			return false;
		}
		
		String query = "UPDATE USER SET WINS = ? WHERE USERNAME = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setInt( 1 , newWins );
			preparedStmt.setString( 2 , username );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
			DBobj.close();
			return false;
		}

		DBobj.close();
		return true;
	}
	
	/*
	 * true - success
	 * false - failure
	 */
	public static boolean addLoss(String username) {
		dbQuery DBobj = query( "SELECT USERNAME, PIN FROM USER", 1 ); // just to init obj.
		
		int newLosses = getLosses(username) + 1;
		if (newLosses == 0) {
			DBobj.close();
			return false;
		}
		
		String query = "UPDATE USER SET LOSSES = ? WHERE USERNAME = ?";

		try {
			PreparedStatement preparedStmt = DBobj.conn.prepareStatement( query );
			preparedStmt.setInt( 1 , newLosses );
			preparedStmt.setString( 2 , username );
			preparedStmt.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
			DBobj.close();
			return false;
		}

		DBobj.close();
		return true;
	}
	
}