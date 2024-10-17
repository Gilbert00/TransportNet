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
import java.util.ArrayList;
/* import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC; */

class BufferFile {
    private static BufferFile buffer = null;
	private static int iLineRead = -1;
	private static ArrayList<String> file ;
	    
    public static BufferFile getBuffer() {
		if (buffer == null) {
            buffer = new BufferFile();
		}
		file.clear();
		initRead();
		return buffer;
    }
    
	BufferFile() {
		file = new ArrayList<>();
		//initRead();
	}
	
	private static void initRead() {
		BufferFile.iLineRead = -1;
	}
	
	void out_str(String s){
		file.add(s);
	}

	String read_line() {
		BufferFile.iLineRead++;
		//System.out.printf("iLineRead,size: %d %d%n",iLineRead,file.size()); //TST
		if (BufferFile.iLineRead == file.size()) {
			//initRead();
			return "";
		}
		else return file.get(BufferFile.iLineRead);
		
	}

	void close() {
		//file.clear();
		//initParam();
		//return;
	}
}