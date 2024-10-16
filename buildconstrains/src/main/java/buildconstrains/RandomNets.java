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
import java.util.Random;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
        
/* #fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#System.out.println(fileName) */

//-------------------
//-------------------
public class RandomNets {
	static BufferFile graphFile;
	static int graph_count=0;
	
	static Graph graph = null;
	
//-----------
// RandomNets 	
	static TransportNetDB open_db() {
		TransportNetDB db = null;
		try {
			db = new TransportNetDB();
			db.clear();
		}
		catch (SQLException e) {
            e.printStackTrace();
        }		
		
		return db;
	}	
//-----------
// RandomNets	
	static void do_all_graphs(TransportNetDB db, int nx, int ny, int ncykl) throws SQLException {
		int ix;
		long gx;
		int i;
		
		System.out.println(" do_all_graphs"); //TST
		print_current_date();

		if(!db.check_net(nx,ny)) return;
		
		long upBound = (long)Math.pow(2,ny) - 1;
        final Random random = new Random();
		
		System.out.printf(Locale.US,"nX nY ng nNet: %d %d %d %,d%n", nx,ny,upBound,ncykl);//TST
		
		try {
			//Statement statement = db.connection.createStatement();
			//ResultSet resultSet = statement.executeQuery("select i_net,x,gx from graph order by 1,2");
					
/* 			DBInsUpd prepstmt = new DBInsUpd(db,
										"INSERT INTO 'R_STAT' ('len', 'count') VALUES (?,1);",
										"UPDATE 'R_STAT' SET count=count+1 WHERE len=?;"
									); */
			TransportNetPrepStmt prepstmt = new TransportNetPrepStmt(db, 
				"INSERT INTO 'R_STAT' ('len', 'count') VALUES (?,1) ON CONFLICT(len) DO " +
				"UPDATE SET count=count+1;");
				//"UPDATE SET count=count+1 WHERE len=?;");

			for(int ic=0; ic<ncykl; ic++) {
				set_new_graph_file();
				for(i=0; i<nx; i++) {
					ix = i+1;
					gx = random.nextLong(upBound) + 1;
					out_graph_str(ix,gx);
				}
				close_graph_file();
				do_one_graph(db,prepstmt);
			}
			//statement.close();
			prepstmt.close();
			System.out.printf("graph_count: %d%n",RandomNets.graph_count); //TST
			db.get_limits_stat();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}		
	}
//-----------
// RandomNets	
	static void set_new_graph_file() {
//		graph = new Graph();
		//RandomNets.graphFile = new OutToFile(fileName);
		RandomNets.graphFile = BufferFile.getBuffer();
	}
//-----------
// RandomNets	
	static void close_graph_file() {
//		graph = null;
		RandomNets.graphFile.close();
	}
//-----------
// RandomNets 
	static void out_graph_str(int x, long gx) {
        //String s;
		String formatInt = "%02d";		
        int iX = x;
        int iY;
        String bin;
        int lenBin;
		//s = String.format(formatInt,iX);
		String s = "";
        bin=Long.toBinaryString(gx);
		lenBin = bin.length();
		if (Constants.check_TST(new int[]{6})) System.out.printf("X,gx,bin,lenBin: %d %d %s %d%n", iX,gx,bin,lenBin); //TST
		for(int j=0; j<lenBin; j++) {
			//System.out.printf("j,char: %d %s%n", j,bin.charAt(j)); //TST
			if(bin.charAt(j)=='1') {
				iY = lenBin - j;
				//System.out.printf("j,iY: %d %d%n", j,iY); //TST
				s = "," + String.format(formatInt,iY) + s;
			}
		}
		s = String.format(formatInt,iX) + s;
		if (Constants.check_TST(new int[]{6})) System.out.printf("s: %s%n", s); //TST
		RandomNets.graphFile.out_str(s);
	}
//-----------
// RandomNets 
//	static void do_one_graph(TransportNetDB db, DBInsUpd prepstmt) throws SQLException {
	static void do_one_graph(TransportNetDB db, TransportNetPrepStmt prepstmt) throws SQLException {
		//System.out.println(" do_one_graph"); //TST
		graph = new Graph();
		//graph.input(fileName);
		graph.input_buffer(graphFile);
		//System.out.println(" after input_buffer"); //TST
		if (Constants.check_TST(new int[]{6})) System.out.printf("graph: %s%n",graph.get_graph()); //TST

		Limits listR = null;
	
		boolean bListRout = (Constants.check_TST(new int[]{6}));
		listR = Limits.build_net_limits(graph,bListRout);
	//!!!	System.out.printf("listR len: %d%n",listR.len());
	//	listR.print_limits(graph);
		prepstmt.add_stat(listR);	

		RandomNets.graph_count++;
		//System.out.printf("graph_count: %d%n",RandomNets.graph_count); //TST
	}	
//-----------	
// RandomNets 
	static void print_current_date() {
		Date current = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
		System.out.println(formatter.format(current));	
	}
//-----------
// RandomNets  
    static void main2(int nx, int ny, int ncykl) {
		
        try {
            TransportNetDB db = open_db();
            do_all_graphs(db,nx,ny,ncykl);
			print_current_date();
            TransportNetDB.close();
        } catch (SQLException ex) {
            Logger.getLogger(RandomNets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//-----------	
// RandomNets     
	private static void main1(String[] argv){

		//int nNetSize = Integer.parseInt(argv[0]);
		//int xSize = nNetSize;
		//int ySize = nNetSize;
		int xSize = Integer.parseInt(argv[0]);
		int ySize = Integer.parseInt(argv[1]);
		int nCyklCount = Integer.parseInt(argv[2]);

		
		String fileName = "Test.csv"; //!!!
		
		if (argv.length > 3) 
			Constants.set_TST(Integer.parseInt(argv[3]));

		main2(xSize,ySize,nCyklCount);
	}
//-----------	
// RandomNets 
  public static void main(String[] argv) {
	
	print_current_date();
	
	String className = MethodHandles.lookup().lookupClass().getSimpleName();
    if (argv.length < 3){
        System.out.println("Usage: " + className + " X_Size Y_Size Cykl_Count TST(?)");
    }
    else {
        System.out.printf("%s.java %s%n",className,Arrays.toString(argv));
        main1(argv);
    }		
  }
}
