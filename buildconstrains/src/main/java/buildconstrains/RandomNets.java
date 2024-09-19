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
        
/* #fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#System.out.println(fileName) */

//-------------------
//-------------------
public class RandomNets {

 private static void main1(String[] argv){

/*     String fileName = argv[0];
    System.out.printf("%s%n",fileName); */
    
// check filename for existence !  
/*     if (! Files.exists( Paths.get(fileName))) {
        System.out.printf("The input file %s doesn't exists!",fileName);
        return;
    } */
    
	int NetSize = Integer.parseInt(argv[1]);
	int CyklCount = Integer.parseInt(argv[2]);
	
	String fileName = "Test"; //!!!
	
/*     int nMode;
    if (argv.length > 1) 
        nMode = Integer.parseInt(argv[3]);
    else
        nMode = 0; */
	
	if (argv.length > 2) 
        Constants.set_TST(Integer.parseInt(argv[4]));

    //System.out.printf("mode: %d%n",nMode);
	
    Graph graph = null;
	Graph graph_dual = null;

	//!!!create_net_file(fileName, NetSize);

	graph = new Graph();
    graph.input(fileName);
    //Tst System.out.println(' after graph_input')
    System.out.printf("graph: %s%n",graph.get_graph());

	Limits listR = null;
	
    //if (nMode==1 || nMode==0)
    //    dual = False
        listR = Limits.build_net_limits(graph);
	//	listR.print_limits(graph);
	//!!!	listR.get_stat();

/*     if (nMode==2 || nMode==0){
    //    dual = True
        graph_dual = graph.create_dual();
        System.out.printf("graph_dual: %s%n",graph_dual.get_graph());
        listR = Limits.build_net_limits(graph_dual);
	//	listR.print_limits(graph_dual);
    } */

/*     if (!( nMode>=0 && nMode<=2))
        System.out.printf("Invalid mode ! %d%n",nMode); */
  }
//-- 
  public static void main(String[] argv) {
    Date current = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy_HH:mm");
    System.out.println(formatter.format(current));
	
	String className = MethodHandles.lookup().lookupClass().getSimpleName();
    if (argv.length < 3){
        System.out.println("Usage: " + className + " Input_File Net_Size Cykl_Count mode(?) TST(?)");
    }
    else {
        System.out.printf("%s.java %s%n",className,Arrays.toString(argv));
        main1(argv);
    }		
  }
}
