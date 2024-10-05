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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
/* import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC; */

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