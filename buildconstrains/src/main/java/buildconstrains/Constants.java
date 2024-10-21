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
        
/* import os
import sys, getopt
import datetime #as dt
import queue
import csv
#import networkx as nx
#G = nx.Graph()
import numpy as np */

/* #fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#System.out.println(fileName) */
//--------

class Constants{
    //static int EMPTY_EL=-1;
    static int TST=0; //!!! [1,2,4,5]
	
	enum TSTvalue{
		NoTst(0),
		TstMatrG(1),
		TstGraph(2),
		TstBuildLimits(4),
		TstPrintLimits(5),
		TstBuildGraph(6),
		DontClearGraph(10),
		OutAll(11),
		DontClearRStat(12);
		
		private int index;
		
		private TSTvalue(int ind){
			this.index = ind;
		}
		
		int getTSTvalue(){ return this.index;}
	}
	
	static boolean check_TST(int[] inds) {
		for(int i=0; i<inds.length; i++) {
			//System.out.printf("TSTi: %d%n",inds[i]);//TST
			if (TST==inds[i]) return true;
		}
		return false;
	}
	
	static void set_TST(int val){
		TST = val;
		//System.out.printf("TST: %d%n",TST);//TST
	}
}
