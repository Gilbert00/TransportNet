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
import java.util.Locale;
import org.sqlite.JDBC;
        
//-----
class DbHandler {
	final static String dbFile = "TransportNet.s3db";
	//public static Connection conn;
	public static Statement statementDH;
	public static PreparedStatement prepStmtDH;
	public static ResultSet resultSetDH;	
//-----------	
	private static final String CON_STR = "jdbc:sqlite:"+dbFile;
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
		//this.connection.setAutoCommit(false);
		this.statementDH = connection.createStatement();
		Statement statement = connection.createStatement();
		statement.execute("PRAGMA synchronous = OFF");
		//connection.commit();
		statement.execute("PRAGMA journal_mode = OFF");
		//connection.commit();
		statement.execute("PRAGMA temp_store = MEMORY");
		//connection.commit();
		statement.execute("PRAGMA cache_size = 1000000");
		//connection.commit();
    }
	
	static void commit() {
		try (Statement statement = DbHandler.connection.createStatement()) {
			statement.execute("COMMIT");
            statement.close();
		} catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	static void close() throws SQLException {
		statementDH.close();
		connection.close();
	}
}

//-----
class TransportNetDB extends DbHandler {
	//DONE: db clearing
	//DONE: db create
    TransportNetDB() throws SQLException{
	//	check_existence_db();
		super.getInstance();
		create_tables();
	}
	
/* 	static void commit() {
		super.commit();
	} */

	static void create_tables() throws SQLException {
        //System.out.printf(" create_tables"); //TST
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("CREATE TABLE if not exists GRAPH ("+
			"i_net INTEGER, "+
			"x     INTEGER (2), "+
			"gx    INTEGER, "+
			"CONSTRAINT U_IX UNIQUE (i_net ASC,	x ASC)"+
			");"
		);
		statement.execute("CREATE TABLE if not exists R_STAT ("+
			"len   INTEGER PRIMARY KEY, "+
			"count INTEGER, "+
			"proc  NUMERIC (4, 1) "+
			");"
		);
		statement.execute("CREATE TABLE if not exists STATE ("+
			"state INTEGER (2), "+
			"i_net INTEGER "+
			");"
		);	
		statement.close();
	}
	
	static void add_stat(Limits listR) throws SQLException {
		//System.out.println(" add_stat"); //TST
		int len = listR.len();
		//System.out.printf("listR.len: %d%n", len);
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("INSERT INTO 'R_STAT' ('len', 'count') VALUES ("+len+",1) ON CONFLICT(len) DO " +
				"UPDATE SET count=count+1 WHERE len="+len+";");
		//!!!commit();
		statement.close();
	}
	
	static void get_limits_stat() throws SQLException {
		System.out.println(" get_limits_stat"); //TST
		
		double avgLen=0, sumLen=0;
		int nCount=0;
		
		//resultSet = statement.executeQuery("select SUM(count*len)/SUM(count) as avg_len, SUM(count) as sum_cnt from r_stat");
		Statement statement1 = DbHandler.connection.createStatement();
		ResultSet resultSet1 = statement1.executeQuery("select SUM(count*len) as sum_len, SUM(count) as sum_cnt from r_stat");
		while(resultSet1.next()) {
			sumLen = resultSet1.getInt("sum_len");
			//avgLen = resultSet1.getDouble("avg_len");
			nCount = resultSet1.getInt("sum_cnt");
		}
		avgLen = 1.0*sumLen/nCount;
		resultSet1.close();
		statement1.close();
		
		double sigma;
		double d=0;
		double sumD = 0;
		int len;
		int count;
		String proc;
		Statement statement3 = DbHandler.connection.createStatement();
		Statement statement2 = DbHandler.connection.createStatement();
		ResultSet resultSet2 = statement2.executeQuery("select len, count from r_stat");
		while(resultSet2.next()) {
			len = resultSet2.getInt("len");
			count = resultSet2.getInt("count");
			d = (len - avgLen);
			//System.out.printf("d: %.2f%n", d); //TST!!!
			sumD += d*d*count;
			proc = String.format(Locale.US, "%.1f",count*100.0 / nCount);
            //System.out.printf(Locale.US,"UPDATE r_stat SET proc="+proc+" WHERE len="+len+";%n");//TST
			statement3.execute("UPDATE r_stat SET proc="+proc+" WHERE len="+len+";");
		}
		resultSet2.close();
		statement2.close();
		statement3.close();
		if (nCount<=1) sigma = d;
		else sigma = Math.sqrt(sumD / (nCount-1.0));
		
		System.out.printf("N,E,sigma: %d %.2f %.2f%n", nCount,avgLen,sigma);
	}
	
	static void clear() throws SQLException {
		//System.out.println(" clear"); //TST
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("delete from state;");
		statement.execute("delete from r_stat;");
		if (! Constants.check_TST(new int[]{10})) {
			statement.execute("delete from GRAPH;");
		}
		statement.execute("VACUUM");
		//!!!commit();
		statement.close();
	}

}	
//-----
class TransportNetPrepStmt{
	TransportNetDB db;
	//Statement statement;
	PreparedStatement prepStmt;
	//ResultSet resultSet;		
	
	TransportNetPrepStmt(TransportNetDB db, String sql) throws SQLException {
		this.db = db;
		this.prepStmt = db.connection.prepareStatement(sql);
	}
	
	void add_arc_to_db(int net, int x, int gx) throws SQLException {
        //System.out.printf("%d %d %d%n", net, x, gx); //TST
		prepStmt.setInt(1,net);
		prepStmt.setInt(2,x);
		prepStmt.setInt(3,gx);
		prepStmt.execute();
	}
	
	void close() throws SQLException {
		this.prepStmt.close();
	}
}