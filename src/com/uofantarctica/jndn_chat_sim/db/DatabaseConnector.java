package com.uofantarctica.jndn_chat_sim.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {
	private static final String TAG = DatabaseConnector.class.getName();
	private static final Logger log = Logger.getLogger(TAG);

	public static final List<String> tables = new ArrayList<String>(){{
		String sql = "CREATE TABLE SYNC_PROTOCOLS " +
			"(id INT PRIMARY KEY     AUTOINCREMENT," +
			" name           TEXT    NOT NULL ";
		add(sql);
		sql = "CREATE TABLE EXPERIMENTS " +
				"(id INT PRIMARY KEY     AUTOINCREMENT," +
				" sync_protocol  INT     NOT NULL, " +
				" FOREIGN KEY(sync_protocol) REFERENCES SYNC_PROTOCOLS(code), " +
				" NUM_PARTICIPANTS  INT     NOT NULL, " +
				" NUM_MESSAGES  INT     NOT NULL";
		add(sql);
		sql = "CREATE TABLE CHAT_USERS " +
			"(id INT PRIMARY KEY     AUTOINCREMENT," +
			" participant_no         INT    NOT NULL, " +
			" FOREIGN KEY(participant_no) REFERENCES EXPERIMENTS(id), " +
			" screen_name           TEXT    NOT NULL ";
		add(sql);
		sql = "CREATE TABLE CHAT_LOGS " +
			"(id INT PRIMARY KEY     AUTOINCREMENT," +
			" to         INT    NOT NULL, " +
			" FOREIGN KEY(to) REFERENCES CHAT_USERS(id), " +
			" from       INT    NOT NULL, " +
			" FOREIGN KEY(from) REFERENCES CHAT_USERS(id) ," +
			" message_no INT    NOT NULL, " +
			" count      INT    NOT NULL, " +
			" duplicates INT    NOT NULL, " +
			" lost       INT    NOT NULL ";
		add(sql);
	}};

	public void createTables() {
		Connection c;
		Statement stmt;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();
			for (String table : tables) {
				try {
					stmt.executeUpdate(table);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Error creating table.", e);
				}
			}
			c.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "could not find jdbc.", e);
		}
	}

	public String getStatementForInsertInSyncProtocols(String name) {
		return "INSERT INTO SYNC_PROTOCOLS (id,NAME) " +
					 " VALUES ('" + name + "');";
	}

	//TODO add allowance for start and end times.
	public String getStatementForInsertInExperiments(int syncId, int numParticipants, int numMessages) {
		return "INSERT INTO EXPERIMENTS (id,sync_protocol,num_participants,num_messages) " +
			" VALUES ('" + syncId + "," + numParticipants + "," + numMessages + "');";
	}

	public String getStatementForInsertInChatUsers(int participantNo, String screenName) {
		return "INSERT INTO CHAT_USERS (id,participant_no,screen_name) " +
			" VALUES ('" + participantNo + "," + screenName + "');";
	}

	public String getStatementForInsertInChatLogs(int to, int from, int messageNo, int count, int duplicates, int lost) {
		return "INSERT INTO CHAT_LOGS (id,to,from,message_no,count,duplicates,lost) " +
			" VALUES ('" + to +
								"," + from +
								"," + messageNo +
								"," + count +
								"," + duplicates +
								"," + lost + "');";
	}

	public void conn() {
		Connection c;
		Statement stmt;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();
			String sql = "CREATE TABLE COMPANY " +
				"(ID INT PRIMARY KEY     NOT NULL," +
				" NAME           TEXT    NOT NULL, " +
				" AGE            INT     NOT NULL, " +
				" ADDRESS        CHAR(50), " +
				" SALARY         REAL)";
			try {
				stmt.executeUpdate(sql);
			}
			catch (Exception e) {
				log.log(Level.SEVERE, "issue with createdb", e);
			}
			c.setAutoCommit(false);
			sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
				"VALUES (1, 'Paul', 32, 'California', 20000.00 );";
			stmt.executeUpdate(sql);

			sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
				"VALUES (2, 'Allen', 25, 'Texas', 15000.00 );";
			stmt.executeUpdate(sql);

			sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
				"VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );";
			stmt.executeUpdate(sql);

			sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
				"VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";
			stmt.executeUpdate(sql);

			c.commit();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );

			while ( rs.next() ) {
				int id = rs.getInt("id");
				String  name = rs.getString("name");
				int age  = rs.getInt("age");
				String  address = rs.getString("address");
				float salary = rs.getFloat("salary");

				System.out.println( "ID = " + id );
				System.out.println( "NAME = " + name );
				System.out.println( "AGE = " + age );
				System.out.println( "ADDRESS = " + address );
				System.out.println( "SALARY = " + salary );
				System.out.println();
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		System.out.println("Opened database successfully");

	}
}
