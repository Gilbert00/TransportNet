package buildconstrains;

/**
 *
 * @author пользователь
 */
 //import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Date;
//import java.text.SimpleDateFormat;
//import java.util.BitSet;
//import java.util.ArrayDeque;
//import java.util.Hashtable;
//import java.util.Map;
//import java.util.Arrays;
//import java.util.Random;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.lang.Math;
//import java.lang.Comparable;
//import java.lang.invoke.MethodHandles;
//import java.lang.StringTemplate;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.math.BigInteger;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC;
        
//-----
class DbHandler {
	final static String dbFile = "TEST1.s3db";
	//public static Connection conn;
	public static Statement statement;
	public static PreparedStatement prepStmnt;
	public static ResultSet resultSet;	
//-----------	
	private static final String CON_STR = "jdbc:sqlite:TransportNet.s3db";
	private static DbHandler instance = null;
	static Connection connection;
	
	public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }
	
	DbHandler() throws SQLException {
		DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR);
		//!!!this.connection.setAutoCommit(false);
		this.statement = connection.createStatement();
    }
	
	static void commit() {
		try (Statement statement = DbHandler.connection.createStatement()) {
			statement.execute("COMMIT");
		} catch (SQLException e) {
            e.printStackTrace();
        }
	}
}

//-----
class TransportNetDB extends DbHandler {
	//TO-DO: db clearing
	//TO-DO: db init
    TransportNetDB() throws SQLException{
		super.getInstance();
	}
	
/* 	static void commit() {
		super.commit();
	} */
	
	static void add_arc_to_db(int net, int x, int gx) throws SQLException {
        //System.out.printf("%d %d %d%n", net, x, gx); //TST
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("INSERT INTO 'GRAPH' ('i_net', 'x', 'gx') VALUES ("+net+","+x+","+gx+");");
		//!!!commit();
	}
	
	static void add_stat(Limits listR) throws SQLException {
		//System.out.println(" add_stat"); //TST
		int len = listR.len();
		//System.out.printf("listR.len: %d%n", len);
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("INSERT INTO 'R_STAT' ('len', 'count') VALUES ("+len+",1) ON CONFLICT(len) DO " +
				"UPDATE SET count=count+1 WHERE len="+len+";");
		//!!!commit();		
	}
	
	static void get_limits_stat() throws SQLException {
		System.out.println(" get_limits_stat"); //TST
		
		double avgLen=0, sumLen=0;
		int nCount=0;
		
		//resultSet = statement.executeQuery("select SUM(count*len)/SUM(count) as avg_len, SUM(count) as sum_cnt from r_stat");
		resultSet = statement.executeQuery("select SUM(count*len) as sum_len, SUM(count) as sum_cnt from r_stat");
		while(resultSet.next()) {
			sumLen = resultSet.getInt("sum_len");
			//avgLen = resultSet.getDouble("avg_len");
			nCount = resultSet.getInt("sum_cnt");
		}
		avgLen = 1.0*sumLen/nCount;
		
		double sigma;
		double d=0;
		double sumD = 0;
		Statement statement = DbHandler.connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select len, count from r_stat");
		while(resultSet.next()) {
			d = (resultSet.getInt("len") - avgLen);
			//System.out.printf("d: %.2f%n", d); //TST!!!
			sumD += d*d*resultSet.getInt("count");
		}
		if (nCount<=1) sigma = d;
		else sigma = Math.sqrt(sumD / (nCount-1.0));
		
		System.out.printf("N,E,sigma: %d %.2f %.2f%n", nCount,avgLen,sigma);
	}
	
	static void clear() throws SQLException {
		//System.out.println(" clear"); //TST
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("delete from state;");
		statement.execute("delete from r_stat;");
		statement.execute("delete from GRAPH;");		
		statement.execute("VACUUM");
		//!!!commit();		
	}

}	
