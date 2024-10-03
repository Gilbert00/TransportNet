package buildconstrains;

/**
 *
 * @author пользователь
 */
 //import java.time.LocalDate;
//import java.time.LocalTime;
import java.util.Date;
import java.text.SimpleDateFormat;
//import java.util.BitSet;
//import java.util.ArrayDeque;
//import java.util.Hashtable;
//import java.util.Map;
import java.util.Arrays;
//import java.util.Random;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.lang.Math;
//import java.lang.Comparable;
import java.lang.invoke.MethodHandles;
//import java.lang.StringTemplate;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;
//import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

}	

//-----
class OutToFile {
    private String fileName;
	private BufferedWriter writter;
    
    OutToFile(String name) {
        fileName = name;
		try {
			writter = new BufferedWriter(new FileWriter(fileName));
			//System.out.printf("File is opened : %s%n", fileName); //TST
		}
        catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	void out_str(String s){
		try {
			writter.write(s + "\n");
			//System.out.printf("String is writed : %s%n", s); //TST
		}
        catch (IOException e) {
			e.printStackTrace();
		}	
	}

	void close() {
		try {
			writter.close();
			//System.out.printf("File is closed : %s%n", fileName); //TST
		}
        catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
//-------------------
public class FullNet {
	final static String fileName = "FullNetTmp.csv"; 
	static OutToFile graphFile;
	static int graph_count=0;
	
	static Graph graph = null;
	//static Graph graph_dual = null;

	static TransportNetDB open_db() {
		TransportNetDB db = null;
		try {
			db = new TransportNetDB();
		}
		catch (SQLException e) {
            e.printStackTrace();
        }		
		
		return db;
	}
	
	static void setVariations(TransportNetDB db, int[] source, int nX, int variationLength) {
        int srcLength = source.length;
        int permutations = (int) Math.pow(srcLength, nX);
        System.out.printf("nX,nY,ng,nNet: %d,%d,%d,%d%n", nX,variationLength,srcLength,permutations);//TST
        
		try {
	//		TransportNetDB db = new TransportNetDB();
			
			for (int i = 0; i < nX; i++) {
				int t2 = (int) Math.pow(srcLength, i);
				for (int p1 = 0; p1 < permutations;) {
					for (int al = 0; al < srcLength; al++) {
						for (int p2 = 0; p2 < t2; p2++) {
							db.add_arc_to_db(p1, i+1, source[al]);
							p1++;
						}
					}
				}
			}	
		}
		catch (SQLException e) {
            e.printStackTrace();
        }
    }	
 
	static void read_all_graphs(TransportNetDB db) {
		try {
			db.resultSet = db.statement.executeQuery("select i_net,x,gx from graph order by 1,2");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void do_all_graphs(TransportNetDB db) {
		final int EMPTY_NET = -1;
		int i_net_old=EMPTY_NET;
		int x_old=0;
		int gx_old=0;
		int i_net;
		int x;
		int gx;
		
		System.out.println(" do_all_graphs"); //TST
		print_current_date();
		//set_new_graph_file();
		try {
			while(db.resultSet.next()) {
				i_net = db.resultSet.getInt("i_net");
				//System.out.printf("i_net_old i_net: %d %d%n", i_net_old,i_net); //TST
				
				if (i_net != i_net_old){
					if (i_net_old != EMPTY_NET) {
						close_graph_file();
						do_one_graph(db);
					}
					set_new_graph_file();
					i_net_old = i_net;
				}
				
				x = db.resultSet.getInt("x");
				gx = db.resultSet.getInt("gx");	
				//System.out.printf("x gx: %d %d%n", x,gx); //TST				
				out_graph_str(x,gx);
			}
			close_graph_file();
			do_one_graph(db);
			
			db.get_limits_stat();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	static void set_new_graph_file() {
//		graph = new Graph();
		FullNet.graphFile = new OutToFile(fileName);
	}
	
	static void close_graph_file() {
//		graph = null;
		FullNet.graphFile.close();
	}
 
	static void out_graph_str(int x, int gx) {
        String s;
		String formatInt = "%02d";		
        int iX = x;
        int iY;
        String bin;
        int lenBin;
		s = String.format(formatInt,iX);
        bin=Integer.toBinaryString(gx);
		lenBin = bin.length();
		for(int j=0; j<lenBin; j++) {
			if(bin.charAt(j)=='1') {
				iY = j+1;
				s += "," + String.format(formatInt,iY);
			}
		}
		//System.out.printf("s: %s%n", s); //TST
		FullNet.graphFile.out_str(s);
	}
 
	static void do_one_graph(TransportNetDB db) throws SQLException {
		//System.out.println(" do_one_graph"); //TST
		graph = new Graph();
		graph.input(fileName);
    //Tst System.out.println(' after graph_input')
		//System.out.printf("graph: %s%n",graph.get_graph()); //TST

		Limits listR = null;
	
		listR = Limits.build_net_limits(graph,false);
	//!!!	System.out.printf("listR len: %d%n",listR.len());
	//	listR.print_limits(graph);
		db.add_stat(listR);	

		FullNet.graph_count++;
		System.out.printf("graph_count: %d%n",FullNet.graph_count); //TST
	}
 
	static void print_current_date() {
		Date current = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
		System.out.println(formatter.format(current));	
	}
 
    static void main2(int nx, int ny) {
		int ng = (int) Math.pow(2, ny) - 1;
		int[] g = new int[ng];
		
		for(int i=0; i<ng; i++) g[i] = i+1;
		
		TransportNetDB db = open_db();

        setVariations(db,g,nx,ny);
		
		read_all_graphs(db);
		do_all_graphs(db);
    }
 
  private static void main1(String[] argv){

	int xSize = Integer.parseInt(argv[0]);
	int ySize = Integer.parseInt(argv[1]);
//	int CyklCount = Integer.parseInt(argv[2]);
	
	final String fileName = "FullNetTmp.csv"; 
	
/*     int nMode;
    if (argv.length > 1) 
        nMode = Integer.parseInt(argv[2]);
    else
        nMode = 0; */
	
	if (argv.length > 2) 
        Constants.set_TST(Integer.parseInt(argv[3]));

	main2(xSize,ySize);

    //System.out.printf("mode: %d%n",nMode);
	
    //!!!Graph graph = null;
	//!!!Graph graph_dual = null;

	//!!!create_net_file(fileName, NetSize);

	//return; //TST!!!
	
	//!!!graph = new Graph();
    //!!!graph.input(fileName);
    //Tst System.out.println(' after graph_input')
    //!!!System.out.printf("graph: %s%n",graph.get_graph());

	//!!!Limits listR = null;
	
    //if (nMode==1 || nMode==0) {
    //    dual = False
    //!!!    listR = Limits.build_net_limits(graph);
	//!!!	System.out.printf("listR len: %d%n",listR.len());
	//	listR.print_limits(graph);
	//!!!	listR.get_stat();
	//}
  }
//-- 
  public static void main(String[] argv) {
/*     Date current = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
    System.out.println(formatter.format(current)); */
	print_current_date();
	
	String className = MethodHandles.lookup().lookupClass().getSimpleName();
    if (argv.length < 2){
        System.out.println("Usage: " + className + " X_Size Y_Size TST(?)");
    }
    else {
        System.out.printf("%s.java %s%n",className,Arrays.toString(argv));
        main1(argv);
    }		
  }
}
