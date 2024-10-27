package buildconstrains;

/**
 *
 * @author пользователь
 */
 //import java.time.LocalDate;
//import java.time.LocalTime;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
//import java.util.BitSet;
//import java.util.ArrayDeque;
//import java.util.Hashtable;
//import java.util.Map;
import java.util.Arrays;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.math.BigInteger;
        
//-------------------
//-------------------
public class CheckResource {
	static ArrayList<String> rsrcX;
	static ArrayList<String> rsrcY;
	static int maxlenX;
	static int maxlenY;
	
	static TransportNetDB open_db(String dbFile) {
		DbHandler.set_conn_base(dbFile);
		
		TransportNetDB db = null;
		//System.out.println(" open_db");//TST
		try {
			db = new TransportNetDB(dbFile);
			//db.clear();
		}
		catch (SQLException e) {
            e.printStackTrace();
        }		
		
		return db;
	}
//--------
// CheckResource	
    private static final String COMMA_DELIMITER = ",";

    private static boolean get_resource(String fileName){
        try {
			String rowX, rowY;
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			rowX = br.readLine();
			if (rowX.trim().length() == 0) {
				System.out.printf("row X don't exists in %s%n",fileName);
				return false;
			}
			CheckResource.rsrcX = set_rsrc(rowX);
			
			rowY = br.readLine();
			if (rowY.trim().length() == 0) {
				System.out.printf("row Y don't exists in %s%n",fileName);
				return false;
			}			
			CheckResource.rsrcY = set_rsrc(rowY);
			
			System.out.printf("rsrcX: %s%n",rsrcX);
			System.out.printf("rsrcY: %s%n",rsrcY);
			
			return true;
			
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        return true;
    }

	private static ArrayList<String> set_rsrc(String row) {
		String[] val = row.split(COMMA_DELIMITER);
		ArrayList<String> vals = new ArrayList<>();
		for(int i=0; i<val.length; i++){
			vals.add(val[i]);
		}		
		return vals;
	}
	
	private static boolean check_resource_len(TransportNetDB db, int rX, int rY)  throws SQLException {
		int x,y;
		String xy = db.get_max_len_limits();
		String[] vals = xy.split(" ");
		x = Integer.parseInt(vals[0]);
		y = Integer.parseInt(vals[1]);
		if(x==rX & y==rY) {
			CheckResource.maxlenX = x;
			CheckResource.maxlenY = y;
			return true; 
		}
		
		System.out.printf("There should be %d and %d values in input file, but really: %d and %d%n",x,y,rX,rY);
        return false;
	}
	
	private static void calc_limits(TransportNetDB db) throws SQLException {
		db.calc_limits(rsrcX,rsrcY, CheckResource.maxlenX, CheckResource.maxlenY);
	}
	
//--------
// CheckResource
	static void calc_resource(TransportNetDB db, Limits listR, String fileResource) throws SQLException {
		if(!get_resource(fileResource)) return;
        try {
            if(!check_resource_len(db,rsrcX.size(),rsrcY.size())) return;
            calc_limits(db);
            //out_calc_result(db);
        } catch (SQLException ex) {
            Logger.getLogger(CheckResource.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
//--------
// CheckResource		
	static void print_current_date() {
		Date current = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
		System.out.println(formatter.format(current));	
	}
//--------
// CheckResource
 private static void main1(String[] argv) throws SQLException{

    String fileGraph = argv[0];
    System.out.printf("%s%n",fileGraph);
// check fileGraph for existence !  
    if (! Files.exists( Paths.get(fileGraph))) {
        System.out.printf("The graph file %s doesn't exists!",fileGraph);
        return;
    }

    String fileResource = argv[1];
    System.out.printf("%s%n",fileResource);
    if (! Files.exists( Paths.get(fileResource))) {
        System.out.printf("The resorce file %s doesn't exists!",fileResource);
        return;
    }
    
    int nMode, modeReal;
    if (argv.length > 2) 
        nMode = Integer.parseInt(argv[2]);
    else
        nMode = 0;
	
	if (argv.length > 3) 
        Constants.set_TST(Integer.parseInt(argv[3]));

    System.out.printf("mode: %d%n",nMode);
	
    Graph graph = new Graph();

    graph.input(fileGraph);
    //Tst System.out.println(' after graph_input')
    System.out.printf("graph: %s%n",graph.get_graph());

	Limits listR = null;
	
	String dbFile = fileGraph + ".s3db";
	TransportNetDB db = open_db(dbFile);
	
	modeReal = 1;
    if (nMode==modeReal || nMode==0) {
    //    dual = False
		if(! db.limits_exists(modeReal)) {
			listR = Limits.build_net_limits(graph,true);
			listR.print_limits(graph);
			db.limits_to_base(listR,modeReal);
		}
		else {
			listR = db.read_limits(modeReal);
			//listR.print_list_xy("listR");//TST
			System.out.printf("mode,listR: %d %s%n", modeReal,listR.out_list());//TST
		}
		
	}

	modeReal = 2;
    if (nMode==modeReal || nMode==0){
    //    dual = True
		if(! db.limits_exists(modeReal)) {
			Graph graph_dual = graph.create_dual();
			System.out.printf("graph_dual: %s%n",graph_dual.get_graph());
			listR = Limits.build_net_limits(graph_dual,true);
			listR.print_limits(graph_dual);
			db.limits_to_base(listR,modeReal);
		}
		else {
			listR = db.read_limits(modeReal);
			//listR.print_list_xy("listR");//TST
			System.out.printf("mode,listR: %d %s%n", modeReal,listR.out_list());//TST
		}
		
    }

    if (!( nMode>=0 && nMode<=2)) {
        System.out.printf("Invalid mode ! %d%n",nMode);
	}
	else
		calc_resource(db, listR, fileResource);
	
	TransportNetDB.close();
	//return;
  }
 
  public static void main(String[] argv) throws SQLException {
 
	print_current_date();
	
	String className = MethodHandles.lookup().lookupClass().getSimpleName();
    if (argv.length == 0){
        System.out.println("Usage: " + className + " Graph_File Resorce_File" + " mode(?) TST(?)");
    }
    else {
        System.out.printf("%s.java %s%n",className,Arrays.toString(argv));
        main1(argv);
    }		
  }
}
