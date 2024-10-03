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
        
/* #fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#System.out.println(fileName) */

/* class OutToFile {
    String fileName;
	BufferedWriter writter;
    
    OutToFile(String name) {
        fileName = name;
		try {
			writter = new BufferedWriter(new FileWriter(fileName));
		}
        catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	void out_str(String s){
		try {
			writter.write(s + "\n");
		}
        catch (IOException e) {
			e.printStackTrace();
		}	
	}

	void close() {
		try {
			writter.close();
		}
        catch (IOException e) {
			e.printStackTrace();
		}	
	}
} */

//-------------------
//-------------------
public class RandomNets {

    static void create_net_file(String fileName, int netSize) {
        int upBound = (int)Math.pow(2,netSize) - 1;
        final Random random = new Random();
        
        int currNetY;
        String binNetY;
        int lenBin;
        int iX;
        int iY;
        int i,j;
        String s;
		final String formatInt = "%02d";
		OutToFile file = new OutToFile(fileName);
        for(i=0; i<netSize; i++) {
            currNetY = random.nextInt(upBound) + 1;
            binNetY = Integer.toBinaryString(currNetY); 
            iX = i+1;
            //s = Integer.toString(iX);
			s = String.format(formatInt,iX);
            lenBin = binNetY.length();
            for(j=0; j<lenBin; j++) {
                if(binNetY.charAt(j)=='1') {
                    iY = j+1;
                    s += "," + String.format(formatInt,iY);
                }
            }
            file.out_str(s);
        }
		file.close();
    }   
    
 private static void main1(String[] argv){

/*     String fileName = argv[0];
    System.out.printf("%s%n",fileName); */
    
// check filename for existence !  
/*     if (! Files.exists( Paths.get(fileName))) {
        System.out.printf("The input file %s doesn't exists!",fileName);
        return;
    } */
    
	int NetSize = Integer.parseInt(argv[0]);
	int CyklCount = Integer.parseInt(argv[1]);
	
	String fileName = "Test.csv"; //!!!
	
/*     int nMode;
    if (argv.length > 1) 
        nMode = Integer.parseInt(argv[2]);
    else
        nMode = 0; */
	
	if (argv.length > 2) 
        Constants.set_TST(Integer.parseInt(argv[3]));

    //System.out.printf("mode: %d%n",nMode);
	
    Graph graph = null;
	Graph graph_dual = null;

	create_net_file(fileName, NetSize);

	//return; //TST!!!
	
	graph = new Graph();
    graph.input(fileName);
    //Tst System.out.println(' after graph_input')
    System.out.printf("graph: %s%n",graph.get_graph());

	Limits listR = null;
	
    //if (nMode==1 || nMode==0) {
    //    dual = False
        listR = Limits.build_net_limits(graph,false);
		System.out.printf("listR len: %d%n",listR.len());
	//	listR.print_limits(graph);
	//!!!	listR.get_stat();
	//}
	
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
    if (argv.length < 2){
        System.out.println("Usage: " + className + " Net_Size Cykl_Count mode(?) TST(?)");
    }
    else {
        System.out.printf("%s.java %s%n",className,Arrays.toString(argv));
        main1(argv);
    }		
  }
}
