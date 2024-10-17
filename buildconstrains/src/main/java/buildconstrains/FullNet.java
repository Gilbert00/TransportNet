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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.JDBC;
       
//-------------------
public class FullNet {
	final static String fileName = "FullNetTmp.csv"; 
//	static OutToFile graphFile;
	static BufferFile graphFile;
	static int graph_count=0;
	
	static Graph graph = null;
	//static Graph graph_dual = null;

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
//--------
// FullNet	
	static void setVariations(TransportNetDB db, int[] source, int nX, int variationLength) {
        int srcLength = source.length;
        int permutations = (int) Math.pow(srcLength, nX);
		System.out.println(" setVariations"); //TST
        System.out.printf("nX,nY,ng,nNet: %d,%d,%d,%d%n", nX,variationLength,srcLength,permutations);//TST
        
		try {
	//		TransportNetDB db = new TransportNetDB();
	
			db.drop_graph_indx();
			
			TransportNetPrepStmt prepstmt = new TransportNetPrepStmt(db, 
				"INSERT INTO 'GRAPH' ('i_net', 'x', 'gx') VALUES (?,?,?);");
			
			for (int i = 0; i < nX; i++) {
				int t2 = (int) Math.pow(srcLength, i);
				for (int p1 = 0; p1 < permutations;) {
					for (int al = 0; al < srcLength; al++) {
						for (int p2 = 0; p2 < t2; p2++) {
							prepstmt.add_arc_to_db(p1, i+1, source[al]);
							p1++;
						}
					}
				}
			}
			prepstmt.close();
			
			System.out.println(" create_graph_indx"); //TST
			print_current_date();
			db.create_graph_indx();
		}
		catch (SQLException e) {
            e.printStackTrace();
        }
    }	
 
/* 	static void read_all_graphs(TransportNetDB db) {
		try {
			db.resultSetDH = db.statementDH.executeQuery("select i_net,x,gx from graph order by 1,2");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	} */
//--------
// FullNet	
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
			Statement statement = db.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select i_net,x,gx from graph order by 1,2");
					
/* 			DBInsUpd prepstmt = new DBInsUpd(db,
										"INSERT INTO 'R_STAT' ('len', 'count') VALUES (?,1);",
										"UPDATE 'R_STAT' SET count=count+1 WHERE len=?;"
									); */
			TransportNetPrepStmt prepstmt = new TransportNetPrepStmt(db, 
				"INSERT INTO 'R_STAT' ('len', 'count') VALUES (?,1) ON CONFLICT(len) DO " +
				"UPDATE SET count=count+1;");
				//"UPDATE SET count=count+1 WHERE len=?;");

			
			while(resultSet.next()) {
				i_net = resultSet.getInt("i_net");
				//System.out.printf("i_net_old i_net: %d %d%n", i_net_old,i_net); //TST
				
				if (i_net != i_net_old){
					if (i_net_old != EMPTY_NET) {
						close_graph_file();
						do_one_graph(db,prepstmt);
					}
					set_new_graph_file();
					i_net_old = i_net;
				}
				
				x = resultSet.getInt("x");
				gx = resultSet.getInt("gx");	
				//System.out.printf("x gx: %d %d%n", x,gx); //TST				
				out_graph_str(x,gx);
			}
			close_graph_file();
			do_one_graph(db,prepstmt);
			System.out.printf("graph_count: %d%n",FullNet.graph_count); //TST
			resultSet.close();
			statement.close();
			prepstmt.close();
			
			db.get_limits_stat();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}		
	}
//--------
// FullNet	
	static void set_new_graph_file() {
//		graph = new Graph();
		//FullNet.graphFile = new OutToFile(fileName);
		FullNet.graphFile = BufferFile.getBuffer();
	}
//--------
// FullNet	
	static void close_graph_file() {
//		graph = null;
		FullNet.graphFile.close();
	}
//--------
// FullNet 
	static void out_graph_str(int x, int gx) {
        //String s;
		String formatInt = "%02d";		
        int iX = x;
        int iY;
        String bin;
        int lenBin;
		//s = String.format(formatInt,iX);
		String s = "";
        bin=Integer.toBinaryString(gx);
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
		FullNet.graphFile.out_str(s);
	}
//--------
// FullNet 
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

		FullNet.graph_count++;
		//System.out.printf("graph_count: %d%n",FullNet.graph_count); //TST
	}
//--------
// FullNet 
	static void print_current_date() {
		Date current = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
		System.out.println(formatter.format(current));	
	}
//--------
// FullNet 
    static void main2(int nx, int ny) {
		
        try {
            TransportNetDB db = open_db();
            
            if (! Constants.check_TST(new int[]{10})) {
                int ng = (int) Math.pow(2, ny) - 1;
                int[] g = new int[ng];
                
                for(int i=0; i<ng; i++) g[i] = i+1;
                
                setVariations(db,g,nx,ny);
            }
            //read_all_graphs(db);
            do_all_graphs(db);
			print_current_date();
            TransportNetDB.close();
        } catch (SQLException ex) {
            Logger.getLogger(FullNet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//--------
// FullNet 
  private static void main1(String[] argv){

	int xSize = Integer.parseInt(argv[0]);
	int ySize = Integer.parseInt(argv[1]);
	
	final String fileName = "FullNetTmp.csv"; 
	
	if (argv.length > 2) 
        Constants.set_TST(Integer.parseInt(argv[2]));

	main2(xSize,ySize);

  }
//--------
// FullNet
  public static void main(String[] argv) {

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
