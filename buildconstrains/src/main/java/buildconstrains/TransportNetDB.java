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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import org.sqlite.JDBC;
        
//-----
class DbHandler {
	private final static String dbFile = "TransportNet.s3db";
	//public static Connection conn;
	public static Statement statementDH;
	public static PreparedStatement prepStmtDH;
	public static ResultSet resultSetDH;	
//-----------	
	private final static String JDBC_STR = "jdbc:sqlite:";
	private static String CON_STR = JDBC_STR + dbFile;
	private static DbHandler instance = null;
	static Connection connection;
	
	public static synchronized DbHandler getInstance() throws SQLException {
		//System.out.println(" DbHandler getInstance()");//TST
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }
	
	public synchronized DbHandler getInstance(String dbFile) throws SQLException {
		//System.out.println(" DbHandler getInstance(String dbFile)");//TST
        if (instance == null) {
			CON_STR = JDBC_STR + dbFile;
            //System.out.printf("CON_STR 1: %s%n",CON_STR);//TST
            instance = new DbHandler();
		}
        return instance;
    }	
	
	DbHandler() throws SQLException {
		DriverManager.registerDriver(new JDBC());
		//System.out.println(" DbHandler()");//TST
        //System.out.printf("CON_STR 0: %s%n",CON_STR);//TST
        DbHandler.connection = DriverManager.getConnection(CON_STR);
		//this.connection.setAutoCommit(false);
		DbHandler.statementDH = connection.createStatement();
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
	
	public static void set_conn_base(String dbFile) {
		DbHandler.CON_STR = JDBC_STR + dbFile;
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
		//System.out.println(" TransportNetDB()");//TST
	//	check_existence_db();
		super.getInstance();
		create_tables();
	}
	
    TransportNetDB(String dbFile) throws SQLException{
		//System.out.println(" TransportNetDB(String dbFile)");//TST
	//	check_existence_db();
		super.getInstance(dbFile);
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
			"gx    INTEGER "+
//			"gx    INTEGER, "+
//			"CONSTRAINT U_IX UNIQUE (i_net ASC,	x ASC)"+
			");"
		);
		
		create_graph_indx();
		
		statement.execute("CREATE TABLE if not exists R_STAT ("+
			"len   INTEGER PRIMARY KEY, "+
			"count INTEGER, "+
			"proc  NUMERIC (4, 1) "+
			");"
		);
		
		statement.execute("CREATE TABLE if not exists STATE ("+
			"state INTEGER (2), "+
			"i_net INTEGER, "+
			"nx    INTEGER (2), "+
			"ny    INTEGER (2)," +
			"graph_file TEXT (64)," +
			"maxlenx    INTEGER (2)," +
			"maxleny    INTEGER (2)" + 
			");"
		);

		
		statement.execute("CREATE TABLE if not exists LIMITS ( "+
			"mode          INTEGER (1), "+
			"x             INTEGER, "+
			"y             INTEGER, "+
			"beyond_border TEXT (16), "+
			"CONSTRAINT P_LIMITS PRIMARY KEY ( "+
				"mode, "+
				"x, y "+
			") "+
			");"
		);
		
		statement.close();
	}
	
	static void create_graph_indx() throws SQLException {
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS U_GRAPH_IX ON GRAPH ("+
		    "i_net ASC, "+
			"x ASC "+
			");"
		);		
		statement.close();
	}
	
	static void drop_graph_indx() throws SQLException {
		Statement statement = DbHandler.connection.createStatement();
		statement.execute("DROP INDEX U_GRAPH_IX;");
		statement.close();
	}
	
	boolean check_net(int nx, int ny) throws SQLException {
		int cnt = check_state_rows();
		if(cnt>1) return false;
		if(cnt==0) {
			Statement statement2 = DbHandler.connection.createStatement();
			statement2.execute("INSERT INTO 'STATE' ('nx', 'ny') VALUES ("+nx+","+ny+");");
			statement2.close();
			return true;
		}
		if(cnt==1) {
			Statement statement2 = DbHandler.connection.createStatement();
			ResultSet resultSet2 = statement2.executeQuery("select nx,ny from STATE");
			int b_nx = resultSet2.getInt("nx");
			int b_ny = resultSet2.getInt("ny");
			boolean result = (nx==b_nx) & (ny==b_ny);
			resultSet2.close();
			statement2.close();
			if(!result)
				System.out.printf("Others net sizes in base: %d %d !%n",b_nx,b_ny);
			return result;
		}
		System.out.println("Unknown error in db.check_net !");
        return false;
	}
	
	int check_state_rows() throws SQLException {
		Statement statement1 = DbHandler.connection.createStatement(); 
		ResultSet resultSet1 = statement1.executeQuery("select count(*) as cnt from STATE");
		int cnt = resultSet1.getInt("cnt");	
		resultSet1.close();
		statement1.close();
		return cnt;
	}
	
	static void get_limits_stat() throws SQLException {
		System.out.println(" get_limits_stat"); //TST
		
		double avgLen=0.0, sumLen=0;
		long nCount=0;
		int minLen, maxLen;
		
        try ( //resultSet = statement.executeQuery("select SUM(count*len)/SUM(count) as avg_len, SUM(count) as sum_cnt from r_stat");
                Statement statement1 = DbHandler.connection.createStatement(); 
                ResultSet resultSet1 = statement1.executeQuery(
					"select SUM(count*len) as sum_len, SUM(count) as sum_cnt, " +
					"MIN(len) as min_len, MAX(len) as max_len  from r_stat")
			) {
            //while(resultSet1.next()) {
                sumLen = resultSet1.getLong("sum_len");
                //avgLen = resultSet1.getDouble("avg_len");
                nCount = resultSet1.getLong("sum_cnt");
				minLen = resultSet1.getInt("min_len");
				maxLen = resultSet1.getInt("max_len");
            //}
			//System.out.printf(Locale.US,"sumLen,nCount: %,.2f %,d%n",sumLen,nCount); //TST
            avgLen = 1.0*sumLen/nCount;
        }
		
		double sigma;
		double d=0;
		double sumD = 0;
		int len;
		long count;
		String proc;
        try (Statement statement3 = DbHandler.connection.createStatement(); 
             Statement statement2 = DbHandler.connection.createStatement(); 
             ResultSet resultSet2 = statement2.executeQuery("select len, count from r_stat")) {
            while(resultSet2.next()) {
                len = resultSet2.getInt("len");
                count = resultSet2.getLong("count");
                d = (len - avgLen);
                //System.out.printf("d: %.2f%n", d); //TST!!!
                sumD += d*d*count;
                proc = String.format(Locale.US, "%.1f",count*100.0 / nCount);
                //System.out.printf(Locale.US,"UPDATE r_stat SET proc="+proc+" WHERE len="+len+";%n");//TST
                statement3.execute("UPDATE r_stat SET proc="+proc+" WHERE len="+len+";");
            }
        }
		if (nCount<=1) sigma = d;
		else sigma = Math.sqrt(sumD / (nCount-1.0));
		
		System.out.printf(Locale.US,"N,E,sigma,min,max: %,d %.2f %.2f %d %d%n", nCount,avgLen,sigma,minLen,maxLen);
	}
	
	static void clear() throws SQLException {
        try ( //System.out.println(" clear"); //TST
            Statement statement = DbHandler.connection.createStatement()) {
            //statement.execute("delete from state;");
            if (! Constants.check_TST(new int[]{12})) {
                statement.execute("delete from r_stat;");
            }   
			if (! Constants.check_TST(new int[]{10,12})) {
                statement.execute("delete from GRAPH;");
            }   
			statement.execute("VACUUM");
            //!!!commit();
        }
	}

//--------
// CheckResource
	static boolean limits_exists(int mode) throws SQLException {
        Statement statement1 = DbHandler.connection.createStatement(); 
        ResultSet resultSet1 = statement1.executeQuery("SELECT COUNT(*) as cnt FROM LIMITS WHERE mode="+mode+";");	
		int cnt = resultSet1.getInt("cnt");	
		return cnt > 0;
	}
	
	void limits_to_base(Limits listR, int nMode) throws SQLException {
		System.out.println(" limits_to_base"); //TST
        long ix, iy;
		int nx=0, ny=0;
		VertexSet x,y;		
        //Connection conn;
        PreparedStatement prepStmtIns;
		prepStmtIns = DbHandler.connection.prepareStatement(
						"INSERT INTO LIMITS (mode,x,y) VALUES (?,?,?);");
		
		for(int i=0; i<listR.len(); i++) {
			x = listR.get_x(i);
			y = listR.get_y(i);
            ix = x.to_int();
            iy = y.to_int();
			nx = Math.max(nx, x.len);
            ny = Math.max(ny,y.len);			
            prepStmtIns.setInt(1,nMode);
			prepStmtIns.setLong(2,ix);
			prepStmtIns.setLong(3,iy);
			prepStmtIns.execute();
        }
		
		/*if(nMode==1)*/ set_max_len_limits(nMode,nx,ny);
	}
	
	Limits read_limits(int nMode) throws SQLException {
		//System.out.println(" read_limits"); //TST
		long ix, iy;
		int nx=0, ny=0;
		VertexSet x,y;
		Statement statement = DbHandler.connection.createStatement(); 
        ResultSet resultSet = statement.executeQuery(
					"SELECT x,y FROM limits WHERE mode="+nMode+";");
		Limits rows = new Limits();
		
		while(resultSet.next()) {
			ix = resultSet.getLong("x");
			iy = resultSet.getLong("y");
			x = VertexSet.int2set(ix);
			y = VertexSet.int2set(iy);
			nx = Math.max(nx, x.len);
            ny = Math.max(ny,y.len);
			VertexSetPair pair = new VertexSetPair(x, y);
            rows.append(pair);
		}
		//System.out.printf("nx,ny: %d %d%n", nx,ny); //TST
        /*if(nMode==1)*/ set_max_len_limits(nMode,nx,ny);
        
		return rows;
	}
	
	void set_max_len_limits(int mode, int nx, int ny) throws SQLException {
		//System.out.println(" set_max_len_limits"); //TST
		if(mode==2) {
			int n=nx;
			nx=ny;
			ny=n;
		}
		int cnt = check_state_rows();
		if(cnt>1) return;
		if(cnt==0) {
			Statement statement2 = DbHandler.connection.createStatement();
			statement2.execute("INSERT INTO state (maxlenx,maxleny) VALUES ("+nx+","+ny+");");
			statement2.close();
			return;
		}
		if(cnt==1) {
			Statement statement2 = DbHandler.connection.createStatement();
			//System.out.println("UPDATE state SET maxlenx="+nx+", maxleny="+ny+";"); //TST
			statement2.execute("UPDATE state SET maxlenx="+nx+", maxleny="+ny+";");
			statement2.close();
			return;
		}
	}
	
	String get_max_len_limits() throws SQLException {
		long x,y;
		Statement statement = DbHandler.connection.createStatement(); 
        ResultSet resultSet = statement.executeQuery("SELECT maxlenx,maxleny FROM state;");	
		x = resultSet.getLong("maxlenx");
		y = resultSet.getLong("maxleny");
		return ""+x+" "+y;
	}
	
	void calc_limits(ArrayList<String> rsrcX, ArrayList<String> rsrcY,
					 int maxlenX, int maxlenY) throws SQLException {
		int mode;
		long ix, iy;
		String x,y;
		Double sx, sy;
        sx=0.0; sy=0.0;
		String opComp = " ";
		String outComp = "";
        
		Statement statement1 = DbHandler.connection.createStatement(); 
        ResultSet resultSet = statement1.executeQuery(
					"SELECT mode,x,y FROM limits ORDER BY 1,2,3;");
					
		TransportNetPrepStmt prepStmt = new TransportNetPrepStmt(this,
				"UPDATE limits SET beyond_border=? WHERE mode=? AND x=? AND y=?;");
					
		while(resultSet.next()) {
			mode = resultSet.getInt("mode");
			ix = resultSet.getLong("x");
			iy = resultSet.getLong("y");
			x = Long.toBinaryString(ix);
			y = Long.toBinaryString(iy);
			
			if(mode==1) {
				opComp= "<=";
				sx=0.0; sy=0.0;
				int nx = x.length();
				for(int i=0; i<nx; i++) 
					sx += Double.parseDouble(rsrcX.get(i)) * Character.getNumericValue(x.charAt(nx-1-i));
			
				int ny = y.length();
				for(int i=0; i<ny; i++) 
					sy += Double.parseDouble(rsrcY.get(i)) * Character.getNumericValue(y.charAt(ny-1-i));
            }
			
			if(mode==2) {
				opComp= ">=";
				sx=0.0; sy=0.0;
				int ny = y.length();
				for(int i=0; i<ny; i++) 
					sx += Double.parseDouble(rsrcX.get(i)) * Character.getNumericValue(y.charAt(ny-1-i));
			
				int nx = x.length();
				for(int i=0; i<nx; i++) 
					sy += Double.parseDouble(rsrcY.get(i)) * Character.getNumericValue(x.charAt(nx-1-i));
            }
			
			outComp = String.format(Locale.US,"%.2f%s%.2f",sx,opComp,sy);
            //System.out.printf(Locale.US,"mode,sx,sy: %d %.2f%s%.2f%n",mode,sx,opComp,sy); //TST
			
			prepStmt.save_border(mode,ix,iy,outComp);
		}
		prepStmt.close();
	}
}	
//-----
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
	
	void add_stat(Limits listR) throws SQLException {
		//System.out.println(" add_stat"); //TST
		int len = listR.len();
		//System.out.printf("listR.len: %d%n", len);
		prepStmt.setInt(1,len);
//		prepStmt.setInt(2,len);
		prepStmt.execute();
		//!!!commit();
	}
	
	void save_border(int mode, long ix, long iy, String outComp) throws SQLException {
		prepStmt.setString(1,outComp);
        prepStmt.setInt(2,mode);
		prepStmt.setLong(3,ix);
		prepStmt.setLong(4,iy);
		prepStmt.execute();		
	}
	
	void close() throws SQLException {
		this.prepStmt.close();
	}
}
//-----
//-----
class DBInsUpd {
	TransportNetDB db;
	PreparedStatement prepStmtIns;
	PreparedStatement prepStmtUpd;
	HashSet<Integer> states;
	
	DBInsUpd(TransportNetDB db, String sqlIns,  String sqlUpd) throws SQLException {
		this.db = db;
		this.prepStmtIns = db.connection.prepareStatement(sqlIns);
		this.prepStmtUpd = db.connection.prepareStatement(sqlUpd);
		this.states = new HashSet<Integer>();
	}	

	void add_stat(Limits listR) throws SQLException {
		//System.out.println(" add_stat"); //TST
		int len = listR.len();
		//System.out.printf("listR.len: %d%n", len);
		
		if (states.add(len)) {//Ins
			prepStmtIns.setInt(1,len);
			prepStmtIns.execute();
		}
		else {//Upd
			prepStmtUpd.setInt(1,len);
			prepStmtUpd.execute();
		}
		//!!!commit();
	}

	void close() throws SQLException {
		this.prepStmtIns.close();
		this.prepStmtUpd.close();
	}	
}	